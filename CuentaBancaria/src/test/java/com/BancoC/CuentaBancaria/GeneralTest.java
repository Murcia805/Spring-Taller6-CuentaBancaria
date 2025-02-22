package com.BancoC.CuentaBancaria;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.modelos.ext.Cliente;

/**
 * Contiene todos los objetos de pruebas para cualquier capa de desarrollo
 */
public abstract class GeneralTest {
    protected Cliente Leonardo;
    protected Cliente Florinda;

    protected CuentaBancaria cuentaAhorrosLeonardo;
    protected CuentaBancaria cuentaCorrienteFlorinda;
    protected CuentaBancaria cuentaFraudulenta;

    protected Transaccion consignacion;
    protected Transaccion retiro;
    protected Transaccion movimientoBancario;

    @BeforeEach
    public void setUp() throws Exception{
        //Clientes
        Leonardo = Cliente.builder()
            .clienteId(1L)
            .nombre("Leonardo Mesa")
            .build();
        
        Florinda = Cliente.builder()
            .clienteId(2L)
            .nombre("Florinda Pinzon")
            .build();
        
        //Cuentas bancarias
        cuentaAhorrosLeonardo = CuentaBancaria.builder()
            .cliente(Leonardo)
            .clienteId(Leonardo.getClienteId())
            .numeroCuenta("53627273")
            .fechaCreacion(LocalDate.now())
            .saldo(100_000.0)
            .build();
        
        cuentaCorrienteFlorinda = CuentaBancaria.builder()
            .cliente(Florinda)
            .clienteId(Florinda.getClienteId())
            .numeroCuenta("4843827238")
            .fechaCreacion(LocalDate.now())
            .saldo(250_000.0)
            .build();
        
        cuentaFraudulenta = CuentaBancaria.builder()
            .clienteId(500L)
            .numeroCuenta("1022222222")
            .fechaCreacion(LocalDate.now())
            .saldo(250_000_000.0)
            .build();  

        //Transacciones
        consignacion = TransaccionEfectivo.builder()
            .monto(50_000.0)
            .cuentaDestino(cuentaAhorrosLeonardo)
            .fechaCreacion(LocalDateTime.now())
            .tipoTransaccion("C")
            .build();
        
        retiro = TransaccionEfectivo.builder()
            .monto(10_000.0)
            .cuentaDestino(cuentaCorrienteFlorinda)
            .fechaCreacion(LocalDateTime.now())
            .tipoTransaccion("R")
            .build();

        movimientoBancario = Movimiento.builder()
            .cuentaOrigen(cuentaAhorrosLeonardo)
            .cuentaDestino(cuentaCorrienteFlorinda)
            .monto(70_000.0)
            .fechaCreacion(LocalDateTime.now())
            .build();
        
    }
}
