-- MySQL-compatible initialization SQL
-- Converted from PostgreSQL init.sql to use MySQL types (AUTO_INCREMENT, ENUM, POINT)
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

CREATE DATABASE IF NOT EXISTS db_aggar;

USE db_aggar;

CREATE TABLE IF NOT EXISTS states (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(10),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cities (
  id INT AUTO_INCREMENT PRIMARY KEY,
  state_id INT NOT NULL,
  name VARCHAR(200) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY ux_cities_state_name (state_id, name),
  CONSTRAINT fk_cities_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS neighborhoods (
  id INT AUTO_INCREMENT PRIMARY KEY,
  city_id INT NOT NULL,
  name VARCHAR(200) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY ux_neighborhoods_city_name (city_id, name),
  CONSTRAINT fk_neighborhoods_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(250) NOT NULL UNIQUE,
  password VARCHAR(250) NOT NULL,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(30),
  role VARCHAR(50) DEFAULT 'owner', -- e.g., owner, agent, admin
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Properties and related tables
CREATE TABLE IF NOT EXISTS properties (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  listing_type ENUM('sale','rent') NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(14,2) NOT NULL,
  currency CHAR(3) NOT NULL DEFAULT 'USD',
  price_period ENUM('monthly','weekly','daily','yearly','one_time'), -- NULL for sale
  bedrooms SMALLINT UNSIGNED,
  bathrooms SMALLINT UNSIGNED,
  rooms SMALLINT UNSIGNED,
  floors SMALLINT UNSIGNED,
  area DECIMAL(10,2), -- e.g., square meters
  state_id INT NOT NULL,
  city_id INT NOT NULL,
  neighborhood_id INT,
  owner_id INT,
  agent_id INT,
  status VARCHAR(30) DEFAULT 'available', -- available, pending, sold, rented
  location_lat DOUBLE,
  location_lng DOUBLE,
  location POINT,
  published_at TIMESTAMP NULL DEFAULT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT(1) DEFAULT 0,
  CONSTRAINT fk_properties_state FOREIGN KEY (state_id) REFERENCES states(id),
  CONSTRAINT fk_properties_city FOREIGN KEY (city_id) REFERENCES cities(id),
  CONSTRAINT fk_properties_neighborhood FOREIGN KEY (neighborhood_id) REFERENCES neighborhoods(id),
  CONSTRAINT fk_properties_owner FOREIGN KEY (owner_id) REFERENCES users(id),
  CONSTRAINT fk_properties_agent FOREIGN KEY (agent_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS property_images (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  property_id BIGINT NOT NULL,
  url TEXT NOT NULL,
  is_primary TINYINT(1) DEFAULT 0,
  sort_order SMALLINT DEFAULT 0,
  CONSTRAINT fk_images_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS property_comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  property_id BIGINT NOT NULL,
  comment TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS amenities (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS property_amenities (
  property_id BIGINT NOT NULL,
  amenity_id INT NOT NULL,
  PRIMARY KEY (property_id, amenity_id),
  CONSTRAINT fk_prop_amen_prop FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
  CONSTRAINT fk_prop_amen_amen FOREIGN KEY (amenity_id) REFERENCES amenities(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS price_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  property_id BIGINT NOT NULL,
  old_price DECIMAL(14,2),
  new_price DECIMAL(14,2) NOT NULL,
  changed_by INT,
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_price_history_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
  CONSTRAINT fk_price_history_user FOREIGN KEY (changed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indexes for common queries
CREATE INDEX idx_properties_listing_city_price ON properties (listing_type, city_id, price);
CREATE INDEX idx_properties_city_bedrooms_price ON properties (city_id, bedrooms, price);
CREATE INDEX idx_properties_status_published ON properties (status, published_at);

-- Full-text index for title+description (InnoDB fulltext supported in MySQL 5.6+)
CREATE FULLTEXT INDEX idx_properties_fulltext ON properties (title, description);

-- Spatial index for quick radius queries (MySQL 8+ InnoDB supports spatial indexes)
-- If your MySQL version doesn't support SPATIAL indexes on InnoDB, consider adding a functional bounding-box index on lat/lng instead.
ALTER TABLE properties ADD SPATIAL INDEX idx_properties_location (location);

-- Notes:
-- 1) MySQL does not enforce CHECK constraints reliably in older versions; validate non-negative numeric fields at the application level if needed.
-- 2) Use ST_GeomFromText(CONCAT('POINT(', location_lng, ' ', location_lat, ')')) to populate the `location` POINT column if desired.
-- 3) Consider adding triggers or application logic to keep location/lat-lng in sync and to maintain audit/history rows.
