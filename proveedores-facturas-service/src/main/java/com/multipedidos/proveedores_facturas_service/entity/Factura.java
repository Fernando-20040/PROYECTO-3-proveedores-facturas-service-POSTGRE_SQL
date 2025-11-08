package com.multipedidos.proveedores_facturas_service.entity;

import com.multipedidos.proveedores_facturas_service.dto.PedidoReferencia;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "facturas")
@Data
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proveedorId;
    private double monto;
    private LocalDate fecha;

    // 🔹 Estado de la factura: ACTIVA o ANULADA
    @Column(nullable = false)
    private String estado = "ACTIVA";

    // 🔹 Motivo de anulación (solo si se anula)
    private String motivoAnulacion;

    // 🔹 Lista de pedidos asociados a la factura (no se persiste)
    @Transient
    private List<PedidoReferencia> pedidos;
}
