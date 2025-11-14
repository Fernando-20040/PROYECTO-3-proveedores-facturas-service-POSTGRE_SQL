package com.multipedidos.proveedores_facturas_service.client;

import com.multipedidos.proveedores_facturas_service.dto.PedidoReferencia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class PedidoClient {

    private final RestTemplate restTemplate;

    @Value("${pedidos.service.url:http://localhost:8080}")
    private String baseUrl;

    public PedidoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PedidoReferencia> obtenerPedidosPorCliente(Long clienteId) {
        try {
            String url = baseUrl + "/pedidos/cliente/" + clienteId;
            ResponseEntity<PedidoReferencia[]> response =
                    restTemplate.getForEntity(url, PedidoReferencia[].class);
            return response.getBody() != null ? Arrays.asList(response.getBody()) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener pedidos por cliente: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 🆕 Obtener un pedido por ID
    public PedidoReferencia obtenerPedidoPorId(Long pedidoId) {
        try {
            String url = baseUrl + "/pedidos/" + pedidoId;
            ResponseEntity<PedidoReferencia> response =
                    restTemplate.getForEntity(url, PedidoReferencia.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("⚠️ Error al obtener pedido " + pedidoId + ": " + e.getMessage());
            return null;
        }
    }

    // 🔄 Actualizar estado de un pedido
    public void actualizarEstadoPedido(Long pedidoId, String nuevoEstado) {
        try {
            String url = baseUrl + "/pedidos/" + pedidoId + "/estado?estado=" + nuevoEstado;
            restTemplate.put(url, null);
            System.out.println("🔁 Pedido " + pedidoId + " actualizado a " + nuevoEstado);
        } catch (Exception e) {
            System.err.println("⚠️ Error al actualizar estado del pedido: " + e.getMessage());
        }
    }
}
