package com.practice.trainingapi.web.dto.mapper;

import com.practice.trainingapi.entity.Vacancy;
import com.practice.trainingapi.web.dto.VacancyCreateDto;
import com.practice.trainingapi.web.dto.VacancyResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VacancyMapper {

    public static Vacancy toVacancy(VacancyCreateDto dto) {
        return new ModelMapper().map(dto, Vacancy.class);
    }

    public static VacancyResponseDto toDto(Vacancy vacancy) {
        return new ModelMapper().map(vacancy, VacancyResponseDto.class);
    }

}
