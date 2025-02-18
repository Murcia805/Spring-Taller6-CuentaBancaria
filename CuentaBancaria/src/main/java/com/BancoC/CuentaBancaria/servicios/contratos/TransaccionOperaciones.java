package com.BancoC.CuentaBancaria.servicios.contratos;

import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

public interface TransaccionOperaciones {
    Transaccion nuevaTransaccion(Transaccion transaccion) throws Exception;
    void transaccionValida(Transaccion transaccion) throws Exception;
    Transaccion obtenerTransaccion(Long transaccionId);
}
