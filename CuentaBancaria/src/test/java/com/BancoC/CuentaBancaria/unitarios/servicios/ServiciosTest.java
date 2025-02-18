package com.BancoC.CuentaBancaria.unitarios.servicios;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.BeanUtils;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.repositorios.CuentaBancariaRepository;
import com.BancoC.CuentaBancaria.repositorios.MovimientoRepository;
import com.BancoC.CuentaBancaria.repositorios.TransaccionEfectivoRepository;
import com.BancoC.CuentaBancaria.servicios.contratos.TransaccionOperaciones;
import com.BancoC.CuentaBancaria.unitarios.GeneralTest;

public class ServiciosTest extends GeneralTest {

    protected TransaccionOperaciones transaccionOperaciones;

    protected MovimientoRepository movimientoRepository;
    protected TransaccionEfectivoRepository transaccionEfectivoRepository;
    protected CuentaBancariaRepository cuentaBancariaRepository;

    protected TransaccionEfectivo consignacionBD;
    protected TransaccionEfectivo retiroBD;
    protected Movimiento movimientoBD;

    protected CuentaBancaria cuentaLeonardoBD;
    protected CuentaBancaria cuentaFlorindaBD;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();  //Trae la definición de los objetos de pruebas

        //Objetos obtenidos de bases de datos
        consignacionBD = (TransaccionEfectivo) this.transaccionBD(consignacion, new TransaccionEfectivo(), 1L);
        retiroBD = (TransaccionEfectivo) this.transaccionBD(retiro, new TransaccionEfectivo(), 2L);
        movimientoBD = (Movimiento) this.transaccionBD(movimientoBancario, new Movimiento(), 3L);

        cuentaLeonardoBD = this.cuentaBD(cuentaAhorrosLeonardo, 13L);
        cuentaFlorindaBD = this.cuentaBD(cuentaCorrienteFlorinda, 129L);

        //Definición de mocks
        movimientoRepository = mock(MovimientoRepository.class);
        transaccionEfectivoRepository = mock(TransaccionEfectivoRepository.class);
        cuentaBancariaRepository = mock(CuentaBancariaRepository.class);
        transaccionOperaciones = mock(TransaccionOperaciones.class);

        //Comportamiento de mocks
        this.comportamientosTransaccionesService();
        this.comportamientosCuentaService();
    }

    private void comportamientosCuentaService() throws Exception {
        this.operacionesSaveCuenta();
        this.operacionesObtenerCuenta();
        this.operacionesTransaccionesCuenta();
    }

    private void operacionesTransaccionesCuenta() throws Exception {
        when(this.transaccionOperaciones.nuevaTransaccion(consignacion))
            .thenReturn(consignacionBD);
        
        when(this.transaccionOperaciones.nuevaTransaccion(retiro))
            .thenReturn(retiroBD);

        when(this.transaccionOperaciones.nuevaTransaccion(movimientoBancario))
            .thenReturn(movimientoBD);
    }

    private void operacionesSaveCuenta() {
        when(cuentaBancariaRepository.save(cuentaAhorrosLeonardo))
            .thenReturn(cuentaLeonardoBD);

        when(cuentaBancariaRepository.save(cuentaCorrienteFlorinda))
            .thenReturn(cuentaFlorindaBD);
    }

    protected void actualizarCuenta(CuentaBancaria cuenta, Double nuevoSaldo) {
        CuentaBancaria cuentaActualizada = this.cuentaBD(cuenta, null);
        cuentaActualizada.setSaldo(nuevoSaldo);

        when(cuentaBancariaRepository.save(cuentaActualizada))
            .thenReturn(
                this.cuentaBD(cuentaActualizada, cuenta.getCuentaId())
            );
    }

    private void operacionesObtenerCuenta() {
        when(this.cuentaBancariaRepository.findById(cuentaLeonardoBD.getCuentaId()))
            .thenReturn(Optional.of(cuentaLeonardoBD));
        
        when(this.cuentaBancariaRepository.findById(cuentaFlorindaBD.getCuentaId()))
            .thenReturn(Optional.of(cuentaFlorindaBD));

        when(this.cuentaBancariaRepository.findByNumeroCuenta(cuentaLeonardoBD.getNumeroCuenta()))
            .thenReturn(Optional.of(cuentaLeonardoBD));
        
        when(this.cuentaBancariaRepository.findByNumeroCuenta(cuentaFlorindaBD.getNumeroCuenta()))
            .thenReturn(Optional.of(cuentaFlorindaBD));

        when(this.cuentaBancariaRepository.findByClienteId(Leonardo.getClienteId()))
            .thenReturn(List.of(cuentaLeonardoBD));
    }

    private void comportamientosTransaccionesService() {
        this.operacionesSaveTransacciones();
        this.operacionesFindTransacciones();
    }

    private void operacionesFindTransacciones() {
        when(transaccionEfectivoRepository.findById(1L))
            .thenReturn(Optional.of(consignacionBD));

        when(transaccionEfectivoRepository.findById(2L))
            .thenReturn(Optional.of(retiroBD));

        when(movimientoRepository.findById(3L))
            .thenReturn(Optional.of(movimientoBD));
    }

    private void operacionesSaveTransacciones() {
        when(transaccionEfectivoRepository.save((TransaccionEfectivo) consignacion))
            .thenReturn(consignacionBD);
        

        when(transaccionEfectivoRepository.save((TransaccionEfectivo) retiro))
            .thenReturn(retiroBD);
        
        when(movimientoRepository.save((Movimiento) movimientoBancario))
            .thenReturn(movimientoBD);
    }

    private <T> Transaccion transaccionBD (T objetoPrueba, Transaccion respuestaVacia, Long id) {
        BeanUtils.copyProperties(objetoPrueba, respuestaVacia);
        respuestaVacia.setTransaccionId(id);
        return respuestaVacia;
    }

    private CuentaBancaria cuentaBD(CuentaBancaria cuenta, Long cuentaId) {
        CuentaBancaria cuentaCopia = new CuentaBancaria();
        BeanUtils.copyProperties(cuenta, cuentaCopia);
        cuentaCopia.setCuentaId(cuentaId);
        return cuentaCopia;
    }
}
