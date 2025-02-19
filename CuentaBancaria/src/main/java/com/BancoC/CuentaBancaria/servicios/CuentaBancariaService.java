package com.BancoC.CuentaBancaria.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.repositorios.CuentaBancariaRepository;
import com.BancoC.CuentaBancaria.servicios.contratos.CuentaBancariaOperaciones;
import com.BancoC.CuentaBancaria.servicios.contratos.TransaccionOperaciones;

@Service
public class CuentaBancariaService implements CuentaBancariaOperaciones {

    private CuentaBancariaRepository repository;
    private TransaccionOperaciones transaccionesService;

    public CuentaBancariaService(CuentaBancariaRepository repository,
                                 TransaccionOperaciones service) {
        this.repository = repository;
        this.transaccionesService = service;
    }

    @Override
    public CuentaBancaria nuevaCuenta(CuentaBancaria cuentaBancaria, Long clienteId) throws Exception {
        this.validacionesCuenta(cuentaBancaria, clienteId);
        return repository.save(cuentaBancaria);
    }

    @Override
    public CuentaBancaria transaccion(Transaccion transaccion) throws Exception {
        CuentaBancaria cuentaCliente = null;
        //Operaciones transaccionales
        if (transaccion.getClass() == TransaccionEfectivo.class) {
            cuentaCliente = this.transaccionEfectivo((TransaccionEfectivo) transaccion);
        } else if (transaccion.getClass() == Movimiento.class) {
            cuentaCliente = this.movimientoBancario((Movimiento) transaccion);
        }

        //Validación y guardado de la transacción
        transaccionesService.nuevaTransaccion(transaccion);
        return cuentaCliente;
    }

    private void validacionSaldoInsuficiente(Double nuevoSaldo) throws Exception {
        if (nuevoSaldo < 0.0) {
            throw new Exception("No se tiene el saldo suficiente para aprobar ese retiro");
        }
    }

    private CuentaBancaria transaccionEfectivo(TransaccionEfectivo transaccion) throws Exception {
        //Obtención de la cuenta
        CuentaBancaria cuentaCliente = this.obtenerCuenta(
            transaccion.getCuentaDestino().getNumeroCuenta()
        );

        //Actualizar saldo
        Double saldo = cuentaCliente.getSaldo();
        if (transaccion.getTipoTransaccion() == "C") {
            saldo += transaccion.getMonto();
        } else if (transaccion.getTipoTransaccion() == "R") {
            saldo -= transaccion.getMonto();
            this.validacionSaldoInsuficiente(saldo);
        }
        cuentaCliente.setSaldo(saldo);
        repository.save(cuentaCliente); //Actualizar la cuenta con el nuevo saldo
        return cuentaCliente;
        
    }

    private CuentaBancaria movimientoBancario(Movimiento transaccion) throws Exception {
        //Obtención de las cuentas
        CuentaBancaria cuentaOrigen = this.obtenerCuenta(
            transaccion.getCuentaOrigen().getNumeroCuenta()
        );
        CuentaBancaria cuentaDestino = this.obtenerCuenta(
            transaccion.getCuentaDestino().getNumeroCuenta()
        );

        //Actualizar saldos
        cuentaOrigen.setSaldo(
            cuentaOrigen.getSaldo() - transaccion.getMonto()
        );
        cuentaDestino.setSaldo(
            cuentaDestino.getSaldo() + transaccion.getMonto()
        );
        this.validacionSaldoInsuficiente(cuentaOrigen.getSaldo());
        repository.save(cuentaOrigen);
        repository.save(cuentaDestino);
        return cuentaOrigen;
    }

    @Override
    public CuentaBancaria obtenerCuenta(Long cuentaId) {
        Optional<CuentaBancaria> cuentaObtenida = this.repository.findById(cuentaId);
        if (cuentaObtenida.isEmpty()) {
            return null;
        }
        return cuentaObtenida.get();
    }

    @Override
    public CuentaBancaria obtenerCuenta(String numeroCuenta) {
        Optional<CuentaBancaria> cuentaObtenida = this.repository.findByNumeroCuenta(numeroCuenta);
        if (cuentaObtenida.isEmpty()) {
            return null;
        }
        return cuentaObtenida.get();
    }

    @Override
    public List<CuentaBancaria> obtenerCuentas(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    @Override
    public Boolean eliminarCuenta(Long clienteId, String numeroCuenta) throws Exception {
        CuentaBancaria cuenta = this.obtenerCuenta(numeroCuenta);
        if (cuenta.getClienteId() != clienteId) {
            throw new Exception("Alerta de fraude: el cliente con id=" + clienteId +
                " intenta eliminar una cuenta que no es de su propiedad (cuentaId=" + 
                cuenta.getClienteId() + ")");
        }
        if (cuenta.getSaldo() > 0.0) {
            return false;
        }
        repository.delete(cuenta);
        return true;
        
    }
    
    private void validacionesCuenta(CuentaBancaria cuenta, Long clienteId) throws Exception {
        if (cuenta.getClienteId() != clienteId) {
            throw new Exception("Alerta de fraude: se intenta vincular una cuenta a un cliente diferente");
        }
        if (cuenta.getSaldo() != null && cuenta.getSaldo() != 0.0) {
            throw new Exception("Alerta de fraude: una cuenta nueva no puede tener un saldo diferente a 0.0");
        }
    }
}
