import { Routes } from '@angular/router';

/**
 * Configuración de rutas de la aplicación.
 * 
 * Define todas las rutas disponibles para la navegación en la aplicación.
 * Utiliza lazy loading para optimizar la carga inicial.
 * 
 * @constant routes
 * @type {Routes}
 * 
 * Rutas disponibles:
 * - '/' - Redirige a /users
 * - '/users' - Lista de usuarios
 * - '/users/new' - Formulario para crear usuario
 * - '/users/:id' - Detalle de usuario
 * - '/users/:id/edit' - Formulario para editar usuario
 * - '**' - Ruta no encontrada (redirige a /users)
 */
export const routes: Routes = [
  {
    path: '',
    redirectTo: '/users',
    pathMatch: 'full'
  },
  {
    path: 'users',
    loadComponent: () => 
      import('./features/users/components/user-list/user-list.component')
        .then(m => m.UserListComponent),
    title: 'Lista de Usuarios'
  },
  {
    path: 'users/new',
    loadComponent: () => 
      import('./features/users/components/user-form/user-form.component')
        .then(m => m.UserFormComponent),
    title: 'Nuevo Usuario'
  },
  {
    path: 'users/:id',
    loadComponent: () => 
      import('./features/users/components/user-detail/user-detail.component')
        .then(m => m.UserDetailComponent),
    title: 'Detalle de Usuario'
  },
  {
    path: 'users/:id/edit',
    loadComponent: () => 
      import('./features/users/components/user-form/user-form.component')
        .then(m => m.UserFormComponent),
    title: 'Editar Usuario'
  },
  {
    path: '**',
    redirectTo: '/users'
  }
];
