package com.multipedidos.proveedores_facturas_service.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.multipedidos.proveedores_facturas_service.entity.Proveedor;
import com.multipedidos.proveedores_facturas_service.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> obtenerProveedor(Long id) {
        return proveedorRepository.findById(id);
    }

    public Proveedor guardarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }
}
