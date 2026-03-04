import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { User, CreateUserDto } from '../../../../core/models/user.model';

/**
 * Componente para crear y editar usuarios.
 * 
 * @class UserFormComponent
 * @standalone
 * @implements {OnInit}
 * @description
 * Formulario reactivo para crear nuevos usuarios o editar existentes.
 * Detecta automáticamente el modo según la presencia de un ID en la ruta.
 * Incluye validaciones completas y mensajes de error personalizados.
 * 
 * @example
 * ```typescript
 * // Uso en routing - Crear
 * {
 *   path: 'users/new',
 *   component: UserFormComponent
 * }
 * 
 * // Uso en routing - Editar
 * {
 *   path: 'users/:id/edit',
 *   component: UserFormComponent
 * }
 * ```
 */
@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss'
})
export class UserFormComponent implements OnInit {
  /**
   * FormBuilder inyectado.
   * @private
   * @readonly
   */
  private readonly fb = inject(FormBuilder);

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
   * Formulario reactivo.
   * @readonly
   */
  readonly userForm: FormGroup;

  /**
   * Signal que indica si se está editando un usuario existente.
   */
  readonly isEditMode = signal(false);

  /**
   * Signal con el ID del usuario en modo edición.
   */
  readonly userId = signal<string | null>(null);

  /**
   * Signal para controlar el estado de guardado.
   */
  readonly saving = signal(false);

  /**
   * Fecha máxima permitida (hoy).
   * @readonly
   */
  readonly maxDate = new Date();

  /**
   * Fecha mínima permitida (150 años atrás).
   * @readonly
   */
  readonly minDate = new Date(new Date().getFullYear() - 150, 0, 1);

  constructor() {
    // Inicializar formulario
    this.userForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      apellido: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.required, Validators.pattern(/^(\+57|0057|57)?[\s\-]?[3][0-9]{2}[\s\-]?\d{3}[\s\-]?\d{4}$/)]],
      fechaNacimiento: ['', [Validators.required]]
    });
  }

  /**
   * Hook de inicialización del componente.
   * Detecta modo edición y carga datos si es necesario.
   * 
   * @returns {void}
   */
  ngOnInit(): void {
    // Verificar si estamos en modo edición
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.isEditMode.set(true);
      this.userId.set(id);
      this.loadUser(id);
    }
  }

  /**
   * Carga los datos de un usuario para edición.
   * 
   * @private
   * @param {string} id - ID del usuario a cargar.
   * @returns {void}
   */
  private loadUser(id: string): void {
    this.userService.getUserById(id, (user, error) => {
      if (error || !user) {
        this.snackBar.open('Usuario no encontrado', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/users']);
        return;
      }

      // Cargar datos en el formulario
      this.userForm.patchValue({
        nombre: user.nombre,
        apellido: user.apellido,
        email: user.email,
        telefono: user.telefono,
        fechaNacimiento: user.fechaNacimiento
      });
    });
  }

  /**
   * Guarda el usuario (crear o actualizar).
   * 
   * @returns {Promise<void>}
   * 
   * @example
   * ```typescript
   * await this.onSubmit();
   * ```
   */
  async onSubmit(): Promise<void> {
    // Validar formulario
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      this.snackBar.open('Por favor, corrige los errores del formulario', 'Cerrar', {
        duration: 3000
      });
      return;
    }

    this.saving.set(true);

    const formValue = this.userForm.value;

    if (this.isEditMode()) {
      // Actualizar usuario existente
      const userId = this.userId();
      if (!userId) {
        this.snackBar.open('ID de usuario no encontrado', 'Cerrar', { duration: 3000 });
        this.saving.set(false);
        return;
      }

      this.userService.updateUser({
        id: userId,
        nombre: formValue.nombre!,
        apellido: formValue.apellido!,
        email: formValue.email!,
        telefono: formValue.telefono!,
        fechaNacimiento: formValue.fechaNacimiento!
      }, async (user, error) => {
        this.saving.set(false);
        if (error) {
          this.snackBar.open(error, 'Cerrar', { duration: 5000 });
        } else {
          this.snackBar.open('Usuario actualizado correctamente', 'Cerrar', { duration: 3000 });
          await this.router.navigate(['/users']);
        }
      });
    } else {
      // Crear nuevo usuario
      const newUser: CreateUserDto = {
        nombre: formValue.nombre!,
        apellido: formValue.apellido!,
        email: formValue.email!,
        telefono: formValue.telefono!,
        fechaNacimiento: formValue.fechaNacimiento!
      };

      this.userService.createUser(newUser, async (user, error) => {
        this.saving.set(false);
        if (error) {
          this.snackBar.open(error, 'Cerrar', { duration: 5000 });
        } else {
          this.snackBar.open('Usuario creado correctamente', 'Cerrar', { duration: 3000 });
          await this.router.navigate(['/users']);
        }
      });
    }
  }

  /**
   * Cancela la operación y vuelve a la lista.
   * 
   * @returns {Promise<boolean>} Promise de la navegación.
   * 
   * @example
   * ```typescript
   * await this.onCancel();
   * ```
   */
  async onCancel(): Promise<boolean> {
    return this.router.navigate(['/users']);
  }

  /**
   * Obtiene el mensaje de error para un campo del formulario.
   * 
   * @param {string} fieldName - Nombre del campo.
   * @returns {string} Mensaje de error o string vacío.
   * 
   * @example
   * ```typescript
   * const error = this.getErrorMessage('email');
   * ```
   */
  getErrorMessage(fieldName: string): string {
    const field = this.userForm.get(fieldName);

    if (!field || !field.errors || !field.touched) {
      return '';
    }

    if (field.errors['required']) {
      return 'Este campo es requerido';
    }

    if (field.errors['email']) {
      return 'Ingresa un email válido';
    }

    if (field.errors['minLength']) {
      return `Mínimo ${field.errors['minLength'].requiredLength} caracteres`;
    }

    if (field.errors['pattern']) {
      if (fieldName === 'telefono') {
        return 'Ingresa un número de teléfono válido (ej: +57 300 123 4567)';
      }
    }

    return '';
  }

  /**
   * Verifica si un campo tiene error.
   * 
   * @param {string} fieldName - Nombre del campo.
   * @returns {boolean} true si el campo tiene error.
   * 
   * @example
   * ```typescript
   * if (this.hasError('email')) {
   *   // Mostrar mensaje de error
   * }
   * ```
   */
  hasError(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  /**
   * Obtiene el título del formulario según el modo.
   * 
   * @returns {string} Título del formulario.
   */
  getTitle(): string {
    return this.isEditMode() ? 'Editar Usuario' : 'Nuevo Usuario';
  }

  /**
   * Obtiene el texto del botón de submit según el modo.
   * 
   * @returns {string} Texto del botón.
   */
  getSubmitButtonText(): string {
    if (this.saving()) {
      return this.isEditMode() ? 'Actualizando...' : 'Creando...';
    }
    return this.isEditMode() ? 'Actualizar' : 'Crear';
  }
}
