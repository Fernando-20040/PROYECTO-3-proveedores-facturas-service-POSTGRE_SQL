package com.multipedidos.proveedores_facturas_service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.multipedidos.proveedores_facturas_service.entity.Proveedor;
import com.multipedidos.proveedores_facturas_service.service.ProveedorService;

@RestController
@RequestMapping("/proveedores")
@CrossOrigin("*")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<Proveedor> listar() {
        return proveedorService.listarProveedores();
    }

    @GetMapping("/{id}")
    public Proveedor obtener(@PathVariable Long id) {
        return proveedorService.obtenerProveedor(id).orElse(null);
    }

    @PostMapping
    public Proveedor crear(@RequestBody Proveedor proveedor) {
        return proveedorService.guardarProveedor(proveedor);
    }
}
