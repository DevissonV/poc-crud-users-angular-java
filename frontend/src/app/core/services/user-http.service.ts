import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User } from '../models/user.model';
import { ApiResponse, ErrorData } from '../models/api-response.model';
import { environment } from '../../../environments/environment';

/**
 * Servicio HTTP para operaciones CRUD de usuarios con el backend REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class UserHttpService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/users`;

  /**
   * Obtiene todos los usuarios del backend.
   */
  getAllUsers(): Observable<User[]> {
    return this.http.get<ApiResponse<any[]>>(this.apiUrl).pipe(
      map(response => response.data.map(this.mapResponseToUser)),
      catchError(this.handleError)
    );
  }

  /**
   * Obtiene un usuario por su ID.
   */
  getUserById(id: string): Observable<User> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/${id}`).pipe(
      map(response => this.mapResponseToUser(response.data)),
      catchError(this.handleError)
    );
  }

  /**
   * Crea un nuevo usuario.
   */
  createUser(user: Omit<User, 'id'>): Observable<User> {
    const payload = this.mapUserToRequest(user);
    return this.http.post<ApiResponse<any>>(this.apiUrl, payload).pipe(
      map(response => this.mapResponseToUser(response.data)),
      catchError(this.handleError)
    );
  }

  /**
   * Actualiza un usuario existente.
   */
  updateUser(user: User): Observable<User> {
    const payload = this.mapUserToRequest(user);
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${user.id}`, payload).pipe(
      map(response => this.mapResponseToUser(response.data)),
      catchError(this.handleError)
    );
  }

  /**
   * Elimina un usuario.
   */
  deleteUser(id: string): Observable<void> {
    return this.http.delete<ApiResponse<null>>(`${this.apiUrl}/${id}`).pipe(
      map(() => undefined),
      catchError(this.handleError)
    );
  }

  /**
   * Busca usuarios por término de búsqueda.
   */
  searchUsers(term: string): Observable<User[]> {
    return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/search`, {
      params: { q: term }
    }).pipe(
      map(response => response.data.map(this.mapResponseToUser)),
      catchError(this.handleError)
    );
  }

  /**
   * Mapea la respuesta del backend (con fechaNacimiento como string) a User (con Date).
   */
  private mapResponseToUser(response: any): User {
    return {
      id: response.id,
      nombre: response.nombre,
      apellido: response.apellido,
      email: response.email,
      telefono: response.telefono,
      fechaNacimiento: new Date(response.fechaNacimiento)
    };
  }

  /**
   * Mapea User (con Date) al formato esperado por el backend (con string ISO).
   */
  private mapUserToRequest(user: Partial<User>): any {
    return {
      ...(user.id && { id: user.id }),
      nombre: user.nombre,
      apellido: user.apellido,
      email: user.email,
      telefono: user.telefono,
      fechaNacimiento: user.fechaNacimiento instanceof Date
        ? user.fechaNacimiento.toISOString().split('T')[0]
        : user.fechaNacimiento
    };
  }

  /**
   * Maneja errores HTTP y los convierte en mensajes amigables.
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Ha ocurrido un error inesperado';

    if (error.error instanceof ErrorEvent) {
      // Error del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del servidor - Nuevo formato ApiResponse
      if (error.status === 0) {
        errorMessage = 'No se pudo conectar con el servidor. Verifica que el backend esté ejecutándose.';
      } else if (error.error?.metadata?.message) {
        // Mensaje del nuevo formato
        errorMessage = error.error.metadata.message;
        
        // Si hay errores de validación
        if (error.error.data?.validationErrors) {
          const errors = Object.values(error.error.data.validationErrors).join(', ');
          errorMessage = `Errores de validación: ${errors}`;
        }
      } else if (error.error?.message) {
        // Formato antiguo por retrocompatibilidad
        errorMessage = error.error.message;
      } else if (error.error?.validationErrors) {
        const errors = Object.values(error.error.validationErrors).join(', ');
        errorMessage = `Errores de validación: ${errors}`;
      } else {
        errorMessage = `Error ${error.status}: ${error.statusText}`;
      }
    }

    console.error('Error en la petición HTTP:', error);
    return throwError(() => new Error(errorMessage));
  }
}
