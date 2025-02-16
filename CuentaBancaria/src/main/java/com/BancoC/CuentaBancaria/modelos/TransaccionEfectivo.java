package com.BancoC.CuentaBancaria.modelos;

import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("EFECTIVO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransaccionEfectivo extends Transaccion{
    private String tipoTransaccion;    //Define si es 'consignación' (C) o 'retiro' (R)
}
