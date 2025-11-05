package com.multipedidos.proveedores_facturas_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "facturas")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proveedorId;

    private double totalFactura;

    // No se persiste: viene del microservicio de pedidos
    @Transient
    private List<PedidoReferencia> pedidos;
}
