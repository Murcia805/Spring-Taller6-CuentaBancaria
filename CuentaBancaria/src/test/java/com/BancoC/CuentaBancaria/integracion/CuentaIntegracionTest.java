package com.BancoC.CuentaBancaria.integracion;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Import(IntegracionTest.ConfiguracionTest.class)
public class CuentaIntegracionTest extends IntegracionTest {

    @Test
    void obtenerCuentaConCuentaId200() throws Exception {
        MvcResult response = mockMvc.perform(
            get("/api/cuenta/{cuentaId}", cuentaAhorrosLeonardo.getCuentaId()))
        .andExpect(status().isOk())
        .andReturn();

        CuentaBancaria cuentaObtenida = objectMapper.readValue(
            response.getResponse().getContentAsString(),
            CuentaBancaria.class
        );

        this.validarCuenta(cuentaAhorrosLeonardo, cuentaObtenida);
    }

    @Test
    void obtenerCuentaConNumeroCuenta200() throws Exception {
        MvcResult response = mockMvc.perform(
            get("/api/cuenta")
            .param("numeroCuenta", cuentaCorrienteFlorinda.getNumeroCuenta()))
        .andExpect(status().isOk())
        .andReturn();
        
        CuentaBancaria cuentaObtenida = objectMapper.readValue(
            response.getResponse().getContentAsString(),
            CuentaBancaria.class
        );

        this.validarCuenta(cuentaCorrienteFlorinda, cuentaObtenida);
    }

    @Test
    void obtenerCuentaConCuentaId404() throws Exception {
        mockMvc.perform(
            get("/api/cuenta/{cuentaId}", 5234L))
        .andExpect(status().isNotFound());
    }

    @Test
    void obtenerCuentaConNumeroCuenta404() throws Exception {
        mockMvc.perform(
            get("/api/cuenta")
            .param("numeroCuenta", "9999999999999"))
        .andExpect(status().isNotFound());
    }

    @Test
    void obtenerCuentasCliente200() throws Exception {
        MvcResult response = mockMvc.perform(
            get("/api/cuenta/todas")
            .param("clienteId", "" + cuentaCorrienteFlorinda.getClienteId()))
        .andExpect(status().isOk())
        .andReturn();
        
        List<CuentaBancaria> cuentasObtenidas = objectMapper.readValue(
            response.getResponse().getContentAsString(),
            new TypeReference<List<CuentaBancaria>> () {}
        );

        assertNotNull(cuentasObtenidas);
        assertEquals(1, cuentasObtenidas.size());
        this.validarCuenta(cuentaCorrienteFlorinda, cuentasObtenidas.get(0));
    }

    @Test
    void obtenerCuentasCliente404() throws Exception {
        mockMvc.perform(
            get("/api/cuenta/todas")
            .param("clienteId", "" + 554))
        .andExpect(status().isNotFound());
    }

    @Test
    void nuevaCuenta200() throws Exception {
        //Nueva cuenta de Florinda
        String requestBody = objectMapper.writeValueAsString(
            CuentaBancaria.builder()
                .cliente(Florinda)
                .clienteId(Florinda.getClienteId())
                .numeroCuenta("538238321")
                .fechaCreacion(LocalDate.now())
                .build()
        );

        //Petición al microservicio
        MvcResult response = mockMvc.perform(
            post("/api/cuenta")
            .param("clienteId", "" + Florinda.getClienteId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andReturn();

        //Deserialización de la respuesta
        CuentaBancaria cuentaGuardada = objectMapper.readValue(
            response.getResponse().getContentAsString(),
            CuentaBancaria.class
        );

        //Validaciones
        assertNotNull(cuentaGuardada);
        assertTrue(cuentaGuardada.getCuentaId() > 0);
        assertEquals(Florinda.getClienteId(), cuentaGuardada.getClienteId());
        assertEquals("538238321", cuentaGuardada.getNumeroCuenta());
        assertEquals(0.0, cuentaGuardada.getSaldo());
    }

    @Test
    void nuevaTransaccion201() throws Exception {
        CuentaBancaria cuentaActualizada = ejecutarTransaccion(consignacion);
        assertEquals(cuentaAhorrosLeonardo.getNumeroCuenta(), cuentaActualizada.getNumeroCuenta());
        assertEquals(
            cuentaAhorrosLeonardo.getSaldo() + consignacion.getMonto(),
            cuentaActualizada.getSaldo()
        );

        cuentaActualizada = ejecutarTransaccion(movimientoBancario);
        assertEquals(cuentaAhorrosLeonardo.getNumeroCuenta(), cuentaActualizada.getNumeroCuenta());
        assertEquals(
            cuentaAhorrosLeonardo.getSaldo() + consignacion.getMonto()
                - movimientoBancario.getMonto(),
            cuentaActualizada.getSaldo()
        );

    }

    private CuentaBancaria ejecutarTransaccion (Transaccion transaccion) throws Exception {
        String requestBody = objectMapper.writeValueAsString(transaccion);

        mockMvc.perform(
            post("/api/cuenta/transaccion")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated());

        Optional<CuentaBancaria> cuentaActualizada = 
            cuentaBancariaRepository.findByNumeroCuenta(
                cuentaAhorrosLeonardo.getNumeroCuenta()
            );
        
        assertFalse(cuentaActualizada.isEmpty());
        
        return cuentaActualizada.get();
    }

    private void validarCuenta (CuentaBancaria cuentaReferencia, 
                                CuentaBancaria cuentaAValidar) {
        assertNotNull(cuentaReferencia);
        assertTrue(cuentaAValidar.getCuentaId() > 0);
        assertEquals(cuentaReferencia.getNumeroCuenta(), cuentaAValidar.getNumeroCuenta());
        assertEquals(cuentaReferencia.getSaldo(), cuentaAValidar.getSaldo());
    }

}
