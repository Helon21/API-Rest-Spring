package com.practice.trainingapi.web.controller;

import com.practice.trainingapi.entity.ClientVacancy;
import com.practice.trainingapi.jwt.JwtUserDetails;
import com.practice.trainingapi.repository.projection.ClientVacancyProjection;
import com.practice.trainingapi.service.ClientVacancyService;
import com.practice.trainingapi.service.ParkingService;
import com.practice.trainingapi.web.dto.PageableDto;
import com.practice.trainingapi.web.dto.ParkingCreateDto;
import com.practice.trainingapi.web.dto.ParkingResponseDto;
import com.practice.trainingapi.web.dto.exception.ErrorMessage;
import com.practice.trainingapi.web.dto.mapper.ClientVacancyMapper;
import com.practice.trainingapi.web.dto.mapper.PageableMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Parking", description = "Operations for registering a vehicle in and out of the parking lot")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-lots")
public class ParkingController {

    private final ParkingService parkingService;
    private final ClientVacancyService clientVacancyService;

    @Operation(summary = "Check-in operation", description = "Resource for entering a vehicle into the parking lot. " +
            "Request requires use a bearer token. Restrict access to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success to create resource",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to access the created resource"),
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Possible causes: <br/>" +
                            "- Client CPF not registered in the system; <br/>" +
                            "- No free vacancies were found;",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to missing data or invalid data",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='CLIENTE'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkIn(@RequestBody @Valid ParkingCreateDto dto) {
        ClientVacancy clientVacancy = ClientVacancyMapper.toClientVacancy(dto);
        parkingService.checkIn(clientVacancy);
        ParkingResponseDto responseDto = ClientVacancyMapper.toDto(clientVacancy);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(clientVacancy.getReceipt())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    ;

    @Operation(summary = "Locate a parked vehicle", description = "Feature for returning a parked vehicle " +
            "by receipt number. Request requires use a bearer token",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Number of receipt generate by check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Number of receipt not found.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', CLIENT)")
    public ResponseEntity<ParkingResponseDto> getByReceipt(@PathVariable String receipt) {
        ClientVacancy clientVacancy = clientVacancyService.findByReceipt(receipt);
        ParkingResponseDto dto = ClientVacancyMapper.toDto(clientVacancy);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Checkout operation", description = "Resource for leaving a vehicle from the parking a lot. " +
            "Request requires use a bearer token. Restrict access to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {@Parameter(in = PATH, name = "receipt", description = "Number of receipt generate by check-in",
                    required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully to update resource",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Number of receipt not exist or " +
                            "the vehicle has already gone through checkout.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='CLIENTE'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ParkingResponseDto> checkOut(@PathVariable String receipt) {
        ClientVacancy clientVacancy = parkingService.checkOut(receipt);
        ParkingResponseDto dto = ClientVacancyMapper.toDto(clientVacancy);
        return ResponseEntity.ok(dto);
    }

    ;

    @Operation(summary = "Find client parking records by CPF", description = "Locate the " +
            "parking register by client cpf. Request requires use a bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "CPF number referring to the client consulted",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Represents the returned page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Represents all of elements for each page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Default sort field 'entryDate,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='CLIENTE'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAllParkingsByCpf(@PathVariable String cpf,
                                                           @Parameter(hidden = true) @PageableDefault(size = 5, sort = "entryDate",
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientVacancyProjection> projection = clientVacancyService.findAllByClientCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Find logged in clients parking records",
            description = "Find logged in clients parking records. " +
                    "Request requires use a bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Represents the returned page"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Represents all elements for each page"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            description = "Default sort field 'entryDate,asc'. ")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to Role='ADMIN'",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageableDto> getAllParkingClient(@AuthenticationPrincipal JwtUserDetails user,
                                                           @Parameter(hidden = true) @PageableDefault(
                                                                   size = 5, sort = "entryDate",
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientVacancyProjection> projection = clientVacancyService.findAllByUserId(user.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }
}