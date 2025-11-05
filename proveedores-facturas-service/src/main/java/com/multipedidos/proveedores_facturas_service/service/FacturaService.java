package com.multipedidos.proveedores_facturas_service.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;
import com.multipedidos.proveedores_facturas_service.entity.*;
import com.multipedidos.proveedores_facturas_service.repository.FacturaRepository;
import com.multipedidos.common.OperacionesNegocio;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> obtenerFactura(Long id) {
        return facturaRepository.findById(id);
    }

    public Factura guardarFactura(Factura factura) {
        if (factura.getPedidos() == null || factura.getPedidos().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un pedido asociado.");
        }

        double total = factura.getPedidos().stream()
                .mapToDouble(PedidoReferencia::getTotal)
                .sum();

        double totalConIVA = OperacionesNegocio.calcularTotalConIVA(total);
        factura.setTotalFactura(totalConIVA);

        return facturaRepository.save(factura);
    }
}
