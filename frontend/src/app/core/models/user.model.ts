/**
 * Modelo de datos para un usuario del sistema.
 * 
 * Este modelo representa la estructura completa de un usuario,
 * incluyendo todos los campos necesarios para el CRUD.
 * 
 * @interface User
 * @example
 * ```typescript
 * const user: User = {
 *   id: 'a1b2c3d4',
 *   nombre: 'Juan',
 *   apellido: 'Pérez',
 *   email: 'juan.perez@example.com',
 *   telefono: '+57 300 123 4567',
 *   fechaNacimiento: new Date('1990-05-15')
 * };
 * ```
 */
export interface User {
  /**
   * Identificador único del usuario.
   * Se genera automáticamente al crear un nuevo usuario.
   * @type {string}
   */
  id: string;

  /**
   * Nombre del usuario.
   * @type {string}
   */
  nombre: string;

  /**
   * Apellido del usuario.
   * @type {string}
   */
  apellido: string;

  /**
   * Correo electrónico del usuario.
   * Debe ser un email válido y único en el sistema.
   * @type {string}
   */
  email: string;

  /**
   * Número de teléfono del usuario.
   * Formato recomendado: +57 XXX XXX XXXX (Colombia)
   * @type {string}
   */
  telefono: string;

  /**
   * Fecha de nacimiento del usuario.
   * @type {Date}
   */
  fechaNacimiento: Date;
}

/**
 * DTO (Data Transfer Object) para crear un nuevo usuario.
 * 
 * Omite el campo 'id' ya que se genera automáticamente
 * en el momento de la creación.
 * 
 * @interface CreateUserDto
 * @example
 * ```typescript
 * const newUser: CreateUserDto = {
 *   nombre: 'María',
 *   apellido: 'García',
 *   email: 'maria.garcia@example.com',
 *   telefono: '+57 310 456 7890',
 *   fechaNacimiento: new Date('1995-08-20')
 * };
 * ```
 */
export type CreateUserDto = Omit<User, 'id'>;

/**
 * DTO (Data Transfer Object) para actualizar un usuario existente.
 * 
 * El ID es requerido para identificar el usuario a actualizar.
 * Todos los demás campos son opcionales, permitiendo actualizaciones parciales.
 * 
 * @interface UpdateUserDto
 * @example
 * ```typescript
 * const updateData: UpdateUserDto = {
 *   id: 'a1b2c3d4',
 *   email: 'nuevo.email@example.com',
 *   telefono: '+57 320 567 8901'
 *   // Los demás campos se mantienen sin cambios
 * };
 * ```
 */
export type UpdateUserDto = Partial<User> & Pick<User, 'id'>;

/**
 * Tipo helper para representar un usuario sin fecha de nacimiento parseada.
 * Útil para la serialización y deserialización desde/hacia localStorage.
 * 
 * @internal
 */
export type UserRaw = Omit<User, 'fechaNacimiento'> & { fechaNacimiento: string };
