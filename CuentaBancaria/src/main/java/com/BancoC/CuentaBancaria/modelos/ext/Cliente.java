package com.BancoC.CuentaBancaria.modelos.ext;

import java.util.List;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cliente {
    private Long clienteId;
    private String nombre;
    private List<CuentaBancaria> cuentas;
}
