import { Injectable, signal, computed, effect, inject } from '@angular/core';
import { User, CreateUserDto, UpdateUserDto } from '../../../core/models/user.model';
import { UserStorageService } from '../../../core/services/user-storage.service';

/**
 * Servicio principal para gestión de usuarios.
 * 
 * Este servicio proporciona la lógica de negocio para el CRUD de usuarios
 * utilizando Signals de Angular para reactividad automática.
 * 
 * @class UserService
 * @injectable
 * @description
 * Capa de lógica de negocio que orquesta las operaciones CRUD,
 * maneja el estado reactivo con Signals, y coordina con el
 * servicio de almacenamiento para persistencia.
 * 
 * @example
 * ```typescript
 * constructor(private userService: UserService) {
 *   effect(() => {
 *     console.log('Usuarios actuales:', this.userService.users());
 *   });
 * }
 * ```
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  /**
   * Servicio de almacenamiento inyectado.
   * @private
   * @readonly
   */
  private readonly storageService = inject(UserStorageService);

  /**
   * Signal privado que contiene el array de usuarios.
   * @private
   */
  private readonly usersSignal = signal<User[]>([]);

  /**
   * Signal público de solo lectura con todos los usuarios.
   * Se actualiza automáticamente cuando cambia usersSignal.
   * 
   * @readonly
   * @returns {Signal<User[]>} Signal con el array de usuarios.
   * 
   * @example
   * ```typescript
   * // En un componente
   * users = this.userService.users;
   * 
   * // En el template
   * @for (user of users(); track user.id) {
   *   <div>{{ user.nombre }}</div>
   * }
   * ```
   */
  readonly users = this.usersSignal.asReadonly();

  /**
   * Signal computado que indica si hay usuarios.
   * 
   * @readonly
   * @returns {Signal<boolean>} true si hay al menos un usuario.
   * 
   * @example
   * ```typescript
   * @if (hasUsers()) {
   *   <p>Hay usuarios disponibles</p>
   * } @else {
   *   <p>No hay usuarios</p>
   * }
   * ```
   */
  readonly hasUsers = computed(() => this.usersSignal().length > 0);

  /**
   * Signal computado con el total de usuarios.
   * 
   * @readonly
   * @returns {Signal<number>} Cantidad total de usuarios.
   * 
   * @example
   * ```typescript
   * <p>Total de usuarios: {{ userCount() }}</p>
   * ```
   */
  readonly userCount = computed(() => this.usersSignal().length);

  constructor() {
    // Cargar usuarios desde localStorage al inicializar
    this.loadUsers();

    // Effect para sincronizar cambios con localStorage automáticamente
    effect(() => {
      const currentUsers = this.usersSignal();
      if (currentUsers.length > 0 || this.storageService.getAll().length > 0) {
        // Solo sincronizar si hay cambios pendientes
        // Este effect se ejecuta en cada cambio del signal
      }
    });
  }

  /**
   * Carga todos los usuarios desde localStorage.
   * 
   * @returns {void}
   * 
   * @example
   * ```typescript
   * this.userService.loadUsers();
   * ```
   */
  loadUsers(): void {
    const users = this.storageService.getAll();
    this.usersSignal.set(users);
  }

  /**
   * Obtiene un usuario por su ID.
   * 
   * @param {string} id - ID del usuario a buscar.
   * @returns {User | undefined} El usuario encontrado o undefined si no existe.
   * 
   * @example
   * ```typescript
   * const user = this.userService.getUserById('abc123');
   * if (user) {
   *   console.log(`Usuario: ${user.nombre} ${user.apellido}`);
   * }
   * ```
   */
  getUserById(id: string): User | undefined {
    return this.usersSignal().find(user => user.id === id);
  }

  /**
   * Crea un nuevo usuario.
   * 
   * @param {CreateUserDto} userData - Datos del usuario a crear.
   * @returns {User} El usuario creado con su ID generado.
   * @throws {Error} Si el email ya existe o si hay error al guardar.
   * 
   * @example
   * ```typescript
   * const newUser = this.userService.createUser({
   *   nombre: 'Juan',
   *   apellido: 'Pérez',
   *   email: 'juan@example.com',
   *   telefono: '+57 300 123 4567',
   *   fechaNacimiento: new Date('1990-01-01')
   * });
   * console.log(`Usuario creado con ID: ${newUser.id}`);
   * ```
   */
  createUser(userData: CreateUserDto): User {
    // Validar email único
    if (this.storageService.emailExists(userData.email)) {
      throw new Error('El email ya está registrado en el sistema');
    }

    // Validar campos requeridos
    this.validateUserData(userData);

    // Crear usuario con ID generado
    const newUser: User = {
      ...userData,
      id: this.generateId()
    };

    // Guardar en localStorage
    this.storageService.save(newUser);

    // Actualizar signal
    this.usersSignal.update(users => [...users, newUser]);

    return newUser;
  }

  /**
   * Actualiza un usuario existente.
   * 
   * @param {UpdateUserDto} userData - Datos a actualizar (debe incluir id).
   * @returns {User} El usuario actualizado.
   * @throws {Error} Si el usuario no existe, el email ya existe, o hay error al actualizar.
   * 
   * @example
   * ```typescript
   * const updated = this.userService.updateUser({
   *   id: 'abc123',
   *   email: 'nuevo@example.com',
   *   telefono: '+57 310 456 7890'
   * });
   * ```
   */
  updateUser(userData: UpdateUserDto): User {
    const { id, ...updates } = userData;

    // Verificar que el usuario existe
    const existingUser = this.getUserById(id);
    if (!existingUser) {
      throw new Error('Usuario no encontrado');
    }

    // Si se actualiza el email, validar que no exista en otro usuario
    if (updates.email && this.storageService.emailExists(updates.email, id)) {
      throw new Error('El email ya está registrado en otro usuario');
    }

    // Validar datos si se proporcionan
    if (Object.keys(updates).length > 0) {
      this.validateUserData(updates);
    }

    // Actualizar en localStorage
    const updatedUser = this.storageService.update(id, updates);
    if (!updatedUser) {
      throw new Error('Error al actualizar el usuario');
    }

    // Actualizar signal
    this.usersSignal.update(users =>
      users.map(user => user.id === id ? updatedUser : user)
    );

    return updatedUser;
  }

  /**
   * Elimina un usuario por su ID.
   * 
   * @param {string} id - ID del usuario a eliminar.
   * @returns {boolean} true si se eliminó correctamente, false si no se encontró.
   * @throws {Error} Si hay un error al eliminar.
   * 
   * @example
   * ```typescript
   * const deleted = this.userService.deleteUser('abc123');
   * if (deleted) {
   *   console.log('Usuario eliminado exitosamente');
   * }
   * ```
   */
  deleteUser(id: string): boolean {
    const deleted = this.storageService.delete(id);

    if (deleted) {
      // Actualizar signal
      this.usersSignal.update(users =>
        users.filter(user => user.id !== id)
      );
    }

    return deleted;
  }

  /**
   * Elimina todos los usuarios del sistema.
   * 
   * @returns {void}
   * 
   * @example
   * ```typescript
   * this.userService.deleteAll();
   * console.log('Todos los usuarios han sido eliminados');
   * ```
   */
  deleteAll(): void {
    this.storageService.clear();
    this.usersSignal.set([]);
  }

  /**
   * Busca usuarios por nombre, apellido o email.
   * 
   * @param {string} searchTerm - Término de búsqueda.
   * @returns {User[]} Array de usuarios que coinciden con la búsqueda.
   * 
   * @example
   * ```typescript
   * const results = this.userService.searchUsers('juan');
   * console.log(`Encontrados ${results.length} usuarios`);
   * ```
   */
  searchUsers(searchTerm: string): User[] {
    if (!searchTerm || searchTerm.trim() === '') {
      return this.usersSignal();
    }

    const term = searchTerm.toLowerCase().trim();
    return this.usersSignal().filter(user =>
      user.nombre.toLowerCase().includes(term) ||
      user.apellido.toLowerCase().includes(term) ||
      user.email.toLowerCase().includes(term)
    );
  }

  /**
   * Genera un ID único para un usuario.
   * 
   * @private
   * @returns {string} ID único generado.
   */
  private generateId(): string {
    return `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
  }

  /**
   * Valida los datos de un usuario.
   * 
   * @private
   * @param {Partial<CreateUserDto>} userData - Datos a validar.
   * @throws {Error} Si algún campo es inválido.
   */
  private validateUserData(userData: Partial<CreateUserDto>): void {
    // Validar nombre
    if (userData.nombre !== undefined && (!userData.nombre || userData.nombre.trim() === '')) {
      throw new Error('El nombre es requerido');
    }

    // Validar apellido
    if (userData.apellido !== undefined && (!userData.apellido || userData.apellido.trim() === '')) {
      throw new Error('El apellido es requerido');
    }

    // Validar email
    if (userData.email !== undefined) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(userData.email)) {
        throw new Error('El email no es válido');
      }
    }

    // Validar teléfono (formato colombiano)
    if (userData.telefono !== undefined) {
      const phoneRegex = /^(\+57|0057|57)?[\s\-]?[3][0-9]{2}[\s\-]?\d{3}[\s\-]?\d{4}$/;
      if (!phoneRegex.test(userData.telefono.replace(/\s/g, ''))) {
        throw new Error('El teléfono debe ser un número válido colombiano');
      }
    }

    // Validar fecha de nacimiento
    if (userData.fechaNacimiento !== undefined) {
      const birthDate = new Date(userData.fechaNacimiento);
      const today = new Date();
      const age = today.getFullYear() - birthDate.getFullYear();

      if (birthDate > today) {
        throw new Error('La fecha de nacimiento no puede ser futura');
      }

      if (age > 150) {
        throw new Error('La fecha de nacimiento no es válida');
      }
    }
  }
}
