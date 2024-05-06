package com.practice.trainingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "customers_has_vacancies")
public class ClientVacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero_recibo", nullable = false, unique = true)
    private String receipt;
    @Column(name = "placa", nullable = false, length = 8)
    private String plate;
    @Column(name = "marca", nullable = false, length = 45)
    private String brand;
    @Column(name = "modelo", nullable = false, length = 45)
    private String model;
    @Column(name = "cor", nullable = false, length = 45)
    private String color;
    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;
    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;
    @Column(name = "valor", columnDefinition = "decimal(7,2)")
    private BigDecimal value;
    @Column(name = "desconto", columnDefinition = "decimal(7,2)")
    private BigDecimal discount;
    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;
    @ManyToOne
    @JoinColumn
    private Vacancy vacancy;

    @CreatedDate
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    @CreatedBy
    @Column(name = "created_by")
    private LocalDateTime createdBy;
    @LastModifiedBy
    @Column(name = "modified_by")
    private LocalDateTime modified_by;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientVacancy that = (ClientVacancy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
