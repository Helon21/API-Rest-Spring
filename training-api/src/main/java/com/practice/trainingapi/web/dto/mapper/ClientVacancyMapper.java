package com.practice.trainingapi.web.dto.mapper;

import com.practice.trainingapi.entity.ClientVacancy;
import com.practice.trainingapi.web.dto.ParkingCreateDto;
import com.practice.trainingapi.web.dto.ParkingResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientVacancyMapper {

    public static ClientVacancy toClientVacancy(ParkingCreateDto dto) {
        return new ModelMapper().map(dto, ClientVacancy.class);
    }

    public static ParkingResponseDto toDto(ClientVacancy clientVacancy) {
        return new ModelMapper().map(clientVacancy, ParkingResponseDto.class)
    }
}
