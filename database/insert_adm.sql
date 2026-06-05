-- ==============================================================================
-- USUÁRIO ADMINISTRADOR PRIMÁRIO
-- ==============================================================================

INSERT INTO users (id, email, password, status, identifier_document)
VALUES (1, 'admin@siti.com', '123456', 'active', '12345678900');

INSERT INTO administrators (id, name, city, state)
VALUES (1, 'Admin Teste', NULL, NULL);