package com.BancoC.CuentaBancaria.servicios.contratos;

import java.util.List;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

public interface CuentaBancariaOperaciones {
    CuentaBancaria nuevaCuenta (CuentaBancaria cuentaBancaria, Long clienteId);
    CuentaBancaria transaccion(Long clienteId, Transaccion transaccion); 
    CuentaBancaria obtenerCuenta (Long cuentaId);
    List<CuentaBancaria> obtenerCuentas (Long clienteId);
    Boolean eliminarCuenta(Long clienteId, Long cuentaId);
}
