package com.practice.trainingapi.service;

import com.practice.trainingapi.entity.Client;
import com.practice.trainingapi.entity.ClientVacancy;
import com.practice.trainingapi.entity.Vacancy;
import com.practice.trainingapi.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ClientVacancyService clientVacancyService;
    private final ClientService clientService;
    private final VacancyService vacancyService;

    @Transactional
    public ClientVacancy checkIn(ClientVacancy clientVacancy) {
        Client client = clientService.findByCpf(clientVacancy.getClient().getCpf());
        clientVacancy.setClient(client);

        Vacancy vacancy = vacancyService.findByFreeVacancy();
        vacancy.setStatus(Vacancy.StatusVacancy.BUSY);
        clientVacancy.setVacancy(vacancy);

        clientVacancy.setEntryDate(LocalDateTime.now());

        clientVacancy.setReceipt(ParkingUtils.generateReceipt());

        return clientVacancyService.insert(clientVacancy);
    }

    @Transactional
    public ClientVacancy checkOut(String receipt) {
        ClientVacancy clientVacancy = clientVacancyService.findByReceipt(receipt);

        LocalDateTime departureDate = LocalDateTime.now();

        BigDecimal value = ParkingUtils.calculateCost(clientVacancy.getEntryDate(), departureDate);
        clientVacancy.setValue(value);

        long totalTimes = clientVacancyService.getTotalParkedTimesComplete(clientVacancy.getClient().getCpf());

        BigDecimal discount = ParkingUtils.calculateDiscount(value, totalTimes);
        clientVacancy.setDiscount(discount);

        clientVacancy.setDepartureDate(departureDate);
        clientVacancy.getVacancy().setStatus(Vacancy.StatusVacancy.FREE);

        return clientVacancyService.insert(clientVacancy);
    };
}
