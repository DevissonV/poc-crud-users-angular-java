import { Component } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

/**
 * Componente raíz de la aplicación.
 * 
 * @class App
 * @description
 * Componente principal que contiene el layout base de la aplicación
 * incluyendo el toolbar de navegación y el router outlet para
 * la renderización de componentes según la ruta activa.
 * 
 * @example
 * ```html
 * <app-root></app-root>
 * ```
 */
@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  /**
   * Título de la aplicación.
   * @readonly
   */
  readonly title = 'CRUD de Usuarios';

  /**
   * Año actual para el footer.
   * @readonly
   */
  readonly currentYear = new Date().getFullYear();
}
