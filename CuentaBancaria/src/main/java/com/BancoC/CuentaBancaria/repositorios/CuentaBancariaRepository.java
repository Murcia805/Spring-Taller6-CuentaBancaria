package com.BancoC.CuentaBancaria.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;

public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {
    List<CuentaBancaria> findByClienteId(Long clienteId);


    @Query(
        value = 
            """
            SELECT 
                t.*,
                m.cuenta_origen_id,
                te.tipo_transaccion
            FROM transaccion t
            LEFT JOIN transaccion_efectivo te 
                ON t.transaccion_id = te.transaccion_id
            LEFT JOIN movimiento m
                ON t.transaccion_id = m.transaccion_id
            WHERE t.cuenta_destino_id = :cuentaId
                OR m.cuenta_origen_id = :cuentaId
            """,
        nativeQuery = true
    )
    List<Transaccion> findAllTransaccionesCuenta(@Param("cuentaId") Long cuentaId);
}
