package com.BancoC.CuentaBancaria.integracion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.BancoC.CuentaBancaria.GeneralTest;
import com.BancoC.CuentaBancaria.repositorios.CuentaBancariaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegracionTest extends GeneralTest {
    
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CuentaBancariaRepository cuentaBancariaRepository;

    protected ObjectMapper objectMapper;

    @TestConfiguration(proxyBeanMethods = false)
    public static class ConfiguracionTest {

        @Bean
        @ServiceConnection
        public PostgreSQLContainer<?> baseDatosPruebas () {
            return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
        }

    }

    @Override
    @BeforeEach
    public void setUp () throws Exception {
        super.setUp();  //=> trae los objetos de pruebas
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        //Almacenamiento de objetos de pruebas en BD
        this.almacenarCuentas();

    }

    private void almacenarCuentas() {
        cuentaAhorrosLeonardo = cuentaBancariaRepository.save(cuentaAhorrosLeonardo);
        cuentaCorrienteFlorinda = cuentaBancariaRepository.save(cuentaCorrienteFlorinda);
    }

    @AfterEach
    public void tearDown() {
        cuentaBancariaRepository.deleteAll();
    }

}
