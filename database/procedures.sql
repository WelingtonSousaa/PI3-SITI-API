
-- ==============================================================================
-- PROCEDURES
-- ==============================================================================

-- =============================================================================
-- ProcCreateUser
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcCreateUser;
DELIMITER $$
CREATE PROCEDURE ProcCreateUser(
    IN p_email               VARCHAR(255),
    IN p_password            VARCHAR(255),
    IN p_identifier_document VARCHAR(50)
)
BEGIN
INSERT INTO users (email, password, status, identifier_document)
VALUES (p_email, p_password, 'active', p_identifier_document);
END$$
DELIMITER ;


-- =============================================================================
-- ProcGetUserByEmail
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcGetUserByEmail;
DELIMITER $$
CREATE PROCEDURE ProcGetUserByEmail(IN p_email VARCHAR(255))
BEGIN
SELECT id, email, status, identifier_document
FROM users
WHERE email = p_email;
END$$
DELIMITER ;


-- =============================================================================
-- ProcExistUserByEmail
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcExistUserByEmail;
DELIMITER $$
CREATE PROCEDURE ProcExistUserByEmail(IN p_email VARCHAR(255))
BEGIN
SELECT EXISTS(SELECT 1 FROM users WHERE email = p_email) AS exists_user;
END$$
DELIMITER ;

-- =============================================================================
-- ProcGetUserByEmailAndPassword
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcGetUserByEmailAndPassword;
DELIMITER $$
CREATE PROCEDURE ProcGetUserByEmailAndPassword(
    IN p_email    VARCHAR(255),
    IN p_password VARCHAR(255)
)
BEGIN
SELECT id, email
FROM users
WHERE email = p_email AND password = p_password;
END$$
DELIMITER ;


-- =============================================================================
-- HasUserAdministratorById
-- =============================================================================

DROP PROCEDURE IF EXISTS HasUserAdministratorById;
DELIMITER $$
CREATE PROCEDURE HasUserAdministratorById(IN p_id BIGINT)
BEGIN
SELECT EXISTS(
    SELECT 1 FROM administrators WHERE id = p_id
) AS result;
END$$
DELIMITER ;