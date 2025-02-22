package com.BancoC.CuentaBancaria.servicios.contratos;

import java.util.List;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

public interface CuentaBancariaOperaciones {
    CuentaBancaria nuevaCuenta (CuentaBancaria cuentaBancaria, Long clienteId) throws Exception;
    Transaccion transaccion(Transaccion transaccion) throws Exception; 
    CuentaBancaria obtenerCuenta (Long cuentaId);
    CuentaBancaria obtenerCuenta(String numeroCuenta);
    List<CuentaBancaria> obtenerCuentas (Long clienteId);
    Boolean eliminarCuenta(Long clienteId, String numeroCuenta) throws Exception;
}
