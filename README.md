# CRUD de Usuarios - Angular

Una aplicación moderna de gestión de usuarios construida con Angular 17+, utilizando Standalone Components, Signals para reactividad, y Angular Material para la interfaz de usuario. Los datos se persisten en localStorage del navegador, ideal para pruebas de concepto y aprendizaje.

## 📋 Características

- ✅ **CRUD Completo**: Crear, leer, actualizar y eliminar usuarios
- 🎨 **UI Moderna**: Interfaz Material Design con Angular Material
- ⚡ **Reactividad**: Uso de Signals de Angular para estado reactivo
- 💾 **Persistencia Local**: Almacenamiento en localStorage del navegador
- 📱 **Responsive**: Diseño adaptable a móviles, tablets y desktop
- ✨ **Standalone Components**: Arquitectura moderna sin NgModules
- 🧪 **Pruebas Unitarias**: Cobertura completa de servicios principales
- 📖 **Documentación JSDoc**: Código completamente documentado
- ♿ **Accesibilidad**: Componentes accesibles con ARIA labels

## 🚀 Tecnologías

- **Angular 17+**: Framework principal
- **TypeScript**: Lenguaje de programación
- **Angular Material 21**: Componentes de UI
- **Signals**: Sistema de reactividad de Angular
- **RxJS**: Para operaciones asíncronas
- **Jasmine/Karma**: Framework de pruebas
- **SCSS**: Preprocesador CSS

## 📁 Estructura del Proyecto

```
src/app/
├── core/                           # Módulo core de la aplicación
│   ├── models/                     # Modelos de datos
│   │   └── user.model.ts           # Interface User y DTOs
│   └── services/                   # Servicios compartidos
│       └── user-storage.service.ts # Servicio de persistencia
│
├── features/                       # Módulos por funcionalidad
│   └── users/                      # Feature de usuarios
│       ├── components/             # Componentes del feature
│       │   ├── user-list/         # Lista de usuarios
│       │   ├── user-form/         # Formulario crear/editar
│       │   └── user-detail/       # Detalle de usuario
│       └── services/              # Servicios del feature
│           └── user.service.ts    # Lógica de negocio
│
├── shared/                        # Recursos compartidos
│   ├── components/                # Componentes reutilizables
│   └── pipes/                     # Pipes personalizados
│
├── app.ts                         # Componente raíz
├── app.config.ts                  # Configuración de la app
└── app.routes.ts                  # Configuración de rutas
```

## 🛠️ Instalación

### Requisitos Previos

- Node.js (v18 o superior)
- npm (v9 o superior)

### Pasos de Instalación

1. **Instalar dependencias**
   ```bash
   npm install
   ```

2. **Iniciar servidor de desarrollo**
   ```bash
   npm start
   # o
   ng serve
   ```

3. **Abrir en el navegador**
   ```
   http://localhost:4200
   ```

## 📖 Uso

### Listar Usuarios

La página principal muestra todos los usuarios registrados en una tabla Material. Puedes hacer clic en cualquier fila para ver el detalle del usuario.

### Crear Usuario

1. Haz clic en el botón flotante **+** (esquina inferior derecha)
2. Completa todos los campos del formulario:
   - **Nombre**: Mínimo 2 caracteres
   - **Apellido**: Mínimo 2 caracteres
   - **Email**: Debe ser un email válido y único
   - **Teléfono**: Formato colombiano (ej: +57 300 123 4567)
   - **Fecha de Nacimiento**: No puede ser futura
3. Haz clic en **Crear**

### Editar Usuario

1. En la lista de usuarios, haz clic en el botón **editar** (icono de lápiz)
2. Modifica los campos deseados
3. Haz clic en **Actualizar**

### Ver Detalle

1. Haz clic en cualquier fila de la tabla
2. Se mostrará toda la información del usuario
3. Puedes copiar email o teléfono al portapapeles

### Eliminar Usuario

