package com.multipedidos.proveedores_facturas_service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.multipedidos.proveedores_facturas_service.entity.Factura;
import com.multipedidos.proveedores_facturas_service.service.FacturaService;

@RestController
@RequestMapping("/facturas")
@CrossOrigin("*")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<Factura> listar() {
        return facturaService.listarFacturas();
    }

    @GetMapping("/{id}")
    public Factura obtener(@PathVariable Long id) {
        return facturaService.obtenerFactura(id).orElse(null);
    }

    @PostMapping
    public Factura crear(@RequestBody Factura factura) {
        return facturaService.guardarFactura(factura);
    }
}
