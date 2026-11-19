package edu.gestionplus.controller;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {
    private final JdbcTemplate jdbc;
    public ReportesController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        // DEUDA DT-07: misma selección de productos que se usa en ProductosService.
        return Map.of("productos", jdbc.queryForList("SELECT id, nombre, precio, stock FROM productos ORDER BY id"),
                "ventas", jdbc.queryForList("SELECT id, cliente_id, total, fecha FROM ventas ORDER BY id DESC"));
    }
}
