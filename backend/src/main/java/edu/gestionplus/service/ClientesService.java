package edu.gestionplus.service;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClientesService {
    private final JdbcTemplate jdbc;
    public ClientesService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String, Object> obtener(Long id) {
        return jdbc.queryForMap("SELECT id, nombre, correo FROM clientes WHERE id = ?", id);
    }
}
