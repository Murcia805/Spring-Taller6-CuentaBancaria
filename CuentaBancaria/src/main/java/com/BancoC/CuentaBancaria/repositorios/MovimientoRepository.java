package com.BancoC.CuentaBancaria.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.BancoC.CuentaBancaria.modelos.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long>{
    
}
