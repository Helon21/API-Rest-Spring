package com.practice.trainingapi.web.controller;

import com.practice.trainingapi.entity.User;
import com.practice.trainingapi.service.UserService;
import com.practice.trainingapi.web.dto.UserCreateDTO;
import com.practice.trainingapi.web.dto.UserPasswordDTO;
import com.practice.trainingapi.web.dto.UserResponseDTO;
import com.practice.trainingapi.web.dto.exception.ErrorMessage;
import com.practice.trainingapi.web.dto.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Have methods to find, create, update or delete users")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }

    @Operation(summary = "Find user by id", description = "Recover an user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource recovered with success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @Operation(summary = "Create a new user", description = "Resource to create a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created with success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "e-mail already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource cannot be processed because of invalid input datas",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> insert(@Valid @RequestBody UserCreateDTO userCreateDto) {
        User newUser = userService.insert(UserMapper.toUser(userCreateDto));
        return ResponseEntity.status(HttpStatus.FOUND).body(UserMapper.toDto(newUser));
    }

    @Operation(summary = "Update password", description = "Resource to update password",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password updated with success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "Password does not match",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/update-password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDTO passwordDTO) {
        userService.updatePassword(id, passwordDTO.getCurrentPassword(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }
}
