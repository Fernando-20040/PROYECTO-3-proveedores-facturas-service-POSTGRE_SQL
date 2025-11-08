package com.multipedidos.proveedores_facturas_service.service;

import com.multipedidos.proveedores_facturas_service.client.PedidoClient;
import com.multipedidos.proveedores_facturas_service.dto.PedidoReferencia;
import com.multipedidos.proveedores_facturas_service.entity.Factura;
import com.multipedidos.proveedores_facturas_service.repository.FacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PedidoClient pedidoClient;

    public FacturaService(FacturaRepository facturaRepository, PedidoClient pedidoClient) {
        this.facturaRepository = facturaRepository;
        this.pedidoClient = pedidoClient;
    }

    // 🔹 Listar todas las facturas, incluyendo los pedidos asociados
    public List<Factura> listarFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        for (Factura factura : facturas) {
            try {
                List<PedidoReferencia> pedidos = pedidoClient.obtenerPedidosPorCliente(factura.getProveedorId());
                factura.setPedidos((pedidos != null && !pedidos.isEmpty()) ? pedidos : null);
            } catch (Exception e) {
                System.err.println("⚠️ Error al obtener pedidos para factura " + factura.getId() + ": " + e.getMessage());
                factura.setPedidos(null);
            }
        }
        return facturas;
    }

    // 🔹 Obtener factura con sus pedidos asociados
    public Factura obtenerFacturaConPedidos(Long id) {
        Optional<Factura> optFactura = facturaRepository.findById(id);
        if (!optFactura.isPresent()) return null;

        Factura factura = optFactura.get();
        try {
            List<PedidoReferencia> pedidos = pedidoClient.obtenerPedidosPorCliente(factura.getProveedorId());
            factura.setPedidos((pedidos != null && !pedidos.isEmpty()) ? pedidos : null);
        } catch (Exception e) {
            System.err.println("⚠️ Error al obtener pedidos asociados a la factura " + id + ": " + e.getMessage());
            factura.setPedidos(null);
        }

        return factura;
    }

    // 🔹 Crear nueva factura (marca pedidos como FACTURADOS)
    public Factura guardarFactura(Factura factura) {
        if (factura.getProveedorId() == null) {
            throw new IllegalArgumentException("Debe indicar un proveedor.");
        }

        Long proveedorId = factura.getProveedorId();
        System.out.println("🔎 Buscando pedidos asociados al proveedor/cliente " + proveedorId + " ...");

        List<PedidoReferencia> pedidos = pedidoClient.obtenerPedidosPorCliente(proveedorId);
        double total = 0.0;

        if (pedidos != null && !pedidos.isEmpty()) {
            factura.setPedidos(pedidos);
            total = pedidos.stream().mapToDouble(PedidoReferencia::getTotal).sum();
            System.out.println("✅ Se encontraron " + pedidos.size() + " pedidos. Total calculado: Q" + total);
        } else {
            if (factura.getMonto() == 0) {
                throw new IllegalArgumentException("Debe ingresar un monto manual si no hay pedidos asociados.");
            }
            factura.setPedidos(null);
            total = factura.getMonto();
            System.out.println("⚠️ Factura manual creada con monto Q" + total);
        }

        factura.setMonto(total);
        factura.setFecha(LocalDate.now());
        factura.setEstado("ACTIVA");

        Factura guardada = facturaRepository.save(factura);
        guardada.setPedidos(factura.getPedidos());

        // 🔹 Marcar pedidos como FACTURADOS
        try {
            if (pedidos != null && !pedidos.isEmpty()) {
                for (PedidoReferencia ref : pedidos) {
                    if (ref.getPedidoId() != null) {
                        pedidoClient.actualizarEstadoPedido(ref.getPedidoId(), "FACTURADO");
                        System.out.println("🧾 Pedido " + ref.getPedidoId() + " marcado como FACTURADO.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo actualizar el estado de los pedidos: " + e.getMessage());
        }

        System.out.println("🧾 Factura guardada correctamente con total Q" + total);
        return guardada;
    }

    // 🔹 Anular factura (sin eliminar)
    public Factura anularFactura(Long id, String motivo) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada."));

        if ("ANULADA".equalsIgnoreCase(factura.getEstado())) {
            throw new IllegalArgumentException("La factura ya está anulada.");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar un motivo para anular la factura.");
        }

        factura.setEstado("ANULADA");
        factura.setMotivoAnulacion(motivo);

        // 🔄 Revertir pedidos a PENDIENTE
        try {
            if (factura.getPedidos() != null && !factura.getPedidos().isEmpty()) {
                for (PedidoReferencia pedido : factura.getPedidos()) {
                    if (pedido.getPedidoId() != null) {
                        pedidoClient.actualizarEstadoPedido(pedido.getPedidoId(), "PENDIENTE");
                        System.out.println("🔁 Pedido " + pedido.getPedidoId() + " devuelto a estado PENDIENTE.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ No se pudieron revertir los pedidos asociados: " + e.getMessage());
        }

        Factura anulada = facturaRepository.save(factura);
        System.out.println("❌ Factura #" + id + " anulada con motivo: " + motivo);
        return anulada;
    }
}
