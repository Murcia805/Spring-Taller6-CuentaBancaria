package com.BancoC.CuentaBancaria.unitarios.servicios;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.BeanUtils;

import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.repositorios.MovimientoRepository;
import com.BancoC.CuentaBancaria.repositorios.TransaccionEfectivoRepository;
import com.BancoC.CuentaBancaria.unitarios.GeneralTest;

public class ServiciosTest extends GeneralTest {

    protected MovimientoRepository movimientoRepository;
    protected TransaccionEfectivoRepository transaccionEfectivoRepository;

    protected TransaccionEfectivo consignacionBD;
    protected TransaccionEfectivo retiroBD;
    protected Movimiento movimientoBD;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();  //Trae la definición de los objetos de pruebas

        //Objetos obtenidos de bases de datos
        consignacionBD = (TransaccionEfectivo) this.respuestaBD(consignacion, new TransaccionEfectivo(), 1L);
        retiroBD = (TransaccionEfectivo) this.respuestaBD(retiro, new TransaccionEfectivo(), 2L);
        movimientoBD = (Movimiento) this.respuestaBD(movimientoBancario, new Movimiento(), 3L);

        //Definición de mocks
        movimientoRepository = mock(MovimientoRepository.class);
        transaccionEfectivoRepository = mock(TransaccionEfectivoRepository.class);

        //Comportamiento de mocks
        this.operacionesSave();
        this.operacionesFind();
    }

    private void operacionesFind() {
        when(transaccionEfectivoRepository.findById(1L))
            .thenReturn(Optional.of(consignacionBD));

        when(transaccionEfectivoRepository.findById(2L))
            .thenReturn(Optional.of(retiroBD));

        when(movimientoRepository.findById(3L))
            .thenReturn(Optional.of(movimientoBD));
    }

    private void operacionesSave() {
        when(transaccionEfectivoRepository.save((TransaccionEfectivo) consignacion))
            .thenReturn(consignacionBD);
        

        when(transaccionEfectivoRepository.save((TransaccionEfectivo) retiro))
            .thenReturn(retiroBD);
        
        when(movimientoRepository.save((Movimiento) movimientoBancario))
            .thenReturn(movimientoBD);
    }

    private <T> Transaccion respuestaBD (T objetoPrueba, Transaccion respuestaVacia, Long id) {
        BeanUtils.copyProperties(objetoPrueba, respuestaVacia);
        respuestaVacia.setTransaccionId(id);
        return respuestaVacia;
    }


}
