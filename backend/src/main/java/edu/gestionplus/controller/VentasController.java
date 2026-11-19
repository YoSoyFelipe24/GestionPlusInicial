package edu.gestionplus.controller;

import edu.gestionplus.model.CrearVenta;
import edu.gestionplus.service.VentasService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {
    private final VentasService ventas;
    public VentasController(VentasService ventas) { this.ventas = ventas; }
    @PostMapping public Object crear(@RequestBody CrearVenta solicitud) { return ventas.registrarVenta(solicitud); }
    @GetMapping("/{id}") public Object consultar(@PathVariable Long id) { return ventas.consultar(id); }
}
