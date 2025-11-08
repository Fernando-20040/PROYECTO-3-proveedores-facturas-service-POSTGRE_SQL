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

    // 🔹 Puedes definir la URL del servicio de pedidos en application.properties:
    // pedidos.service.url=http://localhost:8080
    @Value("${pedidos.service.url:http://localhost:8080}")
    private String baseUrl;

    public PedidoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 🔍 Obtener todos los pedidos de un cliente
     */
    public List<PedidoReferencia> obtenerPedidosPorCliente(Long clienteId) {
        try {
            String url = baseUrl + "/pedidos/cliente/" + clienteId;
            ResponseEntity<PedidoReferencia[]> response =
                    restTemplate.getForEntity(url, PedidoReferencia[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                System.out.println("✅ Pedidos obtenidos del cliente " + clienteId + ": " + response.getBody().length);
                return Arrays.asList(response.getBody());
            } else {
                System.err.println("⚠️ Respuesta vacía o status no OK: " + response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("❌ Error al conectar con el servicio de pedidos:");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 🔄 Marcar un pedido como FACTURADO (usado cuando se crea una factura)
     */
    public void marcarPedidoComoFacturado(Long pedidoId) {
        try {
            String url = baseUrl + "/pedidos/" + pedidoId + "/facturar";
            restTemplate.put(url, null);
            System.out.println("✅ Pedido " + pedidoId + " marcado como FACTURADO.");
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo marcar el pedido " + pedidoId + " como facturado: " + e.getMessage());
        }
    }

    /**
     * 🔄 Cambiar el estado de un pedido (por ejemplo: PENDIENTE, FACTURADO, ANULADO)
     */
    public void actualizarEstadoPedido(Long pedidoId, String nuevoEstado) {
        try {
            String url = baseUrl + "/pedidos/" + pedidoId + "/estado?estado=" + nuevoEstado;
            restTemplate.put(url, null);
            System.out.println("🔁 Estado del pedido " + pedidoId + " actualizado a " + nuevoEstado);
        } catch (Exception e) {
            System.err.println("⚠️ Error al actualizar estado del pedido " + pedidoId + ": " + e.getMessage());
        }
    }
}
