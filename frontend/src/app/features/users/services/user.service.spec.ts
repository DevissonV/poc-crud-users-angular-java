import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';
import { UserHttpService } from '../../../core/services/user-http.service';
import { User, CreateUserDto } from '../../../core/models/user.model';
import { of, throwError } from 'rxjs';

/**
 * Suite de pruebas para UserService
 * 
 * Verifica el correcto funcionamiento del servicio de lógica
 * de negocio, incluyendo validaciones y manejo de Signals.
 */
describe('UserService', () => {
  let service: UserService;
  let httpService: jest.Mocked<UserHttpService>;

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
    // Mock UserHttpService
    const httpServiceMock = {
      getAllUsers: jest.fn().mockReturnValue(of(mockUsers)),
      getUserById: jest.fn(),
      createUser: jest.fn(),
      updateUser: jest.fn(),
      deleteUser: jest.fn(),
      searchUsers: jest.fn()
    } as unknown as jest.Mocked<UserHttpService>;

    TestBed.configureTestingModule({
      providers: [
        UserService,
        { provide: UserHttpService, useValue: httpServiceMock }
      ]
    });

    httpService = TestBed.inject(UserHttpService) as jest.Mocked<UserHttpService>;
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ==================== HAPPY PATHS ====================

  describe('loadUsers - Happy Path', () => {
    it('should load users successfully and update signals', (done) => {
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      
      service.loadUsers();
      
      setTimeout(() => {
        expect(service.users().length).toBe(2);
        expect(service.users()[0].nombre).toBe('Juan');
        expect(service.hasUsers()).toBe(true);
        expect(service.userCount()).toBe(2);
        expect(service.loading()).toBe(false);
        expect(service.error()).toBeNull();
        done();
      }, 10);
    });

    it('should load empty users array', (done) => {
      httpService.getAllUsers.mockReturnValue(of([]));
      
      service.loadUsers();
      
      setTimeout(() => {
        expect(service.users().length).toBe(0);
        expect(service.hasUsers()).toBe(false);
        expect(service.userCount()).toBe(0);
        done();
      }, 10);
    });
  });

  describe('getUserById - Happy Path', () => {
    it('should return user by id via HTTP', (done) => {
      const mockUser = mockUsers[0];
      httpService.getUserById.mockReturnValue(of(mockUser));
      
      service.getUserById('1', (user, error) => {
        expect(user).toEqual(mockUser);
        expect(error).toBeNull();
        expect(service.loading()).toBe(false);
        expect(service.error()).toBeNull();
        done();
      });
    });
  });

  describe('getUserByIdLocal - Happy Path', () => {
    it('should return user from local signal', (done) => {
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      service.loadUsers();
      
      setTimeout(() => {
        const user = service.getUserByIdLocal('1');
        expect(user).toBeTruthy();
        expect(user?.nombre).toBe('Juan');
        done();
      }, 10);
    });

    it('should return undefined for non-existent id', (done) => {
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      service.loadUsers();
      
      setTimeout(() => {
        const user = service.getUserByIdLocal('999');
        expect(user).toBeUndefined();
        done();
      }, 10);
    });
  });

  describe('createUser - Happy Path', () => {
    it('should create user successfully and update signal', (done) => {
      const newUserDto: CreateUserDto = {
        nombre: 'Pedro',
        apellido: 'López',
        email: 'pedro@example.com',
        telefono: '+57 320 789 1234',
        fechaNacimiento: new Date('1988-03-20')
      };
      const createdUser: User = { id: '3', ...newUserDto };
      
      httpService.createUser.mockReturnValue(of(createdUser));
      httpService.getAllUsers.mockReturnValue(of([]));
      service.loadUsers();
      
      setTimeout(() => {
        service.createUser(newUserDto, (user, error) => {
          expect(user).toEqual(createdUser);
          expect(error).toBeNull();
          expect(service.users().length).toBe(1);
          expect(service.loading()).toBe(false);
          done();
        });
      }, 10);
    });
  });

  describe('updateUser - Happy Path', () => {
    it('should update user successfully and update signal', (done) => {
      const updatedUser: User = { ...mockUsers[0], nombre: 'Juan Carlos' };
      
      httpService.updateUser.mockReturnValue(of(updatedUser));
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      service.loadUsers();
      
      setTimeout(() => {
        service.updateUser(updatedUser, (user, error) => {
          expect(user).toEqual(updatedUser);
          expect(error).toBeNull();
          const localUser = service.getUserByIdLocal('1');
          expect(localUser?.nombre).toBe('Juan Carlos');
          expect(service.loading()).toBe(false);
          done();
        });
      }, 10);
    });
  });

  describe('deleteUser - Happy Path', () => {
    it('should delete user successfully and update signal', (done) => {
      httpService.deleteUser.mockReturnValue(of(void 0));
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      service.loadUsers();
      
      setTimeout(() => {
        const initialCount = service.userCount();
        service.deleteUser('1', (success, error) => {
          expect(success).toBe(true);
          expect(error).toBeNull();
          expect(service.userCount()).toBe(initialCount - 1);
          expect(service.getUserByIdLocal('1')).toBeUndefined();
          expect(service.loading()).toBe(false);
          done();
        });
      }, 10);
    });
  });

  describe('searchUsers - Happy Path', () => {
    it('should search users via HTTP successfully', (done) => {
      const searchResults = [mockUsers[0]];
      httpService.searchUsers.mockReturnValue(of(searchResults));
      
      service.searchUsers('Juan', (users, error) => {
        expect(users).toEqual(searchResults);
        expect(error).toBeNull();
        expect(service.loading()).toBe(false);
        done();
      });
    });

    it('should return all users for empty search term', (done) => {
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      service.loadUsers();
      
      setTimeout(() => {
        service.searchUsers('', (users, error) => {
          expect(users).toEqual(mockUsers);
          expect(error).toBeNull();
          done();
        });
      }, 10);
    });
  });

  describe('searchUsersLocal - Happy Path', () => {
    beforeEach((done) => {
      httpService.getAllUsers.mockReturnValue(of(mockUsers));
      service.loadUsers();
      setTimeout(done, 10);
    });

    it('should return all users for empty search', () => {
      const results = service.searchUsersLocal('');
      expect(results.length).toBe(2);
    });

    it('should search by nombre', () => {
      const results = service.searchUsersLocal('Juan');
      expect(results.length).toBe(1);
      expect(results[0].nombre).toBe('Juan');
    });

    it('should search by apellido', () => {
      const results = service.searchUsersLocal('García');
      expect(results.length).toBe(1);
      expect(results[0].apellido).toBe('García');
    });

    it('should search by email', () => {
      const results = service.searchUsersLocal('maria@');
      expect(results.length).toBe(1);
      expect(results[0].email).toBe('maria@example.com');
    });

    it('should be case insensitive', () => {
      const results = service.searchUsersLocal('JUAN');
      expect(results.length).toBe(1);
    });

    it('should return empty array for no matches', () => {
      const results = service.searchUsersLocal('NoExiste');
      expect(results.length).toBe(0);
    });
  });

  // ==================== SAD PATHS ====================

  describe('loadUsers - Sad Path', () => {
    it('should handle HTTP error and set error signal', (done) => {
      const errorMsg = 'Server error';
      httpService.getAllUsers.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.loadUsers();
      
      setTimeout(() => {
        expect(service.error()).toBe(errorMsg);
        expect(service.loading()).toBe(false);
        // El array puede mantener valores previos en caso de error
        done();
      }, 10);
    });
  });

  describe('getUserById - Sad Path', () => {
    it('should handle 404 error', (done) => {
      const errorMsg = 'User not found';
      httpService.getUserById.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.getUserById('999', (user, error) => {
        expect(user).toBeNull();
        expect(error).toBe(errorMsg);
        expect(service.error()).toBe(errorMsg);
        expect(service.loading()).toBe(false);
        done();
      });
    });
  });

  describe('createUser - Sad Path', () => {
    it('should handle duplicate email error (409)', (done) => {
      const newUserDto: CreateUserDto = {
        nombre: 'Pedro',  
        apellido: 'López',
        email: 'juan@example.com',
        telefono: '+57 320 789 1234',
        fechaNacimiento: new Date('1988-03-20')
      };
      const errorMsg = 'Email already exists';
      
      httpService.getAllUsers.mockReturnValue(of([]));
      service.loadUsers();
      httpService.createUser.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      setTimeout(() => {
        service.createUser(newUserDto, (user, error) => {
          expect(user).toBeNull();
          expect(error).toBe(errorMsg);
          expect(service.error()).toBe(errorMsg);
          expect(service.loading()).toBe(false);
          done();
        });
      }, 10);
    });

    it('should handle validation error (400)', (done) => {
      const invalidUserDto: CreateUserDto = {
        nombre: '',
        apellido: 'López',
        email: 'invalid-email',
        telefono: '123',
        fechaNacimiento: new Date('2030-01-01')
      };
      const errorMsg = 'Validation failed';
      
      httpService.createUser.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.createUser(invalidUserDto, (user, error) => {
        expect(user).toBeNull();
        expect(error).toBe(errorMsg);
        done();
      });
    });
  });

  describe('updateUser - Sad Path', () => {
    it('should handle user not found (404)', (done) => {
      const updatedUser: User = { ...mockUsers[0], id: '999' };
      const errorMsg = 'User not found';
      
      httpService.updateUser.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.updateUser(updatedUser, (user, error) => {
        expect(user).toBeNull();
        expect(error).toBe(errorMsg);
        expect(service.error()).toBe(errorMsg);
        expect(service.loading()).toBe(false);
        done();
      });
    });

    it('should handle duplicate email error', (done) => {
      const updatedUser: User = { ...mockUsers[0], email: 'maria@example.com' };
      const errorMsg = 'Email already exists';
      
      httpService.updateUser.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.updateUser(updatedUser, (user, error) => {
        expect(user).toBeNull();
        expect(error).toBe(errorMsg);
        done();
      });
    });
  });

  describe('deleteUser - Sad Path', () => {
    it('should handle user not found error', (done) => {
      const errorMsg = 'User not found';
      httpService.deleteUser.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.deleteUser('999', (success, error) => {
        expect(success).toBe(false);
        expect(error).toBe(errorMsg);
        expect(service.error()).toBe(errorMsg);
        expect(service.loading()).toBe(false);
        done();
      });
    });
  });

  describe('searchUsers - Sad Path', () => {
    it('should handle HTTP error during search', (done) => {
      const errorMsg = 'Search failed';
      httpService.searchUsers.mockReturnValue(throwError(() => new Error(errorMsg)));
      
      service.searchUsers('test', (users, error) => {
        expect(users).toBeNull();
        expect(error).toBe(errorMsg);
        expect(service.error()).toBe(errorMsg);
        expect(service.loading()).toBe(false);
        done();
      });
    });
  });
});
