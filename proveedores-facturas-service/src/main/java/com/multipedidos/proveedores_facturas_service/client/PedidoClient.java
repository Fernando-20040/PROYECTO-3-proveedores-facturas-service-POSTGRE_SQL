package com.multipedidos.proveedores_facturas_service.client;

import com.multipedidos.proveedores_facturas_service.dto.PedidoReferencia;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
public class PedidoClient {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080";

    public PedidoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PedidoReferencia> obtenerPedidosPorCliente(Long clienteId) {
        try {
            String url = BASE_URL + "/pedidos/cliente/" + clienteId;
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
}
