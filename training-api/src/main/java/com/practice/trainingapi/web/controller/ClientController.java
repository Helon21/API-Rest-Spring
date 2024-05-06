package com.practice.trainingapi.web.controller;

import com.practice.trainingapi.entity.Client;
import com.practice.trainingapi.jwt.JwtUserDetails;
import com.practice.trainingapi.repository.projection.ClientProjection;
import com.practice.trainingapi.service.ClientService;
import com.practice.trainingapi.service.UserService;
import com.practice.trainingapi.web.dto.ClientCreateDto;
import com.practice.trainingapi.web.dto.ClientResponseDto;
import com.practice.trainingapi.web.dto.exception.ErrorMessage;
import com.practice.trainingapi.web.dto.mapper.ClientMapper;
import com.practice.trainingapi.web.dto.PageableDto;
import com.practice.trainingapi.web.dto.mapper.PageableMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Clients", description = "Contains all operations related to a client's resource")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Create a new client",
            description = "Recurso para criar um novo cliente vinculado a um usuário cadastrado. " +
                    "Request requires use a new bearer token. Restricted access to Role='CLIENT'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success to create resource",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Client CPF already registered in system",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to missing data or invalid data",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for ADMIN profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDto> create(@RequestBody @Valid ClientCreateDto dto, @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(dto);
        client.setUser(userService.findById(userDetails.getId()));
        clientService.insert(client);
        return ResponseEntity.status(201).body(ClientMapper.toDto(client));
    }

    @Operation(summary = "Find a client", description = "Resource to find a client by ID. " +
            "Request require use a new bearer token. Restricted access to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CLIENT profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDto> getById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }

    @Operation(summary = "Recover a client list",
            description = "Request require use a new bearer token. Restricted access to Role='ADMIN' ",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Represents the returned page"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Represents all elements for each page "
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "nome,asc")),
                            description = "Represents the ordering results. Multiple sorting criteria are supported.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success to recover resource",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='CLIENTE'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"name"}) Pageable pageable) {
        Page<ClientProjection> client = clientService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(client));
    }

    @Operation(summary = "Retrieve authenticated client data",
            description = "Request require use a new bearer token. Restrict access to Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success to recover resource",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClientResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='ADMIN'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/details")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDto> getDetails(@AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.findByUserId(userDetails.getId());
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }
}
