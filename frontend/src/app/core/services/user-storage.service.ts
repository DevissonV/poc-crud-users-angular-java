import { Injectable } from '@angular/core';
import { User, UserRaw } from '../models/user.model';

/**
 * Servicio de persistencia de usuarios en localStorage.
 * 
 * Este servicio maneja todas las operaciones de lectura y escritura
 * de usuarios en el almacenamiento local del navegador.
 * 
 * @class UserStorageService
 * @injectable
 * @description
 * Proporciona una capa de abstracción sobre localStorage para manejar
 * la persistencia de usuarios. Incluye serialización/deserialización
 * automática de fechas y manejo de errores.
 * 
 * @example
 * ```typescript
 * constructor(private storageService: UserStorageService) {}
 * 
 * const users = this.storageService.getAll();
 * console.log(users);
 * ```
 */
@Injectable({
  providedIn: 'root'
})
export class UserStorageService {
  /**
   * Clave utilizada para almacenar los usuarios en localStorage.
   * @private
   * @readonly
   */
  private readonly STORAGE_KEY = 'angular-users';

  /**
   * Obtiene todos los usuarios almacenados en localStorage.
   * 
   * @returns {User[]} Array de usuarios. Retorna array vacío si no hay usuarios.
   * @throws {Error} Si hay un error al parsear los datos de localStorage.
   * 
   * @example
   * ```typescript
   * const users = this.storageService.getAll();
   * console.log(`Total usuarios: ${users.length}`);
   * ```
   */
  getAll(): User[] {
    try {
      const data = localStorage.getItem(this.STORAGE_KEY);
      if (!data) {
        return [];
      }

      const rawUsers: UserRaw[] = JSON.parse(data);
      return rawUsers.map(this.deserializeUser);
    } catch (error) {
      console.error('Error al leer usuarios de localStorage:', error);
      return [];
    }
  }

  /**
   * Obtiene un usuario por su ID.
   * 
   * @param {string} id - ID del usuario a buscar.
   * @returns {User | null} El usuario encontrado o null si no existe.
   * 
   * @example
   * ```typescript
   * const user = this.storageService.getById('abc123');
   * if (user) {
   *   console.log(`Usuario encontrado: ${user.nombre}`);
   * }
   * ```
   */
  getById(id: string): User | null {
    const users = this.getAll();
    return users.find(user => user.id === id) || null;
  }

  /**
   * Guarda un nuevo usuario en localStorage.
   * 
   * @param {User} user - Usuario a guardar.
   * @returns {User} El usuario guardado.
   * @throws {Error} Si hay un error al guardar en localStorage.
   * 
   * @example
   * ```typescript
   * const newUser: User = {
   *   id: 'abc123',
   *   nombre: 'Juan',
   *   apellido: 'Pérez',
   *   email: 'juan@example.com',
   *   telefono: '+57 300 123 4567',
   *   fechaNacimiento: new Date('1990-01-01')
   * };
   * this.storageService.save(newUser);
   * ```
   */
  save(user: User): User {
    try {
      const users = this.getAll();
      users.push(user);
      this.saveAll(users);
      return user;
    } catch (error) {
      console.error('Error al guardar usuario:', error);
      throw new Error('No se pudo guardar el usuario');
    }
  }

  /**
   * Actualiza un usuario existente en localStorage.
   * 
   * @param {string} id - ID del usuario a actualizar.
   * @param {Partial<User>} updates - Campos a actualizar.
   * @returns {User | null} El usuario actualizado o null si no existe.
   * @throws {Error} Si hay un error al actualizar en localStorage.
   * 
   * @example
   * ```typescript
   * const updated = this.storageService.update('abc123', {
   *   email: 'nuevo@example.com',
   *   telefono: '+57 310 456 7890'
   * });
   * ```
   */
  update(id: string, updates: Partial<User>): User | null {
    try {
      const users = this.getAll();
      const index = users.findIndex(user => user.id === id);

      if (index === -1) {
        return null;
      }

      users[index] = { ...users[index], ...updates, id }; // Preserve ID
      this.saveAll(users);
      return users[index];
    } catch (error) {
      console.error('Error al actualizar usuario:', error);
      throw new Error('No se pudo actualizar el usuario');
    }
  }

  /**
   * Elimina un usuario de localStorage.
   * 
   * @param {string} id - ID del usuario a eliminar.
   * @returns {boolean} true si se eliminó correctamente, false si no se encontró.
   * @throws {Error} Si hay un error al eliminar de localStorage.
   * 
   * @example
   * ```typescript
   * const deleted = this.storageService.delete('abc123');
   * if (deleted) {
   *   console.log('Usuario eliminado exitosamente');
   * }
   * ```
   */
  delete(id: string): boolean {
    try {
      const users = this.getAll();
      const filtered = users.filter(user => user.id !== id);

      if (filtered.length === users.length) {
        return false; // No se encontró el usuario
      }

      this.saveAll(filtered);
      return true;
    } catch (error) {
      console.error('Error al eliminar usuario:', error);
      throw new Error('No se pudo eliminar el usuario');
    }
  }

  /**
   * Limpia todos los usuarios de localStorage.
   * 
   * @returns {void}
   * 
   * @example
   * ```typescript
   * this.storageService.clear();
   * console.log('Todos los usuarios han sido eliminados');
   * ```
   */
  clear(): void {
    try {
      localStorage.removeItem(this.STORAGE_KEY);
    } catch (error) {
      console.error('Error al limpiar localStorage:', error);
      throw new Error('No se pudo limpiar el almacenamiento');
    }
  }

  /**
   * Guarda todos los usuarios en localStorage.
   * 
   * @private
   * @param {User[]} users - Array de usuarios a guardar.
   * @returns {void}
   */
  private saveAll(users: User[]): void {
    const rawUsers = users.map(this.serializeUser);
    const data = JSON.stringify(rawUsers);
    localStorage.setItem(this.STORAGE_KEY, data);
  }

  /**
   * Serializa un usuario para almacenamiento en localStorage.
   * Convierte el objeto Date a string ISO.
   * 
   * @private
   * @param {User} user - Usuario a serializar.
   * @returns {UserRaw} Usuario serializado.
   */
  private serializeUser(user: User): UserRaw {
    return {
      ...user,
      fechaNacimiento: user.fechaNacimiento.toISOString()
    };
  }

  /**
   * Deserializa un usuario desde localStorage.
   * Convierte el string ISO a objeto Date.
   * 
   * @private
   * @param {UserRaw} raw - Usuario en formato raw.
   * @returns {User} Usuario deserializado.
   */
  private deserializeUser(raw: UserRaw): User {
    return {
      ...raw,
      fechaNacimiento: new Date(raw.fechaNacimiento)
    };
  }

  /**
   * Verifica si un email ya existe en el sistema.
   * 
   * @param {string} email - Email a verificar.
   * @param {string} [excludeId] - ID de usuario a excluir de la búsqueda (útil para edición).
   * @returns {boolean} true si el email ya existe, false en caso contrario.
   * 
   * @example
   * ```typescript
   * if (this.storageService.emailExists('test@example.com')) {
   *   console.log('Este email ya está registrado');
   * }
   * ```
   */
  emailExists(email: string, excludeId?: string): boolean {
    const users = this.getAll();
    return users.some(user => 
      user.email.toLowerCase() === email.toLowerCase() && 
      user.id !== excludeId
    );
  }
}
