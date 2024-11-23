package ru.tpu.hostel.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "balance", schema = "administration") // Указание схемы
public class Balance {

    @Id
    @Column(name = "\"user\"", nullable = false) // Поле user как UUID
    private UUID user;

    @Column(name = "balance", nullable = false) // Поле balance не может быть null
    private BigDecimal balance;
}
