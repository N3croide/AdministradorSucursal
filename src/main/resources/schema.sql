-- CREATE USER IF NOT EXISTS 'adminAccenture'@'%' IDENTIFIED BY 'supersecurepassword';
-- ALTER USER 'adminAccenture'@'%' IDENTIFIED WITH caching_sha2_password BY 'supersecurepassword';
-- FLUSH PRIVILEGES;
--
-- ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'root';
--
-- GRANT ALL PRIVILEGES ON franchisedb.* TO 'adminAccenture'@'%';
-- FLUSH PRIVILEGES;
--
-- CREATE TABLE IF NOT EXISTS franchises (
--   id BIGINT AUTO_INCREMENT PRIMARY KEY,
--   name VARCHAR(255)
-- );
--
-- CREATE TABLE IF NOT EXISTS products (
--   id BIGINT AUTO_INCREMENT PRIMARY KEY,
--   name VARCHAR(255),
--   price DECIMAL(10,2),
--   franchise_id BIGINT, CONSTRAINT fk_franchise FOREIGN KEY (franchise_id)
--     REFERENCES franchises(id)
-- );
--
-- CREATE TABLE IF NOT EXISTS branches (
--   id BIGINT AUTO_INCREMENT PRIMARY KEY,
--   name VARCHAR(255) NOT NULL,
--   address VARCHAR(255),
--   franchise_id BIGINT NOT NULL,
--
--   CONSTRAINT fk_branch_franchise
--     FOREIGN KEY (franchise_id)
--     REFERENCES franchises(id)
-- ); 
--
-- CREATE TABLE IF NOT EXISTS branch_products (
--     branch_id BIGINT NOT NULL,
--     product_id BIGINT NOT NULL,
--     stock INT NOT NULL DEFAULT 0,
--
--     PRIMARY KEY (branch_id, product_id),
--
--     CONSTRAINT fk_bp_branch
--         FOREIGN KEY (branch_id)
--         REFERENCES branches(id),
--
--     CONSTRAINT fk_bp_product
--         FOREIGN KEY (product_id)
--         REFERENCES products(id)
-- );
--
CREATE TABLE IF NOT EXISTS franchises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(10,2),
    franchise_id BIGINT, 
    CONSTRAINT fk_franchise FOREIGN KEY (franchise_id) REFERENCES franchises(id)
);

CREATE TABLE IF NOT EXISTS branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    franchise_id BIGINT NOT NULL,
    CONSTRAINT fk_branch_franchise FOREIGN KEY (franchise_id) REFERENCES franchises(id)
); 

CREATE TABLE IF NOT EXISTS branch_products (
    branch_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    PRIMARY KEY (branch_id, product_id),
    CONSTRAINT fk_bp_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_bp_product FOREIGN KEY (product_id) REFERENCES products(id)
);
