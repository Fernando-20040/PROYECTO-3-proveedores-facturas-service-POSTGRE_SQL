package com.multipedidos.proveedores_facturas_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class FacturaInput {
    private Long proveedorId;         // proveedor asociado
    private List<Long> pedidosIds;    // pedidos seleccionados
    private Double montoManual;       // opcional si no se seleccionan pedidos
    private Double descuentoPorcentaje;
}
