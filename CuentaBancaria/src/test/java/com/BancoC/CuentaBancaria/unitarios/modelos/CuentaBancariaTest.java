package com.BancoC.CuentaBancaria.unitarios.modelos;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

/**
 * OBJETIVOS: se debe habilitar las siguientes operaciones:
 *            - crear
 *            - obtener UNA cuenta
 *            - obtener TODAS las cuentas de UN cliente
 *            - eliminar
 */
public class CuentaBancariaTest extends ModelosTest{

    @Test
    public void crearYObtenerUnaCuenta() {
        this.validacionesCuenta(
            cuentaCorrienteFlorinda,
            cuentaBancariaRepository.findById(cuentaCorrienteFlorinda.getCuentaId()).get()
        );

        this.validacionesCuenta(
            cuentaAhorrosLeonardo,
            cuentaBancariaRepository.findById(cuentaAhorrosLeonardo.getCuentaId()).get()
        );
    }

    @Test
    public void obtenerTodasCuentasUnCliente() {
        CuentaBancaria cuentaCorrienteLeonardo = this.cuentaBancariaRepository.save(CuentaBancaria.builder()
            .cliente(Leonardo)
            .clienteId(1L)
            .fechaCreacion(LocalDate.now())
            .saldo(0.0)
            .build()
        );

        List<CuentaBancaria> cuentasLeonardo = this.cuentaBancariaRepository.findByClienteId(1L);

        //Validaciones
        assertEquals(2, cuentasLeonardo.size());
        assertEquals(3, cuentaBancariaRepository.findAll().size());
        for (CuentaBancaria cuentaBancaria : cuentasLeonardo) {
            if (cuentaBancaria.getCuentaId() == cuentaCorrienteLeonardo.getCuentaId()) {
                this.validacionesCuenta(cuentaBancaria, cuentaCorrienteLeonardo);
            } else {
                this.validacionesCuenta(cuentaBancaria, cuentaAhorrosLeonardo);
            }
        }
    }

    @Test
    public void eliminarCuenta() {
        this.cuentaBancariaRepository.deleteById(cuentaAhorrosLeonardo.getCuentaId());

        assertTrue(cuentaBancariaRepository.findById(cuentaAhorrosLeonardo.getCuentaId()).isEmpty());
    }

    @Test
    public void obtenerTodasTransacciones() {
        //Transacciones de Leonardo
        List<Transaccion> transacciones = cuentaBancariaRepository
            .findAllTransaccionesCuenta(cuentaAhorrosLeonardo.getCuentaId());

        //Debe retornar dos transacciones: una transacción en efectivo y un
        //movimiento bancario
        assertEquals(2, transacciones.size());
        assertTrue(transacciones.contains(consignacion));
        assertTrue(transacciones.contains(movimientoBancario));

        //Transacciones de Florinda
        transacciones = cuentaBancariaRepository
            .findAllTransaccionesCuenta(cuentaCorrienteFlorinda.getCuentaId());

        assertEquals(2, transacciones.size());
        assertTrue(transacciones.contains(retiro));
        assertTrue(transacciones.contains(movimientoBancario));

    }

    private void validacionesCuenta(CuentaBancaria cuentaReferencia, CuentaBancaria cuentaPrueba) {
        assertNotNull(cuentaPrueba);
        assertEquals(cuentaReferencia.getCuentaId(), cuentaPrueba.getCuentaId());
        assertEquals(cuentaReferencia.getCliente(), cuentaPrueba.getCliente()); 
        assertEquals(cuentaReferencia.getSaldo(), cuentaPrueba.getSaldo());  
    }
}
