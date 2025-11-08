package com.multipedidos.proveedores_facturas_service.dto;

import lombok.Data;

@Data
public class PedidoReferencia {
    private Long id;        // id del pedido
    private Long clienteId; // cliente relacionado
    private double total;   // total calculado
    private String nombre; // 🔹 nombre del producto o pedido

}
