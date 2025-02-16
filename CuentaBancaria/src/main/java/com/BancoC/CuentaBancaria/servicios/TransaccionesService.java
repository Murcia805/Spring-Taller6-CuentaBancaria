package com.BancoC.CuentaBancaria.servicios;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.repositorios.MovimientoRepository;
import com.BancoC.CuentaBancaria.repositorios.TransaccionEfectivoRepository;
import com.BancoC.CuentaBancaria.servicios.contratos.TransaccionOperaciones;

@Service
public class TransaccionesService implements TransaccionOperaciones{

    private MovimientoRepository movimientoRepository;
    private TransaccionEfectivoRepository transaccionEfectivoRepository;

    public TransaccionesService(TransaccionEfectivoRepository repository,
                                MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.transaccionEfectivoRepository = repository;
    }

    @Override
    public Transaccion nuevaTransaccion(Transaccion transaccion) throws Exception {
        this.validacionesTransaccion(transaccion);
        return this.guardarTransaccion(transaccion);
    }

    @Override
    public Transaccion obtenerTransaccion(Long transaccionId) {
        Optional<Movimiento> movimiento = this.movimientoRepository.findById(transaccionId);
        if (!movimiento.isEmpty()) { //¿Es un movimiento?
            return movimiento.get();
        }
        //No es un movimiento. ¿Es una transacción en efectivo?
        Optional<TransaccionEfectivo> transaccionEf = this.transaccionEfectivoRepository.findById(transaccionId);
        if (!transaccionEf.isEmpty()) {
            return transaccionEf.get();
        }
        return null;    //No se encontró la transacción
    }

    private void validacionesTransaccion(Transaccion transaccion) throws Exception {
        //Validación de saldo
        if (transaccion.getMonto() == null) {
            throw new Exception("El monto debe ser un número");
        } else if (transaccion.getMonto() <= 0.0) {
            throw new Exception("El monto debe ser mayor que 0.0");
        }

        //Validación de fechas
        LocalDateTime fecha = transaccion.getFechaCreacion();
        if (fecha == null) {
            transaccion.setFechaCreacion(LocalDateTime.now());
        } else if (Duration.between(fecha, LocalDateTime.now()).toMinutes() > 2) {
            throw new TimeoutException("Timeout: se pasó del límite de tiempo");
        } else if(Duration.between(fecha, LocalDateTime.now()).toMinutes() < 0) {
            throw new TimeoutException("Alerta de fraude: la fecha de la transacción fue alterada");
        }
    }

    private Transaccion guardarTransaccion(Transaccion transaccion) {
        if(transaccion.getClass() == TransaccionEfectivo.class) {
            return this.transaccionEfectivoRepository.save((TransaccionEfectivo) transaccion);
        } else if (transaccion.getClass() == Movimiento.class) {
            return this.movimientoRepository.save((Movimiento) transaccion);
        }
        return null;
    }
    
}
