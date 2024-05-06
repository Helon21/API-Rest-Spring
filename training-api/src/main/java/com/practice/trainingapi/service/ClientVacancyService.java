package com.practice.trainingapi.service;

import com.practice.trainingapi.entity.ClientVacancy;
import com.practice.trainingapi.exception.EntityNotFoundException;
import com.practice.trainingapi.repository.ClientVacancyRepository;
import com.practice.trainingapi.repository.projection.ClientVacancyProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientVacancyService {

    private final ClientVacancyRepository clientVacancyRepository;

    @Transactional
    public ClientVacancy insert(ClientVacancy clientVacancy) {
        return clientVacancyRepository.save(clientVacancy);
    }

    @Transactional(readOnly = true)
    public ClientVacancy findByReceipt(String receipt) {
        return clientVacancyRepository.findByReceiptAndDepartureDateIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(String.format("Receipt '%s' not found in system or checkout already done", receipt))
        );
    }

    @Transactional(readOnly = true)
    public long getTotalParkedTimesComplete(String cpf) {
        return clientVacancyRepository.countByClientCpfAndDepartureDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<ClientVacancyProjection> findAllByClientCpf(String cpf, Pageable pageable) {
        return clientVacancyRepository.findAllByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientVacancyProjection> findAllByUserId(Long id, Pageable pageable) {
        return clientVacancyRepository.findAllByClientUserId(id, pageable);
    }
}
