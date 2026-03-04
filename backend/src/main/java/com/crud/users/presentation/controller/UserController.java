package com.crud.users.presentation.controller;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.port.in.CreateUserUseCase;
import com.crud.users.application.port.in.DeleteUserUseCase;
import com.crud.users.application.port.in.GetAllUsersUseCase;
import com.crud.users.application.port.in.GetUserByIdUseCase;
import com.crud.users.application.port.in.SearchUsersUseCase;
import com.crud.users.application.port.in.UpdateUserUseCase;
import com.crud.users.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para operaciones CRUD de usuarios.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost", "http://localhost:80"})
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UserController {

    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final SearchUsersUseCase searchUsersUseCase;

    public UserController(
            GetAllUsersUseCase getAllUsersUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            SearchUsersUseCase searchUsersUseCase) {
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.searchUsersUseCase = searchUsersUseCase;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna la lista completa de usuarios registrados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = getAllUsersUseCase.execute();
        ApiResponse<List<UserResponseDto>> response = ApiResponse.successList(
                users, 
                "Usuarios obtenidos exitosamente", 
                (long) users.size()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico por su identificador")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @Parameter(description = "ID del usuario") @PathVariable String id) {
        UserResponseDto user = getUserByIdUseCase.execute(id);
        ApiResponse<UserResponseDto> response = ApiResponse.success(
                user, 
                "Usuario encontrado exitosamente"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre, apellido o email")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> searchUsers(
            @Parameter(description = "Término de búsqueda") @RequestParam String q) {
        List<UserResponseDto> users = searchUsersUseCase.execute(q);
        ApiResponse<List<UserResponseDto>> response = ApiResponse.successList(
                users, 
                "Búsqueda completada exitosamente", 
                (long) users.size()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario con los datos proporcionados")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserResponseDto createdUser = createUserUseCase.execute(createUserDto);
        
        // Crear URI del recurso creado
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        
        ApiResponse<UserResponseDto> response = ApiResponse.created(
                createdUser, 
                "Usuario creado exitosamente"
        );
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El email ya está registrado por otro usuario")
    })
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @Parameter(description = "ID del usuario") @PathVariable String id,
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        
        // Asegurar que el ID del path coincida con el del body
        updateUserDto.setId(id);

        UserResponseDto updatedUser = updateUserUseCase.execute(id, updateUserDto);
        ApiResponse<UserResponseDto> response = ApiResponse.success(
                updatedUser, 
                "Usuario actualizado exitosamente"
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "ID del usuario") @PathVariable String id) {
        deleteUserUseCase.execute(id);
        ApiResponse<Void> response = ApiResponse.noContent("Usuario eliminado exitosamente");
        return ResponseEntity.ok(response);
    }
}
