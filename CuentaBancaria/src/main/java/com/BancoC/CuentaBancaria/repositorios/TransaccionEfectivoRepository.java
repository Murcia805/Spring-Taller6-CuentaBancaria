package com.BancoC.CuentaBancaria.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;

public interface TransaccionEfectivoRepository extends JpaRepository<TransaccionEfectivo, Long> {
    
}
