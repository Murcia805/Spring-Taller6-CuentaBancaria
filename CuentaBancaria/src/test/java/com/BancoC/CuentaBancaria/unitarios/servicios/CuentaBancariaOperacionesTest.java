package com.BancoC.CuentaBancaria.unitarios.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.servicios.CuentaBancariaService;
import com.BancoC.CuentaBancaria.servicios.contratos.CuentaBancariaOperaciones;

public class CuentaBancariaOperacionesTest extends ServiciosTest {

    private CuentaBancariaOperaciones operaciones;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        this.operaciones = new CuentaBancariaService(
            cuentaBancariaRepository, transaccionOperaciones);
    }

    @Test
    void nuevaCuentaValida() throws Exception {
        CuentaBancaria cuentaNuevaLeonardo = operaciones.nuevaCuenta(cuentaAhorrosLeonardo, Leonardo.getClienteId());
        this.validacionesCuenta(cuentaLeonardoBD, cuentaNuevaLeonardo);

        CuentaBancaria cuentaNuevaFlorinda = operaciones.nuevaCuenta(cuentaCorrienteFlorinda, Florinda.getClienteId());
        this.validacionesCuenta(cuentaFlorindaBD, cuentaNuevaFlorinda);
    }

    @Test
    void nuevaCuentaInvalida() {
        //Si el 'clienteId' es diferente al de la cuenta, no debería guardarla
        Exception exception = assertThrows(
            Exception.class,
            () -> operaciones.nuevaCuenta(cuentaAhorrosLeonardo, 133L)
        );

        assertEquals("Alerta de fraude: se intenta vincular una cuenta a un cliente diferente", 
            exception.getMessage());

        //El saldo de una nueva cuenta debe ser 0.0
        cuentaAhorrosLeonardo.setSaldo(100_000_000.0);

        exception = assertThrows(
            Exception.class,
            () -> operaciones.nuevaCuenta(cuentaAhorrosLeonardo, 1L)
        );

        assertEquals("Alerta de fraude: una cuenta nueva no puede tener un saldo diferente a 0.0", 
            exception.getMessage());
    }

    @Test
    void obtenerCuentaExistente() {
        //Obtener por id
        CuentaBancaria cuentaObtenida = operaciones.obtenerCuenta(cuentaLeonardoBD.getCuentaId());
        this.validacionesCuenta(cuentaLeonardoBD, cuentaObtenida);

        cuentaObtenida = operaciones.obtenerCuenta(cuentaFlorindaBD.getCuentaId());
        this.validacionesCuenta(cuentaFlorindaBD, cuentaObtenida);

        //Obtener por 'numeroCuenta'
        cuentaObtenida = operaciones.obtenerCuenta(cuentaAhorrosLeonardo.getNumeroCuenta());
        this.validacionesCuenta(cuentaLeonardoBD, cuentaObtenida);

        cuentaObtenida = operaciones.obtenerCuenta(cuentaCorrienteFlorinda.getNumeroCuenta());
        this.validacionesCuenta(cuentaFlorindaBD, cuentaObtenida);

    }

    @Test
    void obtenerCuentaNoExiste() {
        assertNull(operaciones.obtenerCuenta(54L));
        assertNull(operaciones.obtenerCuenta("555-5555"));
    }

    @Test
    void transaccionValidaYObtenerCuenta() throws Exception {
        //Transacciones en efectivo
        CuentaBancaria cuentaActualizada =  operaciones.transaccion(consignacion);
        assertEquals(150_000.0, cuentaActualizada.getSaldo());
        verify(cuentaBancariaRepository, times(1)).save(cuentaActualizada);
    
        cuentaActualizada =  operaciones.transaccion(retiro);
        assertEquals(240_000.0, cuentaActualizada.getSaldo());
        verify(cuentaBancariaRepository, times(1)).save(cuentaActualizada);

        //Movimiento bancario
        cuentaActualizada = operaciones.transaccion(movimientoBancario);
        assertEquals(100_000.0, cuentaActualizada.getSaldo());
        verify(cuentaBancariaRepository, times(2)).save(cuentaActualizada);
    }

    @Test
    void transaccionInvalidaPorSaldoInsuficiente() throws Exception {
        //Transacción en efectivo con saldo insuficiente
        retiro.setMonto(100_000_000_000.0);

        Exception exception = assertThrows(
            Exception.class,
            () -> operaciones.transaccion(retiro)
        );

        assertEquals("No se tiene el saldo suficiente para aprobar ese retiro", 
            exception.getMessage());
        verify(transaccionOperaciones, times(0)).nuevaTransaccion(retiro);
        
        //Movimiento bancario con saldo insuficiente
        movimientoBancario.setMonto(100_000_000_000.0);

        exception = assertThrows(
            Exception.class,
            () -> operaciones.transaccion(movimientoBancario)
        );

        assertEquals("No se tiene el saldo suficiente para aprobar ese retiro", 
            exception.getMessage());
        verify(transaccionOperaciones, times(0)).nuevaTransaccion(movimientoBancario);
    }

    @Test
    void obtenerCuentas() {
        List<CuentaBancaria> cuentasLeonardo = operaciones.obtenerCuentas(Leonardo.getClienteId());

        assertNotNull(cuentasLeonardo);
        assertEquals(1, cuentasLeonardo.size());
        assertTrue(cuentasLeonardo.contains(cuentaLeonardoBD));
    }

    @Test
    void eliminarCuenta() throws Exception {
        cuentaLeonardoBD.setSaldo(0.0);
        Boolean eliminado = operaciones.eliminarCuenta(Leonardo.getClienteId(), 
            cuentaAhorrosLeonardo.getNumeroCuenta());
        assertTrue(eliminado);
        verify(cuentaBancariaRepository, times(1)).delete(cuentaLeonardoBD);
    }

    @Test
    void eliminarCuentaFraude() {
        Exception exception = assertThrows(
            Exception.class,
            () -> operaciones.eliminarCuenta(Florinda.getClienteId(), 
                cuentaAhorrosLeonardo.getNumeroCuenta())
        );

        assertEquals("Alerta de fraude: el cliente con id=2 intenta eliminar una cuenta que no es de su propiedad (cuentaId=1)", 
            exception.getMessage());
        verify(cuentaBancariaRepository, times(0)).delete(cuentaLeonardoBD);
    }

    @Test
    void eliminarCuentaSaldoMayorACero() throws Exception {
        Boolean eliminado = operaciones.eliminarCuenta(Leonardo.getClienteId(), 
            cuentaAhorrosLeonardo.getNumeroCuenta());
        assertFalse(eliminado);
        verify(cuentaBancariaRepository, times(0)).delete(cuentaLeonardoBD);
    }

    private void validacionesCuenta(CuentaBancaria cuentaReferencia, CuentaBancaria cuentaEvaluar) {
        assertNotNull(cuentaEvaluar);
        assertEquals(cuentaReferencia.getCuentaId(), cuentaEvaluar.getCuentaId());
        assertEquals(cuentaReferencia.getNumeroCuenta(), cuentaEvaluar.getNumeroCuenta());
        assertEquals(cuentaReferencia.getSaldo(), cuentaEvaluar.getSaldo());
    }
}
