package com.multipedidos.proveedores_facturas_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "facturas")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proveedorId;

    private double monto;

    private LocalDate fecha = LocalDate.now();

    // No persistimos los pedidos, vienen del microservicio A
    @Transient
    private List<com.multipedidos.proveedores_facturas_service.dto.PedidoReferencia> pedidos;
}
