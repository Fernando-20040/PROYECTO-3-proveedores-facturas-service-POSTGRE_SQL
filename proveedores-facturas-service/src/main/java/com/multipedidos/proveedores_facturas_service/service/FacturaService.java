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

    public List<Factura> listarFacturas() {
        List<Factura> facturas = facturaRepository.findAll();

        for (Factura factura : facturas) {
            try {
                List<PedidoReferencia> pedidos = pedidoClient.obtenerPedidosPorCliente(factura.getProveedorId());
                factura.setPedidos((pedidos != null && !pedidos.isEmpty()) ? pedidos : null);
            } catch (Exception e) {
                factura.setPedidos(null);
            }
        }

        return facturas;
    }

    public Factura obtenerFacturaConPedidos(Long id) {
        Optional<Factura> optFactura = facturaRepository.findById(id);
        if (optFactura.isEmpty()) return null;

        Factura factura = optFactura.get();
        try {
            List<PedidoReferencia> pedidos = pedidoClient.obtenerPedidosPorCliente(factura.getProveedorId());
            factura.setPedidos((pedidos != null && !pedidos.isEmpty()) ? pedidos : null);
        } catch (Exception e) {
            factura.setPedidos(null);
        }

        return factura;
    }
    
    public Factura guardarFactura(Factura factura) {
        if (factura.getProveedorId() == null) {
            throw new IllegalArgumentException("Debe indicar un proveedor.");
        }

        Long clienteIdRelacionado = factura.getProveedorId();

        System.out.println("🔎 Buscando pedidos del cliente " + clienteIdRelacionado + " ...");
        List<PedidoReferencia> pedidos = pedidoClient.obtenerPedidosPorCliente(clienteIdRelacionado);

        double total = 0.0;

        if (pedidos != null && !pedidos.isEmpty()) {
            // ✅ Caso 1: factura automática
            factura.setPedidos(pedidos);
            total = pedidos.stream().mapToDouble(PedidoReferencia::getTotal).sum();
            System.out.println("✅ Se encontraron " + pedidos.size() + " pedidos. Total calculado: Q" + total);
        } else {
            // ⚠️ Caso 2: factura manual
            if (factura.getMonto() == 0) {
                throw new IllegalArgumentException("Debe ingresar un monto manual si no hay pedidos asociados.");
            }
            factura.setPedidos(null);
            total = factura.getMonto();
            System.out.println("⚠️ Factura manual creada con monto Q" + total);
        }

        factura.setMonto(total);
        factura.setFecha(LocalDate.now());

        Factura guardada = facturaRepository.save(factura);
        guardada.setPedidos(factura.getPedidos());

        System.out.println("🧾 Factura guardada correctamente con total Q" + total);
        return guardada;
    }
}
