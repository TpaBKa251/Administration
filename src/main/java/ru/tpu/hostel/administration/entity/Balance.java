package ru.tpu.hostel.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "balance")
public class Balance {

    @Id
    @Column(name = "user", nullable = false)
    private UUID user;

    @Column(name = "balance")
    private BigDecimal balance;
}
