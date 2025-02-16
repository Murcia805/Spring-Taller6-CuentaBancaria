package com.BancoC.CuentaBancaria.modelos;

import java.time.LocalDate;
import java.util.List;

import com.BancoC.CuentaBancaria.modelos.contratos.Transaccion;
import com.BancoC.CuentaBancaria.modelos.ext.Cliente;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cuentaId;

    private String numeroCuenta;
    private Double saldo;
    private LocalDate fechaCreacion;

    //El cliente se recibe desde el microservicio de autenticación, no desde la base de datos de la cuenta bancaria. 
    //Se debe validar el id del cliente con el recibido.
    @Transient
    private Cliente cliente;
    @Column(nullable = false)
    private Long clienteId;

    @OneToMany(mappedBy = "cuentaDestino", cascade = CascadeType.ALL)
    private List<Transaccion> transacciones;
}
