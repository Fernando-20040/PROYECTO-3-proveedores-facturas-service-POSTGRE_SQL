package com.multipedidos.proveedores_facturas_service.dto;

import lombok.Data;

@Data
public class PedidoReferencia {

    private Long pedidoId;     // ID del pedido
    private Long clienteId;    // ID del cliente que hizo el pedido
    private String nombre;     // Nombre o descripción del pedido
    private double total;      // Total del pedido
}
