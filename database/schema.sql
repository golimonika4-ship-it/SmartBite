-- ==============================================================================
-- SMARTBITE - SMART CAMPUS CANTEEN MANAGEMENT SYSTEM
-- MySQL Database Setup & Schema Initialization Script
-- Compatible with MySQL 8.0, 8.4 LTS, and MariaDB 10.5+
-- ==============================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS `smartbite`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `smartbite`;

-- ==============================================================================
-- 2. Schema: Tables
-- ==============================================================================

-- Table: users (Student and Admin Accounts)
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `roll_number` VARCHAR(30) NOT NULL,
  `email` VARCHAR(120) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_roll` (`roll_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: menu_items (Food & Beverage Catalog)
CREATE TABLE IF NOT EXISTS `menu_items` (
  `id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `category_label` VARCHAR(50) DEFAULT NULL,
  `is_veg` TINYINT(1) NOT NULL DEFAULT 1,
  `price` DECIMAL(10,2) NOT NULL,
  `availability` VARCHAR(50) NOT NULL DEFAULT 'Available',
  `rating` DECIMAL(3,1) DEFAULT NULL,
  `prep_time` VARCHAR(50) DEFAULT NULL,
  `image_url` VARCHAR(500) DEFAULT NULL,
  `description` TEXT,
  PRIMARY KEY (`id`),
  KEY `idx_menu_category` (`category`),
  KEY `idx_menu_availability` (`availability`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: orders (Campus Canteen Orders)
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `pickup_token` VARCHAR(20) NOT NULL,
  `customer_identifier` VARCHAR(100) NOT NULL,
  `table_id` VARCHAR(20) DEFAULT NULL,
  `grand_total` DECIMAL(10,2) NOT NULL,
  `status` ENUM('PENDING', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_token` (`pickup_token`),
  KEY `idx_orders_customer` (`customer_identifier`),
  KEY `idx_orders_table` (`table_id`),
  KEY `idx_orders_status` (`status`),
  KEY `idx_orders_created` (`created_at`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: order_items (Items Purchased per Order)
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `menu_item_id` VARCHAR(50) NOT NULL,
  `item_name` VARCHAR(150) NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `line_total` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_items_order` (`order_id`),
  CONSTRAINT `fk_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 3. Initial Seed Data: 16 Sample Canteen Dishes
-- Note: Inserted only if the menu_items table is empty.
-- ==============================================================================

INSERT IGNORE INTO `menu_items` (`id`, `name`, `category`, `category_label`, `is_veg`, `price`, `availability`, `rating`, `prep_time`, `image_url`, `description`) VALUES
-- Breakfast
('sb-b1', 'Crispy Masala Dosa', 'breakfast', 'Breakfast', 1, 50.00, 'Available', 4.8, '8-10 mins', 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80', 'Crispy golden fermented crepe filled with savory spiced potato masala, served with freshly ground coconut chutney and hot sambar.'),
('sb-b2', 'Steamed Idli & Medu Vada Combo', 'breakfast', 'Breakfast', 1, 40.00, 'Available', 4.7, '5-7 mins', 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=600&q=80', 'Two melt-in-mouth steamed rice idlis and one crispy golden medu vada served with piping hot dal sambar and spicy tomato chutney.'),
('sb-b3', 'Fluffy Poori Bhaji (3 pcs)', 'breakfast', 'Breakfast', 1, 45.00, 'Available', 4.6, '8-10 mins', 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80', 'Three puffed golden whole-wheat pooris served with traditional spiced potato bhaji curry and tangy lemon pickle.'),
('sb-b4', 'Indori Poha with Crispy Sev', 'breakfast', 'Breakfast', 1, 30.00, 'Available', 4.9, '4-5 mins', 'https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=600&q=80', 'Steamed flattened rice tempered with mustard seeds, crunchy peanuts, and green chillies, topped with ratlami sev and fresh coriander.'),

-- Meals
('sb-m1', 'Paneer Butter Masala Thali', 'meals', 'Meals', 1, 110.00, 'Available', 4.9, '12-15 mins', 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=600&q=80', 'Tender paneer cubes in velvety tomato cashew gravy. Served with 2 butter rotis, fragrant jeera rice, yellow dal tadka, and salad.'),
('sb-m2', 'Hyderabadi Chicken Dum Biryani', 'meals', 'Meals', 0, 130.00, 'Available', 4.9, '10-12 mins', 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80', 'Aromatic long-grain basmati rice slow-cooked with tender spiced chicken pieces, fragrant saffron, and caramelized onions, served with mirchi ka salan and cool raita.'),
('sb-m3', 'Delhi Style Rajma Chawal Bowl', 'meals', 'Meals', 1, 70.00, 'Available', 4.7, '6-8 mins', 'https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80', 'Classic slow-cooked Kashmiri red kidney beans in rich spiced tomato-onion curry, served over piping hot steamed basmati rice with pickled onions.'),
('sb-m4', 'South Indian Deluxe Meal', 'meals', 'Meals', 1, 85.00, 'Available', 4.6, '8-10 mins', 'https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?auto=format&fit=crop&w=600&q=80', 'Wholesome campus lunch plate featuring steamed rice, sambar, rasam, kootu vegetable, crispy papad, curd, and sweet payasam.'),

-- Snacks
('sb-s1', 'Crispy Punjabi Samosa (2 pcs)', 'snacks', 'Snacks', 1, 25.00, 'Available', 4.8, '3-4 mins', 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80', 'Two crunchy triangular pastry pockets packed with savory potatoes, green peas, and whole spices. Served with tangy tamarind and spicy mint chutneys.'),
('sb-s2', 'Mumbai Special Pav Bhaji', 'snacks', 'Snacks', 1, 60.00, 'Available', 4.8, '7-9 mins', 'https://images.unsplash.com/photo-1606491956689-2ea866880c84?auto=format&fit=crop&w=600&q=80', 'Mashed spiced mixed vegetable bhaji topped with a generous dollop of Amul butter, served with 2 golden butter-toasted ladi pavs, chopped red onions, and lemon.'),
('sb-s3', 'Crisp Veg Cutlet with Mint Dip', 'snacks', 'Snacks', 1, 35.00, 'Available', 4.5, '5-6 mins', 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=600&q=80', 'Two golden breadcrumb-crusted vegetable patties made with beetroot, potatoes, carrots, and sweet corn. Served with refreshing pudina chutney.'),
('sb-s4', 'Irani Bun Maska with Chai Dip', 'snacks', 'Snacks', 1, 30.00, 'Available', 4.7, '3-4 mins', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80', 'Warm, pillowy soft bakery bun generously layered with whipped salted butter and sweet tutti frutti, paired for dipping into hot chai.'),

-- Beverages
('sb-v1', 'Masala Cutting Chai', 'beverages', 'Beverages', 1, 15.00, 'Available', 4.9, '2-3 mins', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=600&q=80', 'Authentic brewed Assam black tea simmered with fresh milk, crushed green cardamom, spicy ginger, and aromatic clove.'),
('sb-v2', 'Traditional Degree Filter Coffee', 'beverages', 'Beverages', 1, 20.00, 'Available', 4.9, '3-4 mins', 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=600&q=80', 'Strong freshly brewed South Indian chicory decoction frothed with boiling whole milk, served piping hot in traditional dabarah style.'),
('sb-v3', 'Thick Mango Lassi', 'beverages', 'Beverages', 1, 40.00, 'Available', 4.8, '3-4 mins', 'https://images.unsplash.com/photo-1546173159-315724a31696?auto=format&fit=crop&w=600&q=80', 'Sweet, creamy yogurt smoothie blended with ripe Alphonso mango pulp and a pinch of fragrant cardamom, topped with slivered pistachios.'),
('sb-v4', 'Thick Chocolate Cold Coffee', 'beverages', 'Beverages', 1, 45.00, 'Available', 4.7, '4-5 mins', 'https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=600&q=80', 'Chilled espresso blended with velvety ice-cold milk, Hershey cocoa syrup, and vanilla ice cream, topped with rich chocolate drizzle.');
