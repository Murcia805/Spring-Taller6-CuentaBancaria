package com.BancoC.CuentaBancaria.unitarios.controladores;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.BancoC.CuentaBancaria.modelos.CuentaBancaria;
import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@Import(ControladoresTest.TestConfig.class)
public class CuentaControladorTest extends ControladoresTest {
    
    @Test
    void obtenerCuenta200() throws Exception {
        MvcResult response = mockMvc.perform(
            get("/api/cuenta/{cuentaId}", cuentaAhorrosLeonardo.getCuentaId()))
        .andExpect(status().isOk())
        .andReturn();
        
        CuentaBancaria cuentaRespuesta = mapper.readValue(
            response.getResponse().getContentAsString(), 
            CuentaBancaria.class
        );

        assertNotNull(cuentaRespuesta);
        assertEquals(cuentaAhorrosLeonardo, cuentaRespuesta);
    }

    @Test
    void obtenerCuenta404() throws Exception {
        mockMvc.perform(
            get("/api/cuenta/{cuentaId}", 502L))
        .andExpect(status().isNotFound())
        .andReturn();

        verify(cuentaBancariaOperaciones, times(1))
            .obtenerCuenta(502L);
    }

    @Test
    void obtenerCuentaNumeroCuenta404() throws Exception {
        mockMvc.perform(
            get("/api/cuenta")
            .param("numeroCuenta", "111111111"))
            .andExpect(status().isNotFound());
        
        verify(cuentaBancariaOperaciones, times(1))
            .obtenerCuenta("111111111");
    }

    @Test
    void obtenerCuentaNumeroCuenta200() throws Exception {
        MvcResult response = mockMvc.perform(
            get("/api/cuenta")
            .param("numeroCuenta", cuentaAhorrosLeonardo.getNumeroCuenta()))
        .andExpect(status().isOk())
        .andReturn();

        CuentaBancaria cuentaRespuesta = mapper.readValue(
            response.getResponse().getContentAsString(),
            CuentaBancaria.class
        );

        assertNotNull(cuentaRespuesta);
        assertEquals(cuentaRespuesta, cuentaAhorrosLeonardo);
    }

    @Test
    void obtenerCuentasCliente200() throws Exception {
        MvcResult response = mockMvc.perform(
            get("/api/cuenta/todas").param("clienteId", "" + Leonardo.getClienteId()))
        .andExpect(status().isOk())
        .andReturn();

        List<CuentaBancaria> cuentasLeonardo = mapper.readValue(
            response.getResponse().getContentAsString(),
            new TypeReference<List<CuentaBancaria>>() {}
        );

        assertNotNull(cuentasLeonardo);
        assertEquals(1, cuentasLeonardo.size());
        assertEquals(cuentaAhorrosLeonardo, cuentasLeonardo.get(0));
    }

    @Test
    void nuevaCuentaValida200() throws Exception {

        String requestBody = mapper.writeValueAsString(cuentaAhorrosLeonardo);

        MvcResult repsonse = mockMvc.perform(
            post("/api/cuenta")
            .param("clienteId", "" + Leonardo.getClienteId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andReturn();

        CuentaBancaria cuentaObtenida = mapper.readValue(
            repsonse.getResponse().getContentAsString(),
            CuentaBancaria.class
        );

        assertNotNull(cuentaObtenida);
        assertEquals(cuentaObtenida, cuentaAhorrosLeonardo);
    }

    @Test
    void nuevaCuentaFraudeSaldo() throws Exception {
        String requestBody = mapper.writeValueAsString(cuentaFraudulenta);

        when(cuentaBancariaOperaciones.nuevaCuenta(
            cuentaFraudulenta,
            cuentaFraudulenta.getClienteId()
        )).thenThrow(new RuntimeException("Alerta de fraude: una cuenta nueva no puede tener un saldo diferente a 0.0"));

        mockMvc.perform(
            post("/api/cuenta")
            .param("clienteId", "" + cuentaFraudulenta.getClienteId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());
        
        verify(cuentaBancariaOperaciones, times(1))
            .nuevaCuenta(cuentaFraudulenta, cuentaFraudulenta.getClienteId());
    }

    @Test
    void nuevaTransaccion200() throws Exception {   
        String requestBody = mapper.writeValueAsString(consignacion);

        MvcResult response = mockMvc.perform(
            post("/api/cuenta/transaccion")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andReturn();

        Transaccion transaccionRealizada = mapper.readValue(
            response.getResponse().getContentAsString(),
            Transaccion.class 
        );

        verify(cuentaBancariaOperaciones, times(1)).transaccion(consignacion);
        assertNotNull(transaccionRealizada);
        assertEquals(consignacion.getMonto(), transaccionRealizada.getMonto());
    }

    @Test
    void nuevaTransaccion400() throws Exception {
        String requestBody = mapper.writeValueAsString(movimientoBancario);

        when(cuentaBancariaOperaciones.transaccion(movimientoBancario))
            .thenThrow(new RuntimeException("Falla general"));

        mockMvc.perform(
            post("/api/cuenta/transaccion")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());

        verify(cuentaBancariaOperaciones, times(1))
            .transaccion(movimientoBancario);
    }

}
