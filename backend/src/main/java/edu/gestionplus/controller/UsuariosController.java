package edu.gestionplus.controller;

import edu.gestionplus.model.CrearPersona;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    private final JdbcTemplate jdbc;
    public UsuariosController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
    public Object listar() { return jdbc.queryForList("SELECT id, nombre, correo FROM usuarios ORDER BY id"); }

    @PostMapping
    public Map<String, Object> crear(@RequestBody CrearPersona datos) {
        // DEUDA DT-02: validación duplicada también en ClientesController.
        if (datos.nombre() == null || datos.nombre().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre obligatorio");
        if (datos.correo() == null || datos.correo().isBlank() || !datos.correo().contains("@")
                || !datos.correo().contains("."))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo inválido");
        jdbc.update("INSERT INTO usuarios(nombre, correo) VALUES (?, ?)", datos.nombre(), datos.correo());
        return Map.of("mensaje", "Usuario creado");
    }
}
