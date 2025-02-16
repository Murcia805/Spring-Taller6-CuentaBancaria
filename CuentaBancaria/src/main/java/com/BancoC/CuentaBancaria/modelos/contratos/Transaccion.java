package com.BancoC.CuentaBancaria.modelos.contratos;

import java.time.LocalDateTime;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transaccionId;
    
    protected Double monto;
    protected LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "cuentaDestinoId")
    protected CuentaBancaria cuentaDestino;
}
