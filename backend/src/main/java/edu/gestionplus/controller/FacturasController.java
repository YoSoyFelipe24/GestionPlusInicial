package edu.gestionplus.controller;

import edu.gestionplus.service.FacturasService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturasController {
    private final FacturasService facturas;
    public FacturasController(FacturasService facturas) { this.facturas = facturas; }
    @GetMapping("/{ventaId}") public Object porVenta(@PathVariable Long ventaId) { return facturas.obtenerPorVenta(ventaId); }
}
