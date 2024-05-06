package com.practice.trainingapi.web.controller;

import com.practice.trainingapi.entity.Vacancy;
import com.practice.trainingapi.service.VacancyService;
import com.practice.trainingapi.web.dto.VacancyCreateDto;
import com.practice.trainingapi.web.dto.VacancyResponseDto;
import com.practice.trainingapi.web.dto.exception.ErrorMessage;
import com.practice.trainingapi.web.dto.mapper.VacancyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Vacancy", description = "Contains all operations related to a vacancy resource")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/vacacies")
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Create a new vacancy", description = "Resource to create a new vacancy." +
            "Request require use a bearer token. Restrict access to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success to create resource",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL of created resource")),
                    @ApiResponse(responseCode = "409", description = "Vacancy already registered",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to missing or invalid data",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed Role='CLIENTE'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> insert(@RequestBody @Valid VacancyCreateDto dto) {
        Vacancy vacancy = VacancyMapper.toVacancy(dto);
        vacancyService.save(vacancy);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{code}")
                .buildAndExpand(vacancy.getCode())
                .toUri();
        return ResponseEntity.created(location).build();
    };

    @Operation(summary = "Find vacancy", description = "Resource to return vacancy using your code" +
            "Request require use a bearer token. Restrict access to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success to create resource",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = VacancyResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Vacation not found",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='CLIENTE'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VacancyResponseDto> getByCode(@PathVariable String code) {
        Vacancy vacancy = vacancyService.findByCode(code);
        return ResponseEntity.ok(VacancyMapper.toDto(vacancy));
    }
}