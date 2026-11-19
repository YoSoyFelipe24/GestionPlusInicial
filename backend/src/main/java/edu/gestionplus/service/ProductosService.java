package edu.gestionplus.service;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductosService {
    private final JdbcTemplate jdbc;
    public ProductosService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String, Object>> listar() {
        // DEUDA DT-07: consulta replicada parcialmente en ventas y reportes.
        return jdbc.queryForList("SELECT id, nombre, precio, stock FROM productos ORDER BY id");
    }
}
