-- H2 schema definition for tests

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    identifier_document VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    neighborhood VARCHAR(255),
    street VARCHAR(255),
    building_number VARCHAR(20),
    complement VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS administrators (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(100),
    id_address BIGINT,
    CONSTRAINT fk_admin_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_admin_address FOREIGN KEY (id_address) REFERENCES addresses(id)
);

-- Aliases for Stored Procedures pointing to Procedures.java class
CREATE ALIAS IF NOT EXISTS ProcCreateUser FOR "com.siti.sitiapi.h2.Procedures.procCreateUser";
CREATE ALIAS IF NOT EXISTS ProcGetUserByEmail FOR "com.siti.sitiapi.h2.Procedures.procGetUserByEmail";
CREATE ALIAS IF NOT EXISTS ProcExistUserByEmail FOR "com.siti.sitiapi.h2.Procedures.procExistUserByEmail";
CREATE ALIAS IF NOT EXISTS ProcGetUserByEmailAndPassword FOR "com.siti.sitiapi.h2.Procedures.procGetUserByEmailAndPassword";
CREATE ALIAS IF NOT EXISTS HasUserAdministratorById FOR "com.siti.sitiapi.h2.Procedures.hasUserAdministratorById";
