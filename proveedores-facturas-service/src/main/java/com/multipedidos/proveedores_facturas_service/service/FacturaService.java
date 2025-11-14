package com.multipedidos.proveedores_facturas_service.service;

import com.multipedidos.common.OperacionesNegocio;
import com.multipedidos.proveedores_facturas_service.client.PedidoClient;
import com.multipedidos.proveedores_facturas_service.dto.FacturaInput;
import com.multipedidos.proveedores_facturas_service.dto.PedidoReferencia;
import com.multipedidos.proveedores_facturas_service.entity.Factura;
import com.multipedidos.proveedores_facturas_service.repository.FacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PedidoClient pedidoClient;

    public FacturaService(FacturaRepository facturaRepository, PedidoClient pedidoClient) {
        this.facturaRepository = facturaRepository;
        this.pedidoClient = pedidoClient;
    }

    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    /* ============================================================
       🔥 AQUI VIENE LA PARTE CORREGIDA: RECONSTRUIR PEDIDOS ASOCIADOS
       ============================================================ */
    public Factura obtenerFacturaConPedidos(Long id) {
        Factura factura = facturaRepository.findById(id).orElse(null);
        if (factura == null) return null;

        String idsTexto = factura.getPedidoIds();

        if (idsTexto != null && !idsTexto.isBlank()) {

            List<Long> ids = Arrays.stream(idsTexto.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            List<PedidoReferencia> pedidos = ids.stream()
                    .map(pedidoClient::obtenerPedidoPorId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            factura.setPedidos(pedidos);

        } else {
            factura.setPedidos(List.of());
        }

        return factura;
    }

    /* ============================================================
       🔥 AQUI SE AGREGA GUARDAR pedidoIds AL MOMENTO DE CREAR FACTURA
       ============================================================ */
    public Factura guardarFactura(FacturaInput input) {
        if (input.getProveedorId() == null)
            throw new IllegalArgumentException("Debe indicar un proveedor.");

        Factura factura = new Factura();
        factura.setProveedorId(input.getProveedorId());
        factura.setFecha(LocalDate.now());
        factura.setEstado("ACTIVA");

        double subtotal = 0;
        double descuentoPorcentaje = Optional.ofNullable(input.getDescuentoPorcentaje()).orElse(0.0);
        List<PedidoReferencia> pedidosAsociados = new ArrayList<>();

        // ======== SI VIENEN PEDIDOS SELECCIONADOS (AUTO) ========
        if (input.getPedidosIds() != null && !input.getPedidosIds().isEmpty()) {

            for (Long pedidoId : input.getPedidosIds()) {
                PedidoReferencia ref = pedidoClient.obtenerPedidoPorId(pedidoId);
                if (ref != null) {
                    pedidosAsociados.add(ref);
                    subtotal += ref.getTotal();
                }
            }

            // 🔥 GUARDA LOS IDS EN LA FACTURA (EJ: "8,9,10")
            String idsTexto = input.getPedidosIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            factura.setPedidoIds(idsTexto);

        }
        // ======== SI VIENE MONTO MANUAL (MANUAL) ========
        else if (input.getMontoManual() != null && input.getMontoManual() > 0) {
            subtotal = input.getMontoManual();
            factura.setPedidoIds(null);
        }
        // ======== ERROR SI NO VINO NADA ========
        else {
            throw new IllegalArgumentException("Debe seleccionar pedidos o indicar monto manual.");
        }

        // ======== CALCULOS =========
        double iva = OperacionesNegocio.calcularIVA(subtotal);
        double totalConIVA = OperacionesNegocio.calcularTotalConIVA(subtotal);
        double totalConDescuento = OperacionesNegocio.aplicarDescuento(totalConIVA, descuentoPorcentaje);
        double descuento = totalConIVA - totalConDescuento;

        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setDescuentoPorcentaje(descuentoPorcentaje);
        factura.setDescuento(descuento);
        factura.setTotalFactura(totalConDescuento);
        factura.setMonto(totalConDescuento);

        Factura guardada = facturaRepository.save(factura);

        // Guarda pedidos en memoria para la respuesta
        guardada.setPedidos(pedidosAsociados);

        // Marcar pedidos como FACTURADO
        for (PedidoReferencia ref : pedidosAsociados) {
            Long pId = (ref.getPedidoId() != null) ? ref.getPedidoId() : ref.getId();
            if (pId != null) {
                pedidoClient.actualizarEstadoPedido(pId, "FACTURADO");
            }
        }

        return guardada;
    }

    public Factura anularFactura(Long id, String motivo) {

        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada."));

        if ("ANULADA".equalsIgnoreCase(factura.getEstado()))
            throw new IllegalArgumentException("La factura ya está anulada.");

        factura.setEstado("ANULADA");
        factura.setMotivoAnulacion(motivo);

        if (factura.getPedidos() != null) {
            for (PedidoReferencia p : factura.getPedidos()) {
                Long idPedido = (p.getPedidoId() != null) ? p.getPedidoId() : p.getId();
                if (idPedido != null) {
                    pedidoClient.actualizarEstadoPedido(idPedido, "PENDIENTE");
                }
            }
        }

        return facturaRepository.save(factura);
    }
}
