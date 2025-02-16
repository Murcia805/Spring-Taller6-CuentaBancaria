package com.BancoC.CuentaBancaria.modelos;

import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@DiscriminatorValue("MOVIMIENTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder 
public class Movimiento extends Transaccion{
    @ManyToOne
    @JoinColumn(name = "cuentaOrigenId")
    private CuentaBancaria cuentaOrigen;
}

