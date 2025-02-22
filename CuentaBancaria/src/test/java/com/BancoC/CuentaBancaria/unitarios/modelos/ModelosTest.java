package com.BancoC.CuentaBancaria.unitarios.modelos;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.BancoC.CuentaBancaria.GeneralTest;
import com.BancoC.CuentaBancaria.modelos.Movimiento;
import com.BancoC.CuentaBancaria.modelos.TransaccionEfectivo;
import com.BancoC.CuentaBancaria.repositorios.CuentaBancariaRepository;
import com.BancoC.CuentaBancaria.repositorios.MovimientoRepository;
import com.BancoC.CuentaBancaria.repositorios.TransaccionEfectivoRepository;

@DataJpaTest
@ActiveProfiles("test_unitarios")
public abstract class ModelosTest extends GeneralTest {
    @Autowired
    protected TransaccionEfectivoRepository transaccionEfectivoRepository;

    @Autowired
    protected MovimientoRepository movimientoRepository;

    @Autowired
    protected CuentaBancariaRepository cuentaBancariaRepository;

    @Override
    @BeforeEach
    public void setUp() throws Exception { 
        super.setUp();  //Definición de los objetos de pruebas

        //Almacenamiento de los objetos de pruebas en la base de datos
        consignacion = transaccionEfectivoRepository.save((TransaccionEfectivo) consignacion);
        retiro = transaccionEfectivoRepository.save((TransaccionEfectivo) retiro);

        movimientoBancario = movimientoRepository.save((Movimiento) movimientoBancario);

        cuentaAhorrosLeonardo = cuentaBancariaRepository.save(cuentaAhorrosLeonardo);
        cuentaCorrienteFlorinda = cuentaBancariaRepository.save(cuentaCorrienteFlorinda);
    }

}
