-- Test data for H2 DB
INSERT INTO users (id, email, password, status, identifier_document, name) VALUES (999, 'admin@siti.edu.br', '$2a$10$WqfV4QZ0a7k5w3J2F.6.QO3f/1f/R7Lw4Q7Vf1QZ0a7k5w3J2F.6', 'Ativo', '00000000000', 'Admin Teste');
INSERT INTO administrators (id, name, city, state) VALUES (999, 'Admin Teste', 'Quixadá', 'CE');
