import { Injectable, signal, computed, inject } from '@angular/core';
import { User, CreateUserDto, UpdateUserDto } from '../../../core/models/user.model';
import { UserHttpService } from '../../../core/services/user-http.service';

/**
 * Servicio principal para gestión de usuarios.
 * Conecta con el backend REST API y maneja estado reactivo con Signals.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly httpService = inject(UserHttpService);

  private readonly usersSignal = signal<User[]>([]);
  readonly users = this.usersSignal.asReadonly();

  readonly hasUsers = computed(() => this.usersSignal().length > 0);
  readonly userCount = computed(() => this.usersSignal().length);

  private readonly loadingSignal = signal<boolean>(false);
  readonly loading = this.loadingSignal.asReadonly();

  private readonly errorSignal = signal<string | null>(null);
  readonly error = this.errorSignal.asReadonly();

  constructor() {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    this.httpService.getAllUsers().subscribe({
      next: (users) => {
        this.usersSignal.set(users);
        this.loadingSignal.set(false);
      },
      error: (error) => {
        this.errorSignal.set(error.message);
        this.loadingSignal.set(false);
        console.error('Error al cargar usuarios:', error);
      }
    });
  }

  getUserById(id: string, callback: (user: User | null, error: string | null) => void): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    this.httpService.getUserById(id).subscribe({
      next: (user) => {
        this.loadingSignal.set(false);
        callback(user, null);
      },
      error: (error) => {
        this.errorSignal.set(error.message);
        this.loadingSignal.set(false);
        callback(null, error.message);
      }
    });
  }

  getUserByIdLocal(id: string): User | undefined {
    return this.usersSignal().find(user => user.id === id);
  }

  createUser(userData: CreateUserDto, callback: (user: User | null, error: string | null) => void): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    this.httpService.createUser(userData).subscribe({
      next: (user) => {
        this.usersSignal.update(users => [...users, user]);
        this.loadingSignal.set(false);
        callback(user, null);
      },
      error: (error) => {
        this.errorSignal.set(error.message);
        this.loadingSignal.set(false);
        callback(null, error.message);
      }
    });
  }

  updateUser(userData: User, callback: (user: User | null, error: string | null) => void): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    this.httpService.updateUser(userData).subscribe({
      next: (user) => {
        this.usersSignal.update(users =>
          users.map(u => u.id === user.id ? user : u)
        );
        this.loadingSignal.set(false);
        callback(user, null);
      },
      error: (error) => {
        this.errorSignal.set(error.message);
        this.loadingSignal.set(false);
        callback(null, error.message);
      }
    });
  }

  deleteUser(id: string, callback: (success: boolean, error: string | null) => void): void {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    this.httpService.deleteUser(id).subscribe({
      next: () => {
        this.usersSignal.update(users =>
          users.filter(user => user.id !== id)
        );
        this.loadingSignal.set(false);
        callback(true, null);
      },
      error: (error) => {
        this.errorSignal.set(error.message);
        this.loadingSignal.set(false);
        callback(false, error.message);
      }
    });
  }

  searchUsers(searchTerm: string, callback: (users: User[] | null, error: string | null) => void): void {
    if (!searchTerm || searchTerm.trim() === '') {
      callback(this.usersSignal(), null);
      return;
    }

    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    this.httpService.searchUsers(searchTerm).subscribe({
      next: (users) => {
        this.loadingSignal.set(false);
        callback(users, null);
      },
      error: (error) => {
        this.errorSignal.set(error.message);
        this.loadingSignal.set(false);
        callback(null, error.message);
      }
    });
  }

  searchUsersLocal(searchTerm: string): User[] {
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
}