1. En la lista o en el detalle, haz clic en el botón **eliminar** (icono de papelera)
2. Confirma la eliminación en el diálogo

## 🧪 Pruebas

### Ejecutar Pruebas Unitarias

```bash
npm test
# o
ng test
```

### Ejecutar Pruebas con Cobertura

```bash
ng test --code-coverage
```

Los reportes de cobertura se generan en `coverage/`

### Pruebas Implementadas

- ✅ **UserStorageService**: Operaciones CRUD, serialización de fechas
- ✅ **UserService**: Lógica de negocio, validaciones, Signals
- ✅ **AppComponent**: Renderización del layout

## 🏗️ Arquitectura

### Patrón de Diseño

El proyecto sigue una arquitectura en capas:

1. **Capa de Presentación**: Componentes (user-list, user-form, user-detail)
2. **Capa de Lógica de Negocio**: UserService
3. **Capa de Persistencia**: UserStorageService

### Flujo de Datos

```
Component → UserService → UserStorageService → localStorage
                ↓ (Signals)
             Component (auto-actualización)
```

### Signals vs RxJS

- **Signals**: Para estado local y reactividad automática
- **RxJS**: Para operaciones asíncronas y eventos complejos

## 📚 Conceptos de Angular para Aprender

### 1. Standalone Components

```typescript
@Component({
  selector: 'app-user-list',
  standalone: true,  // ← Sin NgModules
  imports: [CommonModule, MatTableModule],
  templateUrl: './user-list.component.html'
})
```

### 2. Signals

```typescript
// Signal privado
private readonly usersSignal = signal<User[]>([]);

// Signal público de solo lectura
readonly users = this.usersSignal.asReadonly();

// Signal computado
readonly userCount = computed(() => this.users().length);
```

### 3. Inject Function

```typescript
// Inyección moderna (sin constructor)
private readonly userService = inject(UserService);
private readonly router = inject(Router);
```

### 4. Control Flow Syntax

```html
<!-- Nueva sintaxis @if -->
@if (hasUsers()) {
  <div>Hay usuarios</div>
} @else {
  <div>No hay usuarios</div>
}

<!-- Nueva sintaxis @for -->
@for (user of users(); track user.id) {
  <div>{{ user.nombre }}</div>
}
```

### 5. Reactive Forms

```typescript
userForm = this.fb.group({
  nombre: ['', [Validators.required, Validators.minLength(2)]],
  email: ['', [Validators.required, Validators.email]]
});
```

### 6. Lazy Loading

```typescript
{
  path: 'users',
  loadComponent: () => import('./user-list.component')
    .then(m => m.UserListComponent)
}
```

## 🎨 Personalización

### Cambiar Tema de Material

Edita `src/styles.scss`:

```scss
$crud-users-primary: mat.m2-define-palette(mat.$m2-blue-palette);
$crud-users-accent: mat.m2-define-palette(mat.$m2-amber-palette);
```

### Agregar Nuevos Campos

1. Actualiza `User` interface en `user.model.ts`
2. Modifica `UserFormComponent` para incluir el campo
3. Actualiza `UserListComponent` y `UserDetailComponent`

## 🐛 Solución de Problemas

### Los datos no persisten al recargar

- Verifica que tu navegador permita localStorage
- Abre DevTools → Application → Local Storage
- Busca la clave `angular-users`

### Error al compilar

```bash
# Limpiar caché
rm -rf node_modules
npm install
```

## 📝 Mejoras Futuras

- [ ] Agregar búsqueda y filtros en la lista
- [ ] Paginación para grandes cantidades de usuarios
- [ ] Exportar/Importar datos (JSON, CSV)
- [ ] Modo oscuro
- [ ] Internacionalización (i18n)
- [ ] PWA capabilities
- [ ] Integración con backend real

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

**¡Disfruta aprendiendo Angular!** 🚀

Para más información sobre Angular CLI: [Angular CLI Overview](https://angular.dev/tools/cli)
# poc-list-skills-angular
