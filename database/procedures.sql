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
INSERT INTO users (
    email,
    password,
    status,
    identifier_document
)
VALUES (
           p_email,
           p_password,
           'active',
           p_identifier_document
       );
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


-- =============================================================================
-- ProcCreatePassenger
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcCreatePassenger;
DELIMITER $$
CREATE PROCEDURE ProcCreatePassenger(
    IN p_id               BIGINT,
    IN p_birth_date       DATE,
    IN p_phone            VARCHAR(20),
    IN p_type             VARCHAR(50),
    IN p_registration_number VARCHAR(50),
    IN p_bond_proof       VARCHAR(255),
    IN p_id_address       BIGINT
)
BEGIN
INSERT INTO passengers (
    id,
    birth_date,
    phone,
    type,
    registration_number,
    bond_proof,
    id_address
)
VALUES (
           p_id,
           p_birth_date,
           p_phone,
           p_type,
           p_registration_number,
           p_bond_proof,
           p_id_address
       );
END$$
DELIMITER ;

-- =============================================================================
-- ProcCreateDriver
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcCreateDriver;
DELIMITER $$
CREATE PROCEDURE ProcCreateDriver(
    IN p_id               BIGINT,
    IN p_cnh_number       VARCHAR(20),
    IN p_cnh_category     VARCHAR(10),
    IN p_name             VARCHAR(255),
    IN p_birth_date       DATE,
    IN p_cnh_validity_date DATE,
    IN p_phone            VARCHAR(20),
    IN p_id_address       BIGINT
)
BEGIN
INSERT INTO drivers (
    id,
    cnh_number,
    cnh_category,
    name,
    birth_date,
    cnh_validity_date,
    phone,
    id_address
)
VALUES (
           p_id,
           p_cnh_number,
           p_cnh_category,
           p_name,
           p_birth_date,
           p_cnh_validity_date,
           p_phone,
           p_id_address
       );
END$$
DELIMITER ;
