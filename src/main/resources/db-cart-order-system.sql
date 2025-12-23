-- =====================================================
-- ECR14 Marketplace - Cart and Order System Migration
-- Created: 2025-12-21
-- Purpose: Add cart and order management functionality
-- =====================================================

-- Update users table to add apartment number
-- ALTER TABLE users ADD COLUMN apartment_number VARCHAR(20) NOT NULL DEFAULT '';
-- Note: Column already exists, skipping

-- Update brands table to add minimum notice days
-- ALTER TABLE brands ADD COLUMN min_notice_days INT NOT NULL DEFAULT 0;
-- Note: Column already exists, skipping

-- =====================================================
-- Create carts table
-- =====================================================
CREATE TABLE IF NOT EXISTS carts (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    brand_id VARCHAR(36),
    brand_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- Create cart_items table
-- =====================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id VARCHAR(36) PRIMARY KEY,
    cart_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_price DOUBLE NOT NULL,
    product_unit VARCHAR(50) NOT NULL,
    product_image VARCHAR(500),
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_product (cart_id, product_id),
    INDEX idx_cart_id (cart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- Create orders table
-- =====================================================
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_phone VARCHAR(15) NOT NULL,
    apartment_number VARCHAR(20) NOT NULL,
    delivery_date DATE NOT NULL,
    brand_id VARCHAR(36) NOT NULL,
    brand_name VARCHAR(100) NOT NULL,
    brand_phone VARCHAR(15) NOT NULL,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    whatsapp_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_brand_id (brand_id),
    INDEX idx_status (status),
    INDEX idx_delivery_date (delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- Create order_items table
-- =====================================================
CREATE TABLE IF NOT EXISTS order_items (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_price DOUBLE NOT NULL,
    product_unit VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- Update seed data for existing demo users
-- Add apartment numbers to existing customer
-- =====================================================
UPDATE users SET apartment_number = 'A-101' WHERE phone = '9876543213';
UPDATE users SET apartment_number = 'B-202' WHERE phone = '9876543210';
UPDATE users SET apartment_number = 'C-303' WHERE phone = '9876543211';
UPDATE users SET apartment_number = 'D-404' WHERE phone = '9876543212';

-- =====================================================
-- Update brands with default minimum notice days
-- =====================================================
UPDATE brands SET min_notice_days = 1 WHERE name = 'Amma''s Kitchen';
UPDATE brands SET min_notice_days = 2 WHERE name = 'Sweet Delights';
UPDATE brands SET min_notice_days = 1 WHERE name = 'Spice Corner';
