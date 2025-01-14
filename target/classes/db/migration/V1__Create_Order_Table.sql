CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product_details VARCHAR(255) NOT NULL,
    status ENUM('CANCELLED', 'CONFIRMED', 'DELIVERED', 'DISPATCHED', 'PENDING'),
    created_at DATETIME(6),
    updated_at DATETIME(6)
) ENGINE=InnoDB;
