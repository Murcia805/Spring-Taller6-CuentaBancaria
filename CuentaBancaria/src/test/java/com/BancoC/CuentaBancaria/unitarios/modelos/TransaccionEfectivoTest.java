package com.BancoC.CuentaBancaria.unitarios.modelos;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;

public class TransaccionEfectivoTest extends ModelosTest{

    @Test
    public void crearTransaccion() {
        this.validacionesConsignacion((TransaccionEfectivo) consignacion);
    }

    @Test
    public void obtenerUnaTransaccion() {
        this.validacionesConsignacion(
            transaccionEfectivoRepository.findById(consignacion.getTransaccionId()).get()
        );
    }

    private void validacionesConsignacion(TransaccionEfectivo transaccion) {
        assertNotNull(transaccion);
        assertEquals(consignacion.getTransaccionId(), transaccion.getTransaccionId());
        assertEquals(50_000.0, transaccion.getMonto());
        assertEquals(cuentaAhorrosLeonardo, transaccion.getCuentaDestino());
        assertEquals("C", transaccion.getTipoTransaccion());
    }
}
