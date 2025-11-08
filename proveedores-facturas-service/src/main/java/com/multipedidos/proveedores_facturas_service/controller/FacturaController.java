package com.multipedidos.proveedores_facturas_service.controller;

import com.multipedidos.proveedores_facturas_service.entity.Factura;
import com.multipedidos.proveedores_facturas_service.service.FacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
@CrossOrigin(origins = "http://localhost:3000")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<Factura> listar() {
        return facturaService.listarFacturas();
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<Factura> obtenerDetalle(@PathVariable Long id) {
        Factura factura = facturaService.obtenerFacturaConPedidos(id);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(factura);
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@RequestBody Factura factura) {
        Factura nueva = facturaService.guardarFactura(factura);
        return ResponseEntity.ok(nueva);
    }

    // 🔹 Nueva ruta para anular factura con motivo
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anularFactura(@PathVariable Long id, @RequestParam String motivo) {
        try {
            Factura anulada = facturaService.anularFactura(id, motivo);
            return ResponseEntity.ok(anulada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
