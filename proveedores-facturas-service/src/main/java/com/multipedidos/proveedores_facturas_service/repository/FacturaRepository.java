package com.multipedidos.proveedores_facturas_service.repository;

import com.multipedidos.proveedores_facturas_service.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}
