package com.practice.trainingapi.service;

import com.practice.trainingapi.entity.Client;
import com.practice.trainingapi.exception.CpfUniqueViolationException;
import com.practice.trainingapi.exception.EntityNotFoundException;
import com.practice.trainingapi.repository.ClientRepository;
import com.practice.trainingapi.repository.projection.ClientProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client insert(Client client) {
        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("CPF '%s', cannot be registered, already exists in system", client.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Client id=%s cannot found in system", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<ClientProjection> findAll(Pageable pageable) {
        return clientRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Client findByUserId(Long id) {
        return clientRepository.findByUserId(id);
    }

    @Transactional(readOnly = true)
    public Client findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Client with this CPF '%s', not found", cpf))
        );
    }
}
