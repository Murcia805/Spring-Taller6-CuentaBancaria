package com.BancoC.CuentaBancaria.unitarios.controladores;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.BancoC.CuentaBancaria.servicios.contratos.CuentaBancariaOperaciones;
import com.BancoC.CuentaBancaria.unitarios.GeneralTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
@ActiveProfiles("test_unitarios")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ControladoresTest extends GeneralTest {
    
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CuentaBancariaOperaciones cuentaBancariaOperaciones;

    @Autowired
    protected ObjectMapper mapper;

    //Generación de beans de testing para mock de servicios
    @TestConfiguration
    public static class TestConfig {

        @Bean
        public CuentaBancariaOperaciones cuentaTest() {
            return mock(CuentaBancariaOperaciones.class);
        }

    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();  

        //Ajuste de objetos de pruebas
        cuentaAhorrosLeonardo.setCuentaId(1L);
        cuentaCorrienteFlorinda.setCuentaId(2L);

        //Comportamiento de los mocks
        this.obtenerCuenta();
        this.obtenerCuentasCliente();
        this.guardarCuentas();
        this.transacciones();
    }

    private void transacciones() throws Exception {
        when(cuentaBancariaOperaciones.transaccion(consignacion))
            .thenReturn(
                cuentaAhorrosLeonardo.builder()
                .saldo(150_000.0)
                .build()
            );
        
        when(cuentaBancariaOperaciones.transaccion(movimientoBancario))
            .thenThrow(new RuntimeException("Falla general"));
    }

    private void guardarCuentas() throws Exception {
        when(cuentaBancariaOperaciones.nuevaCuenta(
            cuentaAhorrosLeonardo,
            Leonardo.getClienteId()
        )).thenReturn(cuentaAhorrosLeonardo);

        when(cuentaBancariaOperaciones.nuevaCuenta(
            cuentaCorrienteFlorinda,
            Florinda.getClienteId()
        )).thenReturn(cuentaCorrienteFlorinda);

        when(cuentaBancariaOperaciones.nuevaCuenta(
            cuentaFraudulenta,
            cuentaFraudulenta.getClienteId()
        )).thenThrow(new RuntimeException("Alerta de fraude: una cuenta nueva no puede tener un saldo diferente a 0.0"));

    }

    private void obtenerCuenta() {
        when(cuentaBancariaOperaciones.obtenerCuenta(
            cuentaAhorrosLeonardo.getCuentaId()
        )).thenReturn(cuentaAhorrosLeonardo);

        when(cuentaBancariaOperaciones.obtenerCuenta(
            cuentaAhorrosLeonardo.getNumeroCuenta()
        )).thenReturn(cuentaAhorrosLeonardo);

        when(cuentaBancariaOperaciones.obtenerCuenta(
            cuentaCorrienteFlorinda.getCuentaId()
        )).thenReturn(cuentaCorrienteFlorinda);

        when(cuentaBancariaOperaciones.obtenerCuenta(
            cuentaCorrienteFlorinda.getNumeroCuenta()
        )).thenReturn(cuentaCorrienteFlorinda);
    }

    private void obtenerCuentasCliente() {
        when(cuentaBancariaOperaciones.obtenerCuentas(
            Leonardo.getClienteId()
        )).thenReturn(List.of(cuentaAhorrosLeonardo));

        when(cuentaBancariaOperaciones.obtenerCuentas(
            Florinda.getClienteId()
        )).thenReturn(List.of(cuentaCorrienteFlorinda));
    }

}
