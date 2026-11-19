package edu.gestionplus.model;

import java.util.List;
public record CrearVenta(Long clienteId, List<ItemVenta> items) {}
