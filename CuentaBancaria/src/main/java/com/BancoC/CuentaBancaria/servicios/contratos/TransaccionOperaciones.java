package com.BancoC.CuentaBancaria.servicios.contratos;

import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

public interface TransaccionOperaciones {
    Transaccion nuevaTransaccion(Transaccion transaccion);
    Transaccion obtenerTransaccion(Long transaccionId);
}
