import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';
import { UserStorageService } from '../../../core/services/user-storage.service';
import { User, CreateUserDto } from '../../../core/models/user.model';

/**
 * Suite de pruebas para UserService
 * 
 * Verifica el correcto funcionamiento del servicio de lógica
 * de negocio, incluyendo validaciones y manejo de Signals.
 */
describe('UserService', () => {
  let service: UserService;
  let storageService: jest.Mocked<UserStorageService>;

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

  beforeEach(() => {
    // Crear spy del UserStorageService
    const storageServiceSpy = {
      getAll: jest.fn(),
      getById: jest.fn(),
      save: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      clear: jest.fn(),
      emailExists: jest.fn()
    } as unknown as jest.Mocked<UserStorageService>;

    TestBed.configureTestingModule({
      providers: [
        UserService,
        { provide: UserStorageService, useValue: storageServiceSpy }
      ]
    });

    service = TestBed.inject(UserService);
    storageService = TestBed.inject(UserStorageService) as jest.Mocked<UserStorageService>;

    // Configurar comportamiento por defecto
    storageService.getAll.mockReturnValue([]);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('loadUsers', () => {
    it('should load users from storage', () => {
      storageService.getAll.mockReturnValue(mockUsers);
      
      service.loadUsers();
      
      expect(service.users().length).toBe(2);
      expect(service.users()[0].nombre).toBe('Juan');
    });

    it('should update hasUsers signal', () => {
      storageService.getAll.mockReturnValue([]);
      service.loadUsers();
      expect(service.hasUsers()).toBe(false);

      storageService.getAll.mockReturnValue(mockUsers);
      service.loadUsers();
      expect(service.hasUsers()).toBe(true);
    });

    it('should update userCount signal', () => {
      storageService.getAll.mockReturnValue(mockUsers);
      service.loadUsers();
      expect(service.userCount()).toBe(2);
    });
  });

  describe('getUserById', () => {
    beforeEach(() => {
      storageService.getAll.mockReturnValue(mockUsers);
      service.loadUsers();
    });

    it('should return user by id', () => {
      const user = service.getUserById('1');
      expect(user).toBeTruthy();
      expect(user?.nombre).toBe('Juan');
    });

    it('should return undefined for non-existent id', () => {
      const user = service.getUserById('999');
      expect(user).toBeUndefined();
    });
  });

  describe('createUser', () => {
    const validUserData: CreateUserDto = {
      nombre: 'Pedro',
      apellido: 'López',
      email: 'pedro@example.com',
      telefono: '+57 320 789 1234',
      fechaNacimiento: new Date('1988-03-20')
    };

    beforeEach(() => {
      storageService.getAll.mockReturnValue([]);
      storageService.emailExists.mockReturnValue(false);
      storageService.save.mockImplementation((user: User) => user);
      service.loadUsers();
    });

    it('should create a new user', () => {
      const created = service.createUser(validUserData);
      
      expect(created).toBeTruthy();
      expect(created.id).toBeTruthy();
      expect(created.nombre).toBe('Pedro');
      expect(storageService.save).toHaveBeenCalled();
    });

    it('should throw error if email exists', () => {
      storageService.emailExists.mockReturnValue(true);
      
      expect(() => service.createUser(validUserData))
        .toThrow('El email ya está registrado en el sistema');
    });

    it('should throw error for empty nombre', () => {
      const invalidData = { ...validUserData, nombre: '' };
      
      expect(() => service.createUser(invalidData))
        .toThrow('El nombre es requerido');
    });

    it('should throw error for empty apellido', () => {
      const invalidData = { ...validUserData, apellido: '' };
      
      expect(() => service.createUser(invalidData))
        .toThrow('El apellido es requerido');
    });

    it('should throw error for invalid email', () => {
      const invalidData = { ...validUserData, email: 'invalid-email' };
      
      expect(() => service.createUser(invalidData))
        .toThrow('El email no es válido');
    });

    it('should throw error for invalid phone', () => {
      const invalidData = { ...validUserData, telefono: '123' };
      
      expect(() => service.createUser(invalidData))
        .toThrow('El teléfono debe ser un número válido colombiano');
    });

    it('should throw error for future birth date', () => {
      const futureDate = new Date();
      futureDate.setFullYear(futureDate.getFullYear() + 1);
      const invalidData = { ...validUserData, fechaNacimiento: futureDate };
      
      expect(() => service.createUser(invalidData))
        .toThrow('La fecha de nacimiento no puede ser futura');
    });

    it('should update users signal after creation', () => {
      service.createUser(validUserData);
      expect(service.users().length).toBe(1);
    });
  });

  describe('updateUser', () => {
    beforeEach(() => {
      storageService.getAll.mockReturnValue(mockUsers);
      storageService.emailExists.mockReturnValue(false);
      service.loadUsers();
    });

    it('should update an existing user', () => {
      const updatedUser = { ...mockUsers[0], nombre: 'Juan Carlos' };
      storageService.update.mockReturnValue(updatedUser);

      const result = service.updateUser({
        id: '1',
        nombre: 'Juan Carlos'
      });

      expect(result.nombre).toBe('Juan Carlos');
      expect(storageService.update).toHaveBeenCalledWith('1', { nombre: 'Juan Carlos' });
    });

    it('should throw error for non-existent user', () => {
      expect(() => service.updateUser({ id: '999', nombre: 'Test' }))
        .toThrow('Usuario no encontrado');
    });

    it('should throw error if email exists in another user', () => {
      storageService.emailExists.mockReturnValue(true);
      
      expect(() => service.updateUser({ id: '1', email: 'maria@example.com' }))
        .toThrow('El email ya está registrado en otro usuario');
    });

    it('should update users signal after update', () => {
      const updatedUser = { ...mockUsers[0], nombre: 'Juan Carlos' };
      storageService.update.mockReturnValue(updatedUser);

      service.updateUser({ id: '1', nombre: 'Juan Carlos' });

      const user = service.getUserById('1');
      expect(user?.nombre).toBe('Juan Carlos');
    });
  });

  describe('deleteUser', () => {
    beforeEach(() => {
      storageService.getAll.mockReturnValue(mockUsers);
      service.loadUsers();
    });

    it('should delete an existing user', () => {
      storageService.delete.mockReturnValue(true);
      
      const deleted = service.deleteUser('1');
      
      expect(deleted).toBe(true);
      expect(storageService.delete).toHaveBeenCalledWith('1');
    });

    it('should return false for non-existent user', () => {
      storageService.delete.mockReturnValue(false);
      
      const deleted = service.deleteUser('999');
      
      expect(deleted).toBe(false);
    });

    it('should update users signal after deletion', () => {
      storageService.delete.mockReturnValue(true);
      
      service.deleteUser('1');
      
      expect(service.users().length).toBe(1);
      expect(service.getUserById('1')).toBeUndefined();
    });
  });

  describe('deleteAll', () => {
    beforeEach(() => {
      storageService.getAll.mockReturnValue(mockUsers);
      service.loadUsers();
    });

    it('should delete all users', () => {
      service.deleteAll();
      
      expect(storageService.clear).toHaveBeenCalled();
      expect(service.users().length).toBe(0);
    });
  });

  describe('searchUsers', () => {
    beforeEach(() => {
      storageService.getAll.mockReturnValue(mockUsers);
      service.loadUsers();
    });

    it('should return all users for empty search', () => {
      const results = service.searchUsers('');
      expect(results.length).toBe(2);
    });

    it('should search by nombre', () => {
      const results = service.searchUsers('Juan');
      expect(results.length).toBe(1);
      expect(results[0].nombre).toBe('Juan');
    });

    it('should search by apellido', () => {
      const results = service.searchUsers('García');
      expect(results.length).toBe(1);
      expect(results[0].apellido).toBe('García');
    });

    it('should search by email', () => {
      const results = service.searchUsers('maria@');
      expect(results.length).toBe(1);
      expect(results[0].email).toBe('maria@example.com');
    });

    it('should be case insensitive', () => {
      const results = service.searchUsers('JUAN');
      expect(results.length).toBe(1);
    });

    it('should return empty array for no matches', () => {
      const results = service.searchUsers('NoExiste');
      expect(results.length).toBe(0);
    });
  });
});
