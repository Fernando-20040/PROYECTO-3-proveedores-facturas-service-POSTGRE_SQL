package com.multipedidos.proveedores_facturas_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.multipedidos.proveedores_facturas_service.entity.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}
