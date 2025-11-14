package com.multipedidos.proveedores_facturas_service.controller;

import com.multipedidos.proveedores_facturas_service.dto.FacturaInput;
import com.multipedidos.proveedores_facturas_service.entity.Factura;
import com.multipedidos.proveedores_facturas_service.service.FacturaService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/facturas")
@CrossOrigin(origins = "http://localhost:3000")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    /* ====================== LISTAR TODAS ====================== */
    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        List<Factura> facturas = facturaService.listarFacturas();
        return facturas.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(facturas);
    }

    /* ====================== DETALLE ====================== */
    @GetMapping("/detalle/{id}")
    public ResponseEntity<Factura> obtenerDetalle(@PathVariable Long id) {
        Factura factura = facturaService.obtenerFacturaConPedidos(id);
        return factura != null
                ? ResponseEntity.ok(factura)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /* ====================== CREAR ====================== */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crear(@RequestBody FacturaInput input) {
        try {
            Factura nueva = facturaService.guardarFactura(input);

            // 🔹 Devuelve JSON completo con la factura creada
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nueva);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la factura: " + e.getMessage()));
        }
    }

    /* ====================== ANULAR ====================== */
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anularFactura(@PathVariable Long id, @RequestParam String motivo) {
        try {
            Factura anulada = facturaService.anularFactura(id, motivo);
            return ResponseEntity.ok(anulada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al anular factura: " + e.getMessage()));
        }
    }
}
