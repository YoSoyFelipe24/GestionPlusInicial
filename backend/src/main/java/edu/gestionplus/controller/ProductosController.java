package edu.gestionplus.controller;

import edu.gestionplus.service.ProductosService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {
    private final ProductosService productos;
    public ProductosController(ProductosService productos) { this.productos = productos; }
    @GetMapping public Object listar() { return productos.listar(); }
}
