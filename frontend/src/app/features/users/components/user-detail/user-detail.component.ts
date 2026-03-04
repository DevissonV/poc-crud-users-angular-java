import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { User } from '../../../../core/models/user.model';

/**
 * Componente para mostrar detalles de un usuario.
 * 
 * @class UserDetailComponent
 * @standalone
 * @implements {OnInit}
 * @description
 * Muestra toda la información de un usuario en formato de tarjeta.
 * Incluye botones para editar, eliminar y volver a la lista.
 * 
 * @example
 * ```typescript
 * // Uso en routing
 * {
 *   path: 'users/:id',
 *   component: UserDetailComponent
 * }
 * ```
 */
@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatSnackBarModule
  ],
  templateUrl: './user-detail.component.html',
  styleUrl: './user-detail.component.scss'
})
export class UserDetailComponent implements OnInit {
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
   * ActivatedRoute para obtener parámetros de la ruta.
   * @private
   * @readonly
   */
  private readonly route = inject(ActivatedRoute);

  /**
   * MatSnackBar para mostrar notificaciones.
   * @private
   * @readonly
   */
  private readonly snackBar = inject(MatSnackBar);

  /**
   * Signal con el usuario actual.
   */
  readonly user = signal<User | null>(null);

  /**
   * Signal que indica si se está cargando.
   */
  readonly loading = signal(true);

  /**
   * Signal computado con el nombre completo del usuario.
   */
  readonly fullName = computed(() => {
    const currentUser = this.user();
    return currentUser ? `${currentUser.nombre} ${currentUser.apellido}` : '';
  });

  /**
   * Signal computado con la edad del usuario.
   */
  readonly age = computed(() => {
    const currentUser = this.user();
    if (!currentUser) return null;

    const today = new Date();
    const birthDate = new Date(currentUser.fechaNacimiento);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    return age;
  });

  /**
   * Hook de inicialización del componente.
   * Carga el usuario desde la ruta.
   * 
   * @returns {void}
   */
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.showNotFoundError();
      return;
    }

    this.loadUser(id);
  }

  /**
   * Carga los datos de un usuario.
   * 
   * @private
   * @param {string} id - ID del usuario a cargar.
   * @returns {void}
   */
  private loadUser(id: string): void {
    this.userService.getUserById(id, (foundUser, error) => {
      if (error || !foundUser) {
        this.showNotFoundError();
        return;
      }

      this.user.set(foundUser);
      this.loading.set(false);
    });
  }

  /**
   * Muestra error de usuario no encontrado y redirige.
   * 
   * @private
   * @returns {void}
   */
  private showNotFoundError(): void {
    this.snackBar.open('Usuario no encontrado', 'Cerrar', { duration: 3000 });
    this.router.navigate(['/users']);
  }

  /**
   * Navega al formulario de edición.
   * 
   * @returns {Promise<boolean>} Promise de la navegación.
   * 
   * @example
   * ```typescript
   * await this.editUser();
   * ```
   */
  async editUser(): Promise<boolean> {
    const currentUser = this.user();
    if (!currentUser) return false;

    return this.router.navigate(['/users', currentUser.id, 'edit']);
  }

  /**
   * Elimina el usuario actual.
   * 
   * @returns {Promise<void>}
   * 
   * @example
   * ```typescript
   * await this.deleteUser();
   * ```
   */
  async deleteUser(): Promise<void> {
    const currentUser = this.user();
    if (!currentUser) return;

    const confirmed = confirm(
      `¿Estás seguro de que deseas eliminar a ${this.fullName()}?`
    );

    if (!confirmed) {
      return;
    }

    this.userService.deleteUser(currentUser.id, async (success, error) => {
      if (success) {
        this.snackBar.open(
          `Usuario ${this.fullName()} eliminado correctamente`,
          'Cerrar',
          { duration: 3000 }
        );

        await this.router.navigate(['/users']);
      } else {
        console.error('Error al eliminar usuario:', error);
        this.snackBar.open(
          error || 'Error al eliminar el usuario',
          'Cerrar',
          { duration: 3000 }
        );
      }
    });
  }

  /**
   * Vuelve a la lista de usuarios.
   * 
   * @returns {Promise<boolean>} Promise de la navegación.
   * 
   * @example
   * ```typescript
   * await this.goBack();
   * ```
   */
  async goBack(): Promise<boolean> {
    return this.router.navigate(['/users']);
  }

  /**
   * Formatea una fecha para mostrarla.
   * 
   * @param {Date} date - Fecha a formatear.
   * @returns {string} Fecha formateada.
   * 
   * @example
   * ```typescript
   * const formatted = this.formatDate(new Date()); // "lunes, 3 de marzo de 2026"
   * ```
   */
  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * Formatea un teléfono para mostrar.
   * 
   * @param {string} phone - Teléfono a formatear.
   * @returns {string} Teléfono formateado.
   * 
   * @example
   * ```typescript
   * const formatted = this.formatPhone('+573001234567'); // "+57 300 123 4567"
   * ```
   */
  formatPhone(phone: string): string {
    // Si ya está formateado, retornar tal cual
    if (phone.includes(' ')) {
      return phone;
    }

    // Intentar formatear número colombiano
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 12 && cleaned.startsWith('57')) {
      return `+57 ${cleaned.substring(2, 5)} ${cleaned.substring(5, 8)} ${cleaned.substring(8)}`;
    } else if (cleaned.length === 10) {
      return `${cleaned.substring(0, 3)} ${cleaned.substring(3, 6)} ${cleaned.substring(6)}`;
    }

    return phone;
  }

  /**
   * Copia un valor al portapapeles.
   * 
   * @param {string} value - Valor a copiar.
   * @param {string} label - Etiqueta para el mensaje de confirmación.
   * @returns {Promise<void>}
   * 
   * @example
   * ```typescript
   * await this.copyToClipboard('test@example.com', 'Email');
   * ```
   */
  async copyToClipboard(value: string, label: string): Promise<void> {
    try {
      await navigator.clipboard.writeText(value);
      this.snackBar.open(`${label} copiado al portapapeles`, 'Cerrar', {
        duration: 2000
      });
    } catch (error) {
      console.error('Error al copiar:', error);
      this.snackBar.open('Error al copiar al portapapeles', 'Cerrar', {
        duration: 2000
      });
    }
  }
}
