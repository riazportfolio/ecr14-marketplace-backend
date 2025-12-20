-- ECR14 Marketplace Initial Seed Data
-- This file will be executed on application startup

-- Categories
INSERT INTO categories (id, name, icon) VALUES
('cat-1', 'Food', '🍲'),
('cat-2', 'Masala', '🌶️'),
('cat-3', 'Baked Goods', '🥐'),
('cat-4', 'Beverages', '🥤'),
('cat-5', 'Snacks', '🍿'),
('cat-6', 'Pickles', '🥒'),
('cat-7', 'Sweets', '🍬'),
('cat-8', 'Handicrafts', '🎨'),
('cat-9', 'Groceries', '🛒'),
('cat-10', 'Clothing', '👕')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Super Admin User
-- Password: super123
INSERT INTO users (id, phone, password, name, role) VALUES
('user-superadmin', '9876543210', '$2a$12$ioDKsaWS7Wx3cl.fm2bBaOwVKc0h.0lbDMBWsE.gFEKgppDPUgr8C', 'Super Admin', 'SUPERADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Demo Customer User (no password required)
INSERT INTO users (id, phone, name, role) VALUES
('user-customer-1', '9876543213', 'John Doe', 'CUSTOMER')
ON DUPLICATE KEY UPDATE name=VALUES(name);
