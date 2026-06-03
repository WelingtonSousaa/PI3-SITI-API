-- =============================================================================
-- ProcCreateUser
-- =============================================================================

DROP PROCEDURE IF EXISTS ProcCreateUser;
DELIMITER $$
CREATE PROCEDURE ProcCreateUser(
    IN p_email               VARCHAR(255),
    IN p_password            VARCHAR(255),
    IN p_identifier_document VARCHAR(50),
    IN p_api_key             VARCHAR(255)
)
BEGIN
INSERT INTO users (
    email,
    password,
    status,
    identifier_document,
    api_key
)
VALUES (
       p_email,
       p_password,
       'active',
       p_identifier_document,
       p_api_key
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
SELECT
    id,
    email,
    status,
    identifier_document,
    api_key
FROM users
WHERE
    email = p_email;
END$$
DELIMITER ;