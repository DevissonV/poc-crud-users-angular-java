import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserHttpService } from './user-http.service';
import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';

function mockApiResponse<T>(data: T, totalItems = 0) {
  return {
    data,
    metadata: {
      status: 200,
      message: 'OK',
      timestamp: new Date().toISOString(),
      totalItems
    }
  };
}

/**
 * Suite de pruebas para UserHttpService
 * 
 * Verifica el correcto funcionamiento de las operaciones HTTP
 * con el backend, incluyendo transformación de datos y manejo de errores.
 */
describe('UserHttpService', () => {
  let service: UserHttpService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/users`;

  const mockUserResponse = {
    id: '1',
    nombre: 'Juan',
    apellido: 'Pérez',
    email: 'juan@example.com',
    telefono: '+57 300 123 4567',
    fechaNacimiento: '1990-01-15'
  };

  const mockUsersResponse = [
    mockUserResponse,
    {
      id: '2',
      nombre: 'María',
      apellido: 'García',
      email: 'maria@example.com',
      telefono: '+57 310 456 7890',
      fechaNacimiento: '1995-05-20'
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserHttpService]
    });

    service = TestBed.inject(UserHttpService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllUsers', () => {
    it('should fetch all users and map dates', (done) => {
      service.getAllUsers().subscribe({
        next: (users) => {
          expect(users.length).toBe(2);
          expect(users[0].nombre).toBe('Juan');
          expect(users[1].nombre).toBe('María');
          expect(users[0].fechaNacimiento).toBeInstanceOf(Date);
          expect(users[1].fechaNacimiento).toBeInstanceOf(Date);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse(mockUsersResponse, 2));
    });

    it('should handle empty response', (done) => {
      service.getAllUsers().subscribe({
        next: (users) => {
          expect(users).toEqual([]);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(mockApiResponse([], 0));
    });

    it('should handle HTTP error', (done) => {
      service.getAllUsers().subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('500');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getUserById', () => {
    it('should fetch user by id and map date', (done) => {
      service.getUserById('1').subscribe({
        next: (user) => {
          expect(user.id).toBe('1');
          expect(user.nombre).toBe('Juan');
          expect(user.fechaNacimiento).toBeInstanceOf(Date);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse(mockUserResponse));
    });

    it('should handle 404 error', (done) => {
      service.getUserById('999').subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('404');
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('createUser', () => {
    const newUser: Omit<User, 'id'> = {
      nombre: 'Pedro',
      apellido: 'López',
      email: 'pedro@example.com',
      telefono: '+57 320 789 1234',
      fechaNacimiento: new Date('1988-03-20')
    };

    it('should create user and convert date to ISO string', (done) => {
      service.createUser(newUser).subscribe({
        next: (user) => {
          expect(user.nombre).toBe('Pedro');
          expect(user.fechaNacimiento).toBeInstanceOf(Date);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.fechaNacimiento).toBe('1988-03-20');
      expect(req.request.body.nombre).toBe('Pedro');
      
      req.flush(mockApiResponse({ ...newUser, id: '3', fechaNacimiento: '1988-03-20' }));
    });

    it('should handle validation errors', (done) => {
      service.createUser(newUser).subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('validación');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(
        {
          message: 'Error de validación',
          validationErrors: {
            email: 'Email ya existe',
            telefono: 'Teléfono inválido'
          }
        },
        { status: 400, statusText: 'Bad Request' }
      );
    });

    it('should handle 409 conflict error', (done) => {
      service.createUser(newUser).subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('El email ya está registrado');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(
        { message: 'El email ya está registrado' },
        { status: 409, statusText: 'Conflict' }
      );
    });
  });

  describe('updateUser', () => {
    const updatedUser: User = {
      id: '1',
      nombre: 'Juan Actualizado',
      apellido: 'Pérez',
      email: 'juan.updated@example.com',
      telefono: '+57 300 123 4567',
      fechaNacimiento: new Date('1990-01-15')
    };

    it('should update user and convert date to ISO string', (done) => {
      service.updateUser(updatedUser).subscribe({
        next: (user) => {
          expect(user.nombre).toBe('Juan Actualizado');
          expect(user.fechaNacimiento).toBeInstanceOf(Date);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body.fechaNacimiento).toBe('1990-01-15');
      expect(req.request.body.id).toBe('1');
      
      req.flush(mockApiResponse({ ...updatedUser, fechaNacimiento: '1990-01-15' }));
    });

    it('should handle update errors', (done) => {
      service.updateUser(updatedUser).subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      req.flush(
        { message: 'Error al actualizar' },
        { status: 500, statusText: 'Internal Server Error' }
      );
    });
  });

  describe('deleteUser', () => {
    it('should delete user', (done) => {
      service.deleteUser('1').subscribe({
        next: () => {
          expect(true).toBe(true); // Success
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle delete errors', (done) => {
      service.deleteUser('1').subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      req.flush(
        { message: 'No se puede eliminar' },
        { status: 400, statusText: 'Bad Request' }
      );
    });
  });

  describe('searchUsers', () => {
    it('should search users by term', (done) => {
      service.searchUsers('Juan').subscribe({
        next: (users) => {
          expect(users.length).toBe(1);
          expect(users[0].nombre).toBe('Juan');
          expect(users[0].fechaNacimiento).toBeInstanceOf(Date);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/search?q=Juan`);
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('q')).toBe('Juan');
      req.flush(mockApiResponse([mockUserResponse], 1));
    });

    it('should return empty array when no results', (done) => {
      service.searchUsers('nonexistent').subscribe({
        next: (users) => {
          expect(users).toEqual([]);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/search?q=nonexistent`);
      req.flush(mockApiResponse([], 0));
    });

    it('should handle search errors', (done) => {
      service.searchUsers('test').subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/search?q=test`);
      req.flush('Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('error handling', () => {
    it('should handle network errors', (done) => {
      service.getAllUsers().subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('conectar con el servidor');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });
    });

    it('should extract error message from response', (done) => {
      service.getAllUsers().subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Custom error message');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(
        { message: 'Custom error message' },
        { status: 400, statusText: 'Bad Request' }
      );
    });

    it('should format validation errors', (done) => {
      service.createUser({
        nombre: '',
        apellido: '',
        email: 'invalid',
        telefono: '123',
        fechaNacimiento: new Date()
      }).subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('Errores de validación');
          expect(error.message).toContain('Email inválido');
          expect(error.message).toContain('Teléfono muy corto');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(
        {
          validationErrors: {
            email: 'Email inválido',
            telefono: 'Teléfono muy corto'
          }
        },
        { status: 400, statusText: 'Bad Request' }
      );
    });

    it('should use status text when no message available', (done) => {
      service.getAllUsers().subscribe({
        next: () => done.fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('403');
          expect(error.message).toContain('Forbidden');
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(null, { status: 403, statusText: 'Forbidden' });
    });
  });

  describe('data mapping', () => {
    it('should correctly map response to User with Date object', (done) => {
      service.getUserById('1').subscribe({
        next: (user) => {
          expect(user.fechaNacimiento).toBeInstanceOf(Date);
          expect(user.fechaNacimiento.getUTCFullYear()).toBe(1990);
          expect(user.fechaNacimiento.getUTCMonth()).toBe(0); // January
          expect(user.fechaNacimiento.getUTCDate()).toBe(15);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      req.flush(mockApiResponse(mockUserResponse));
    });

    it('should correctly map User to request with ISO date string', (done) => {
      const user: User = {
        id: '1',
        nombre: 'Test',
        apellido: 'User',
        email: 'test@example.com',
        telefono: '+57 300 123 4567',
        fechaNacimiento: new Date('1995-12-25')
      };

      service.updateUser(user).subscribe({
        next: () => done(),
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.body.fechaNacimiento).toBe('1995-12-25');
      req.flush(mockApiResponse({ ...user, fechaNacimiento: '1995-12-25' }));
    });
  });
});
