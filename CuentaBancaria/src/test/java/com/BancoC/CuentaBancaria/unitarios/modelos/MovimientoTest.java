package com.BancoC.CuentaBancaria.unitarios.modelos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

public class MovimientoTest extends ModelosTest {

    @Test
    public void crearMovimiento() {
        this.validacionesMovimiento(movimientoBancario);
    }

    @Test
    public void obtenerUnMovimiento() {
        this.validacionesMovimiento(
            movimientoRepository.findById(movimientoBancario.getTransaccionId()).get()
        );
    }

    private void validacionesMovimiento(Transaccion movimiento) {
        assertNotNull(movimiento);
        assertEquals(Movimiento.class, movimiento.getClass());
        assertEquals(movimientoBancario.getTransaccionId(), movimiento.getTransaccionId());
        assertEquals(70_000.0, movimiento.getMonto());
        assertEquals(cuentaAhorrosLeonardo, ((Movimiento) movimiento).getCuentaOrigen());
        assertEquals(cuentaCorrienteFlorinda, movimiento.getCuentaDestino());
    }
}
