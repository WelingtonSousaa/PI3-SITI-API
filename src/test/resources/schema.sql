-- H2 schema definition for tests

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    identifier_document VARCHAR(50),
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    neighborhood VARCHAR(255),
    street VARCHAR(255),
    building_number VARCHAR(20),
    complement VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS passengers (
    id BIGINT PRIMARY KEY,
    birth_date DATE,
    phone VARCHAR(20),
    type VARCHAR(50),
    registration_number VARCHAR(50),
    bond_proof VARCHAR(255),
    photo_url VARCHAR(500),
    id_address BIGINT,
    CONSTRAINT fk_passenger_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_passenger_address FOREIGN KEY (id_address) REFERENCES addresses(id)
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

CREATE TABLE IF NOT EXISTS drivers (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(20),
    birth_date DATE,
    cnh_number VARCHAR(50),
    cnh_category VARCHAR(10),
    cnh_validity_date DATE,
    id_address BIGINT,
    CONSTRAINT fk_driver_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_driver_address FOREIGN KEY (id_address) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    bus_model VARCHAR(255),
    manufacturing_year VARCHAR(10),
    capacity INT,
    accessibility BOOLEAN DEFAULT FALSE,
    operation_status VARCHAR(50),
    id_administrator BIGINT,
    CONSTRAINT fk_bus_admin FOREIGN KEY (id_administrator) REFERENCES administrators(id)
);

CREATE TABLE IF NOT EXISTS routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    description TEXT,
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `time` VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50),
    id_route BIGINT,
    id_address BIGINT,
    id_schedule BIGINT,
    CONSTRAINT fk_stop_route FOREIGN KEY (id_route) REFERENCES routes(id),
    CONSTRAINT fk_stop_address FOREIGN KEY (id_address) REFERENCES addresses(id),
    CONSTRAINT fk_stop_schedule FOREIGN KEY (id_schedule) REFERENCES schedules(id)
);

CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `date` DATE,
    status VARCHAR(50),
    id_route BIGINT,
    id_bus BIGINT,
    id_driver BIGINT,
    CONSTRAINT fk_trip_route FOREIGN KEY (id_route) REFERENCES routes(id),
    CONSTRAINT fk_trip_bus FOREIGN KEY (id_bus) REFERENCES buses(id),
    CONSTRAINT fk_trip_driver FOREIGN KEY (id_driver) REFERENCES drivers(id)
);

CREATE TABLE IF NOT EXISTS passenger_trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_passenger BIGINT,
    id_trip BIGINT,
    id_schedule BIGINT,
    CONSTRAINT fk_pt_passenger FOREIGN KEY (id_passenger) REFERENCES passengers(id),
    CONSTRAINT fk_pt_trip FOREIGN KEY (id_trip) REFERENCES trips(id),
    CONSTRAINT fk_pt_schedule FOREIGN KEY (id_schedule) REFERENCES schedules(id)
);

CREATE TABLE IF NOT EXISTS transport_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `day` DATE,
    schedule VARCHAR(10),
    needs_accessibility BOOLEAN DEFAULT FALSE,
    destination VARCHAR(255),
    id_passenger BIGINT,
    id_bus BIGINT,
    CONSTRAINT fk_tr_passenger FOREIGN KEY (id_passenger) REFERENCES passengers(id),
    CONSTRAINT fk_tr_bus FOREIGN KEY (id_bus) REFERENCES buses(id)
);

CREATE TABLE IF NOT EXISTS settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_time VARCHAR(10),
    close_time VARCHAR(10),
    blocked_next_day BOOLEAN
);

CREATE TABLE IF NOT EXISTS notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS support_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    subject VARCHAR(255),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sm_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS failures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_plate VARCHAR(20),
    issue_type VARCHAR(100),
    severity VARCHAR(50),
    description TEXT,
    status VARCHAR(50) DEFAULT 'Registrado',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_passenger BIGINT,
    route_id BIGINT,
    stop_name VARCHAR(255),
    voted_date DATE,
    status VARCHAR(50) DEFAULT 'Pendente',
    CONSTRAINT fk_vote_passenger FOREIGN KEY (id_passenger) REFERENCES passengers(id)
);

-- Aliases for Stored Procedures pointing to Procedures.java class
CREATE ALIAS IF NOT EXISTS ProcCreateUser FOR "com.siti.sitiapi.h2.Procedures.procCreateUser";
CREATE ALIAS IF NOT EXISTS ProcGetUserByEmail FOR "com.siti.sitiapi.h2.Procedures.procGetUserByEmail";
CREATE ALIAS IF NOT EXISTS ProcExistUserByEmail FOR "com.siti.sitiapi.h2.Procedures.procExistUserByEmail";
CREATE ALIAS IF NOT EXISTS ProcGetUserByEmailAndPassword FOR "com.siti.sitiapi.h2.Procedures.procGetUserByEmailAndPassword";
CREATE ALIAS IF NOT EXISTS HasUserAdministratorById FOR "com.siti.sitiapi.h2.Procedures.hasUserAdministratorById";
CREATE ALIAS IF NOT EXISTS HasUserDriverById FOR "com.siti.sitiapi.h2.Procedures.hasUserDriverById";
CREATE ALIAS IF NOT EXISTS ProcCreatePassenger FOR "com.siti.sitiapi.h2.Procedures.procCreatePassenger";
CREATE ALIAS IF NOT EXISTS ProcCreateDriver FOR "com.siti.sitiapi.h2.Procedures.procCreateDriver";
