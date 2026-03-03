import { TestBed } from '@angular/core/testing';
import { UserStorageService } from './user-storage.service';
import { User } from '../models/user.model';

/**
 * Suite de pruebas para UserStorageService
 * 
 * Verifica el correcto funcionamiento de las operaciones CRUD
 * sobre localStorage, incluyendo serialización de fechas.
 */
describe('UserStorageService', () => {
  let service: UserStorageService;
  let mockLocalStorage: { [key: string]: string };

  // Mock de localStorage
  beforeEach(() => {
    mockLocalStorage = {};

    jest.spyOn(localStorage, 'getItem').mockImplementation((key: string) => {
      return mockLocalStorage[key] || null;
    });

    jest.spyOn(localStorage, 'setItem').mockImplementation((key: string, value: string) => {
      mockLocalStorage[key] = value;
    });

    jest.spyOn(localStorage, 'removeItem').mockImplementation((key: string) => {
      delete mockLocalStorage[key];
    });

    jest.spyOn(localStorage, 'clear').mockImplementation(() => {
      mockLocalStorage = {};
    });

    TestBed.configureTestingModule({
      providers: [UserStorageService]
    });

    service = TestBed.inject(UserStorageService);
  });

  afterEach(() => {
    mockLocalStorage = {};
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('should return empty array when localStorage is empty', () => {
      const users = service.getAll();
      expect(users).toEqual([]);
    });

    it('should return all users from localStorage', () => {
      const mockUsers: User[] = [
        {
          id: '1',
          nombre: 'Juan',
          apellido: 'Pérez',
          email: 'juan@example.com',
          telefono: '+57 300 123 4567',
          fechaNacimiento: new Date('1990-01-01')
        },
        {
          id: '2',
          nombre: 'María',
          apellido: 'García',
          email: 'maria@example.com',
          telefono: '+57 310 456 7890',
          fechaNacimiento: new Date('1995-05-15')
        }
      ];

      // Simular datos en localStorage
      mockLocalStorage['angular-users'] = JSON.stringify(
        mockUsers.map(u => ({ ...u, fechaNacimiento: u.fechaNacimiento.toISOString() }))
      );

      const users = service.getAll();
      expect(users.length).toBe(2);
      expect(users[0].nombre).toBe('Juan');
      expect(users[1].nombre).toBe('María');
      expect(users[0].fechaNacimiento).toBeInstanceOf(Date);
    });

    it('should return empty array on parse error', () => {
      mockLocalStorage['angular-users'] = 'invalid json';
      const users = service.getAll();
      expect(users).toEqual([]);
    });
  });

  describe('getById', () => {
    beforeEach(() => {
      const mockUser = {
        id: '1',
        nombre: 'Juan',
        apellido: 'Pérez',
        email: 'juan@example.com',
        telefono: '+57 300 123 4567',
        fechaNacimiento: new Date('1990-01-01').toISOString()
      };
      mockLocalStorage['angular-users'] = JSON.stringify([mockUser]);
    });

    it('should return user by id', () => {
      const user = service.getById('1');
      expect(user).toBeTruthy();
      expect(user?.nombre).toBe('Juan');
    });

    it('should return null for non-existent id', () => {
      const user = service.getById('999');
      expect(user).toBeNull();
    });
  });

  describe('save', () => {
    it('should save a new user', () => {
      const newUser: User = {
        id: '1',
        nombre: 'Juan',
        apellido: 'Pérez',
        email: 'juan@example.com',
        telefono: '+57 300 123 4567',
        fechaNacimiento: new Date('1990-01-01')
      };

      const saved = service.save(newUser);
      expect(saved).toEqual(newUser);

      const users = service.getAll();
      expect(users.length).toBe(1);
      expect(users[0].id).toBe('1');
    });
  });

  describe('update', () => {
    beforeEach(() => {
      const mockUser = {
        id: '1',
        nombre: 'Juan',
        apellido: 'Pérez',
        email: 'juan@example.com',
        telefono: '+57 300 123 4567',
        fechaNacimiento: new Date('1990-01-01').toISOString()
      };
      mockLocalStorage['angular-users'] = JSON.stringify([mockUser]);
    });

    it('should update an existing user', () => {
      const updated = service.update('1', { nombre: 'Pedro' });
      expect(updated).toBeTruthy();
      expect(updated?.nombre).toBe('Pedro');
      expect(updated?.apellido).toBe('Pérez'); // No cambió
    });

    it('should return null for non-existent user', () => {
      const updated = service.update('999', { nombre: 'Pedro' });
      expect(updated).toBeNull();
    });

    it('should preserve id when updating', () => {
      const updated = service.update('1', { id: '999', nombre: 'Pedro' });
      expect(updated?.id).toBe('1'); // ID no debe cambiar
    });
  });

  describe('delete', () => {
    beforeEach(() => {
      const mockUsers = [
        {
          id: '1',
          nombre: 'Juan',
          apellido: 'Pérez',
          email: 'juan@example.com',
          telefono: '+57 300 123 4567',
          fechaNacimiento: new Date('1990-01-01').toISOString()
        },
        {
          id: '2',
          nombre: 'María',
          apellido: 'García',
          email: 'maria@example.com',
          telefono: '+57 310 456 7890',
          fechaNacimiento: new Date('1995-05-15').toISOString()
        }
      ];
      mockLocalStorage['angular-users'] = JSON.stringify(mockUsers);
    });

    it('should delete an existing user', () => {
      const deleted = service.delete('1');
      expect(deleted).toBe(true);

      const users = service.getAll();
      expect(users.length).toBe(1);
      expect(users[0].id).toBe('2');
    });

    it('should return false for non-existent user', () => {
      const deleted = service.delete('999');
      expect(deleted).toBe(false);

      const users = service.getAll();
      expect(users.length).toBe(2); // No cambió
    });
  });

  describe('clear', () => {
    beforeEach(() => {
      const mockUser = {
        id: '1',
        nombre: 'Juan',
        apellido: 'Pérez',
        email: 'juan@example.com',
        telefono: '+57 300 123 4567',
        fechaNacimiento: new Date('1990-01-01').toISOString()
      };
      mockLocalStorage['angular-users'] = JSON.stringify([mockUser]);
    });

    it('should clear all users', () => {
      service.clear();
      expect(localStorage.removeItem).toHaveBeenCalledWith('angular-users');
    });
  });

  describe('emailExists', () => {
    beforeEach(() => {
      const mockUsers = [
        {
          id: '1',
          nombre: 'Juan',
          apellido: 'Pérez',
          email: 'juan@example.com',
          telefono: '+57 300 123 4567',
          fechaNacimiento: new Date('1990-01-01').toISOString()
        },
        {
          id: '2',
          nombre: 'María',
          apellido: 'García',
          email: 'maria@example.com',
          telefono: '+57 310 456 7890',
          fechaNacimiento: new Date('1995-05-15').toISOString()
        }
      ];
      mockLocalStorage['angular-users'] = JSON.stringify(mockUsers);
    });

    it('should return true if email exists', () => {
      const exists = service.emailExists('juan@example.com');
      expect(exists).toBe(true);
    });

    it('should return false if email does not exist', () => {
      const exists = service.emailExists('noexiste@example.com');
      expect(exists).toBe(false);
    });

    it('should be case insensitive', () => {
      const exists = service.emailExists('JUAN@EXAMPLE.COM');
      expect(exists).toBe(true);
    });

    it('should exclude specified id', () => {
      const exists = service.emailExists('juan@example.com', '1');
      expect(exists).toBe(false); // Excluye el propio usuario
    });
  });
});
