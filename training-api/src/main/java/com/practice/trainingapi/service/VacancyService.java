package com.practice.trainingapi.service;

import com.practice.trainingapi.entity.Vacancy;
import com.practice.trainingapi.exception.CodeUniqueViolationException;
import com.practice.trainingapi.exception.EntityNotFoundException;
import com.practice.trainingapi.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.practice.trainingapi.entity.Vacancy.StatusVacancy.FREE;

@RequiredArgsConstructor
@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    @Transactional
    public Vacancy save(Vacancy vacancy) {
        try {
            return vacancyRepository.save(vacancy);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueViolationException(String.format("Vacancy with code '%s' already registered", vacancy.getCode()));
        }
    }

    @Transactional(readOnly = true)
    public Vacancy findByCode(String code) {
        return vacancyRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy with code '%s' not found", code))
        );
    }

    @Transactional(readOnly = true)
    public Vacancy findByFreeVacancy() {
        return vacancyRepository.findFirstByStatus(FREE).orElseThrow(
                () -> new EntityNotFoundException("cannot found a vacancy free")
        );
    }
}
