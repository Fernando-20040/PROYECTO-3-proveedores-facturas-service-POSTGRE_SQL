package com.multipedidos.proveedores_facturas_service.dto;

import lombok.Data;

@Data
public class PedidoReferencia {

    private Long id;           // ID del pedido (algunos endpoints lo devuelven así)
    private Long pedidoId;     // compatibilidad con otros DTOs
    private Long clienteId;    // ID del cliente asociado
    private String nombre;     // descripción o nombre del pedido
    private double subtotal;   // subtotal del pedido
    private double iva;        // IVA aplicado
    private double descuento;  // descuento aplicado
    private double total;      // total final del pedido
}
