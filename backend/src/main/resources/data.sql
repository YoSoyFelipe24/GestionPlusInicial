INSERT INTO clientes (id,nombre,correo) VALUES (1,'Cliente Demo','cliente.demo@example.com') ON DUPLICATE KEY UPDATE nombre=nombre;
INSERT INTO productos (id,nombre,precio,stock) VALUES (1,'Teclado',85000.00,20),(2,'Mouse',45000.00,35),(3,'Monitor',680000.00,10) ON DUPLICATE KEY UPDATE nombre=nombre;
