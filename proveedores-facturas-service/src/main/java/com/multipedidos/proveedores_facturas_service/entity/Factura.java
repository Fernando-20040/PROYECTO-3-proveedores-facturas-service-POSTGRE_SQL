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

    // 🔹 Nuevos campos de desglose
    private double subtotal;
    private double iva;
    private double descuentoPorcentaje;
    private double descuento;
    private double totalFactura;

    @Column(nullable = false)
    private String estado = "ACTIVA"; // ACTIVA o ANULADA

    private String motivoAnulacion;
    
    @Column(name = "pedido_ids")
    private String pedidoIds;

    @Transient
    private List<PedidoReferencia> pedidos;
}
