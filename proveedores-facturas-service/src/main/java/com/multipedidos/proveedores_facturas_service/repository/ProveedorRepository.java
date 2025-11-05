package com.multipedidos.proveedores_facturas_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.multipedidos.proveedores_facturas_service.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
