package edu.gestionplus.service;

import edu.gestionplus.model.CrearVenta;
import edu.gestionplus.model.ItemVenta;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
public class VentasService {
    private final JdbcTemplate jdbc;
    private final ClientesService clientes;
    private final ProductosService productos;

    public VentasService(JdbcTemplate jdbc, ClientesService clientes, ProductosService productos) {
        this.jdbc = jdbc;
        this.clientes = clientes;
        this.productos = productos;
    }

    @Transactional
    public Map<String, Object> registrarVenta(CrearVenta solicitud) {
        // DEUDA DT-03/04: método extenso que coordina validación, lectura de datos,
        // cálculo, persistencia de venta, inventario y facturación.
        if (solicitud == null || solicitud.clienteId() == null || solicitud.items() == null
                || solicitud.items().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Venta incompleta");
        try {
            clientes.obtener(solicitud.clienteId()); // DEUDA DT-05: dependencia de implementación concreta.
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente no existe");
        }
        // Dependencia adicional sin necesidad real: se pide la lista completa para comprobar
        // que el catálogo tenga productos. Ilustra acoplamiento innecesario.
        if (productos.listar().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay productos");

        // Fijar filas y bloquear stock durante la transacción para evitar ventas simultáneas
        // que consuman las mismas unidades. La deuda es de diseño, no de integridad de datos.
        Map<Long, Integer> cantidades = new java.util.LinkedHashMap<>();
        for (ItemVenta item : solicitud.items()) {
            if (item == null || item.productoId() == null || item.cantidad() == null || item.cantidad() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ítem incorrecto");
            try {
                cantidades.merge(item.productoId(), item.cantidad(), Math::addExact);
            } catch (ArithmeticException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad excesiva");
            }
        }
        List<Map<String, Object>> renglones = new java.util.ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Long productoId : cantidades.keySet().stream().sorted().toList()) {
            List<Map<String, Object>> encontrados = jdbc.queryForList(
                // DEUDA DT-07: SELECT de producto equivalente al que aparece en ProductosService.
                "SELECT id, nombre, precio, stock FROM productos WHERE id = ? FOR UPDATE", productoId);
            if (encontrados.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto inexistente: " + productoId);
            Map<String, Object> producto = encontrados.get(0);
            int cantidad = cantidades.get(productoId);
            if (((Number) producto.get("stock")).intValue() < cantidad)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventario insuficiente: " + productoId);
            BigDecimal precio = (BigDecimal) producto.get("precio");
            // DEUDA DT-06: regla de cálculo duplicada en FacturasService.
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);
            renglones.add(Map.of("productoId", productoId, "cantidad", cantidad,
                    "precio", precio, "subtotal", subtotal));
        }
        final BigDecimal montoFinal = total;
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO ventas (cliente_id, total) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, solicitud.clienteId());
            ps.setBigDecimal(2, montoFinal);
            return ps;
        }, llave);
        long ventaId = llave.getKey().longValue();
        for (Map<String, Object> renglon : renglones) {
            long productoId = ((Number) renglon.get("productoId")).longValue();
            int cantidad = ((Number) renglon.get("cantidad")).intValue();
            jdbc.update("INSERT INTO venta_detalle(venta_id,producto_id,cantidad,precio_unitario,subtotal) VALUES (?,?,?,?,?)",
                ventaId, productoId, cantidad, renglon.get("precio"), renglon.get("subtotal"));
            jdbc.update("UPDATE productos SET stock = stock - ? WHERE id = ?", cantidad, productoId);
        }
        // DEUDA DT-03: preparación/creación de factura dentro del servicio de ventas.
        jdbc.update("INSERT INTO facturas(venta_id,monto) VALUES (?,?)", ventaId, total);
        return Map.of("ventaId", ventaId, "total", total, "items", renglones.size());
    }

    public Object consultar(Long id) {
        return Map.of("venta", jdbc.queryForMap("SELECT id, cliente_id, total, fecha FROM ventas WHERE id=?", id),
                "detalle", jdbc.queryForList("SELECT producto_id,cantidad,precio_unitario,subtotal FROM venta_detalle WHERE venta_id=?", id));
    }
}
