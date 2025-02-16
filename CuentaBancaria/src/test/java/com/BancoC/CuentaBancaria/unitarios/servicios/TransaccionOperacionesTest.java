package com.BancoC.CuentaBancaria.unitarios.servicios;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.servicios.TransaccionesService;
import com.BancoC.CuentaBancaria.servicios.contratos.TransaccionOperaciones;

/**
 * Requerimientos:
 *  1. La creación de una nueva transacción debe cumplir los siguientes 
 *      requerimientos:
 *      - Clasificar la transacción correctamente (movimiento bancario o
 *          transacción en efectivo).
 *      - La fecha debe ser válida (no debe ser mayor a la fecha actual)
 *  2. Obtener una transacción de la base de datos mediante la `transaccionId`.
 */
public class TransaccionOperacionesTest extends ServiciosTest{

    private TransaccionOperaciones operaciones;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();  //Trae configuraciones de pruebas y mocks de repositorios

        this.operaciones = new TransaccionesService(
            transaccionEfectivoRepository, movimientoRepository
        );
    }

    @Test
    void nuevaTransaccionEfectivo() throws Exception {
        Transaccion transaccionGuardada = this.operaciones.nuevaTransaccion(consignacion);
        this.validacionTransaccion(consignacionBD, transaccionGuardada);

        transaccionGuardada = this.operaciones.nuevaTransaccion(retiro);
        this.validacionTransaccion(retiroBD, transaccionGuardada);
    }

    @Test
    void nuevoMovimiento() throws Exception {
        Transaccion transaccionGuardada = this.operaciones.nuevaTransaccion(movimientoBancario);

        this.validacionTransaccion(movimientoBD, transaccionGuardada);
    }

    @Test
    void nuevaTransaccionInvalidaPorSaldo() {
        //No se deben aceptar montos negativos o iguales a 0.0
        consignacion.setMonto(-1.0);

        Exception exception = assertThrows(
            Exception.class, 
            () -> this.operaciones.nuevaTransaccion(consignacion)
        );

        assertEquals("El monto debe ser mayor que 0.0", 
            exception.getMessage());
    
        //El monto no puede ser 'null'
        consignacion.setMonto(null);

        exception = assertThrows(
            Exception.class, 
            () -> this.operaciones.nuevaTransaccion(consignacion)
        );

        assertEquals("El monto debe ser un número", 
            exception.getMessage());
    }

    @Test
    void nuevaTransaccionInvalidaPorFecha() {
        //Si no hay fecha definida, que tome el momento de ahora y no falle
        consignacion.setFechaCreacion(null);

        assertDoesNotThrow(
            () -> this.operaciones.nuevaTransaccion(consignacion)
        );

        //La fecha es invalida si está por debajo de 2 minutos antes de guardar la transacción
        consignacion.setFechaCreacion(
            LocalDateTime.now().minusMinutes(3)
        );

        Exception exception = assertThrows(
            TimeoutException.class,
            () -> this.operaciones.nuevaTransaccion(consignacion)
        );

        assertEquals("Timeout: se pasó del límite de tiempo", 
            exception.getMessage());

        //La fecha es invalida si está por encima de la fecha actual
        consignacion.setFechaCreacion(
            LocalDateTime.now().plusMinutes(1)
        );

        exception = assertThrows(
            TimeoutException.class,
            () -> this.operaciones.nuevaTransaccion(consignacion)
        );

        assertEquals("Alerta de fraude: la fecha de la transacción fue alterada", 
            exception.getMessage());

    }

    @Test
    void obtenerTransaccionesValidas() throws Exception {
        //Guardamos todas las transacciones
        this.operaciones.nuevaTransaccion(consignacion);
        this.operaciones.nuevaTransaccion(retiro);
        this.operaciones.nuevaTransaccion(movimientoBancario);

        //Validamos respecto al resultado de 'obtenerTransaccion' para cada una
        this.validacionTransaccion(
            consignacionBD,
            this.operaciones.obtenerTransaccion(1L)
        );

        this.validacionTransaccion(
            retiroBD,
            this.operaciones.obtenerTransaccion(2L)
        );

        this.validacionTransaccion(
            movimientoBD,
            this.operaciones.obtenerTransaccion(3L)
        );
    }

    @Test
    void obtenerTransaccionQueNoExiste() {
        assertNull(
            this.operaciones.obtenerTransaccion(50L)
        );
    }

    private void validacionTransaccion(Transaccion referencia, Transaccion transaccionPrueba) {
        assertNotNull(transaccionPrueba);
        assertEquals(referencia.getClass(), transaccionPrueba.getClass());
        assertEquals(referencia.getTransaccionId(), transaccionPrueba.getTransaccionId());
        assertEquals(referencia.getMonto(), transaccionPrueba.getMonto());
        if (referencia.getClass() == TransaccionEfectivo.class) {
            assertEquals(
                ((TransaccionEfectivo) referencia).getTipoTransaccion(),
                ((TransaccionEfectivo) transaccionPrueba).getTipoTransaccion()
            );
        } else if (referencia.getClass() == Movimiento.class) {
            assertEquals(
                ((Movimiento) referencia).getCuentaOrigen(),
                ((Movimiento) transaccionPrueba).getCuentaOrigen()
            );
        }
    }


}
