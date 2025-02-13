package com.BancoC.CuentaBancaria;

import org.springframework.boot.SpringApplication;

public class TestCuentaBancariaApplication {

	public static void main(String[] args) {
		SpringApplication.from(CuentaBancariaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
