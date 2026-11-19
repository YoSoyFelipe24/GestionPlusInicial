package edu.gestionplus.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FacturasService {
    private final JdbcTemplate jdbc;
    public FacturasService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String, Object> obtenerPorVenta(Long id) {
        List<Map<String, Object>> detalle = jdbc.queryForList(
            "SELECT cantidad, precio_unitario FROM venta_detalle WHERE venta_id = ?", id);
        // DEUDA DT-06: recalcula el total en lugar de tener una regla de cálculo común.
        BigDecimal totalRecalculado = BigDecimal.ZERO;
        for (Map<String, Object> fila : detalle) {
            totalRecalculado = totalRecalculado.add(((BigDecimal) fila.get("precio_unitario"))
                .multiply(BigDecimal.valueOf(((Number) fila.get("cantidad")).longValue())));
        }
        return Map.of("factura", jdbc.queryForMap("SELECT id,venta_id,monto,emitida_en FROM facturas WHERE venta_id=?", id),
            "totalRecalculado", totalRecalculado);
    }
}
