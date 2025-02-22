package com.BancoC.CuentaBancaria.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.servicios.contratos.CuentaBancariaOperaciones;

@RestController
@RequestMapping("/api/cuenta")
public class CuentaControlador {
    
    private CuentaBancariaOperaciones operaciones;

    public CuentaControlador(CuentaBancariaOperaciones operaciones) {
        this.operaciones = operaciones;
    }

    @GetMapping("{cuentaId}")
    public ResponseEntity<CuentaBancaria> obtenerCuenta(@PathVariable("cuentaId") Long cuentaId) {
        CuentaBancaria cuentaObtenida = operaciones.obtenerCuenta(cuentaId);
        if (cuentaObtenida == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(cuentaObtenida);
    }

    @GetMapping
    public ResponseEntity<CuentaBancaria> obtenerCuenta(@RequestParam("numeroCuenta") String numeroCuenta) {
        CuentaBancaria cuentaObtenida = operaciones.obtenerCuenta(numeroCuenta);
        if (cuentaObtenida == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(cuentaObtenida);
    }

    @GetMapping("todas")
    public ResponseEntity<List<CuentaBancaria>> obtenerCuentas(@RequestParam("clienteId") Long clienteId) {
        List<CuentaBancaria> cuentasCliente = operaciones.obtenerCuentas(clienteId);
        if (cuentasCliente == null || cuentasCliente.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(cuentasCliente);
    }

    @PostMapping
    public ResponseEntity<CuentaBancaria> nuevaCuenta(
            @RequestParam("clienteId") Long clienteId, 
            @RequestBody CuentaBancaria cuentaBancaria) {
        CuentaBancaria nuevaCuenta = null;
        try {
            nuevaCuenta = operaciones.nuevaCuenta(cuentaBancaria, clienteId);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
    }

    @PostMapping("transaccion")
    public ResponseEntity<CuentaBancaria> nuevaTransaccion(@RequestBody Transaccion transaccion) throws Exception {
        CuentaBancaria cuentaObtenida = null;
        try {
            cuentaObtenida = operaciones.transaccion(transaccion);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaObtenida);
    }
}
