import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { UserService } from '../../services/user.service';
import { User } from '../../../../core/models/user.model';

/**
 * Componente para listar todos los usuarios.
 * 
 * @class UserListComponent
 * @standalone
 * @description
 * Muestra una tabla Material con todos los usuarios del sistema.
 * Permite navegar a detalle, editar y eliminar usuarios.
 * Incluye un botón flotante (FAB) para crear nuevos usuarios.
 * 
 * @example
 * ```typescript
 * // Uso en routing
 * {
 *   path: 'users',
 *   component: UserListComponent
 * }
 * ```
 */
@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {
  /**
   * Servicio de usuarios inyectado.
   * @private
   * @readonly
   */
  private readonly userService = inject(UserService);

  /**
   * Router para navegación.
   * @private
   * @readonly
   */
  private readonly router = inject(Router);

  /**
   * MatDialog para mostrar diálogos de confirmación.
   * @private
   * @readonly
   */
  private readonly dialog = inject(MatDialog);

  /**
   * MatSnackBar para mostrar notificaciones.
   * @private
   * @readonly
   */
  private readonly snackBar = inject(MatSnackBar);

  /**
   * Signal con todos los usuarios.
   * Se actualiza automáticamente cuando cambia el servicio.
   * @readonly
   */
  readonly users = this.userService.users;

  /**
   * Signal que indica si hay usuarios.
   * @readonly
   */
  readonly hasUsers = this.userService.hasUsers;

  /**
   * Columnas a mostrar en la tabla.
   * @readonly
   */
  readonly displayedColumns: string[] = [
    'nombreCompleto',
    'email',
    'telefono',
    'fechaNacimiento',
    'acciones'
  ];

  /**
   * Signal para controlar el estado de carga.
   */
  readonly loading = signal(false);

  /**
   * Navega al detalle de un usuario.
   * 
   * @param {User} user - Usuario a visualizar.
   * @returns {Promise<boolean>} Promise de la navegación.
   * 
   * @example
   * ```typescript
   * this.viewUser(user);
   * ```
   */
  async viewUser(user: User): Promise<boolean> {
    return this.router.navigate(['/users', user.id]);
  }

  /**
   * Navega al formulario de edición de un usuario.
   * 
   * @param {User} user - Usuario a editar.
   * @param {Event} event - Evento del click (para prevenir propagación).
   * @returns {Promise<boolean>} Promise de la navegación.
   * 
   * @example
   * ```typescript
   * this.editUser(user, event);
   * ```
   */
  async editUser(user: User, event: Event): Promise<boolean> {
    event.stopPropagation();
    return this.router.navigate(['/users', user.id, 'edit']);
  }

  /**
   * Elimina un usuario después de confirmación.
   * 
   * @param {User} user - Usuario a eliminar.
   * @param {Event} event - Evento del click (para prevenir propagación).
   * @returns {void}
   * 
   * @example
   * ```typescript
   * this.deleteUser(user, event);
   * ```
   */
  deleteUser(user: User, event: Event): void {
    event.stopPropagation();

    // Confirmar eliminación
    const confirmed = confirm(
      `¿Estás seguro de que deseas eliminar a ${user.nombre} ${user.apellido}?`
    );

    if (!confirmed) {
      return;
    }

    this.userService.deleteUser(user.id, (success, error) => {
      if (success) {
        this.snackBar.open(
          `Usuario ${user.nombre} ${user.apellido} eliminado correctamente`,
          'Cerrar',
          { duration: 3000 }
        );
      } else {
        this.snackBar.open(
          error || 'Error: No se pudo eliminar el usuario',
          'Cerrar',
          { duration: 3000 }
        );
      }
    });
  }

  /**
   * Navega al formulario de creación de usuario.
   * 
   * @returns {Promise<boolean>} Promise de la navegación.
   * 
   * @example
   * ```typescript
   * this.createUser();
   * ```
   */
  async createUser(): Promise<boolean> {
    return this.router.navigate(['/users/new']);
  }

  /**
   * Obtiene el nombre completo de un usuario.
   * 
   * @param {User} user - Usuario.
   * @returns {string} Nombre completo.
   * 
   * @example
   * ```typescript
   * const fullName = this.getFullName(user); // "Juan Pérez"
   * ```
   */
  getFullName(user: User): string {
    return `${user.nombre} ${user.apellido}`;
  }

  /**
   * Formatea una fecha para mostrarla.
   * 
   * @param {Date} date - Fecha a formatear.
   * @returns {string} Fecha formateada.
   * 
   * @example
   * ```typescript
   * const formatted = this.formatDate(new Date()); // "03/03/2026"
   * ```
   */
  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('es-ES');
  }
}
