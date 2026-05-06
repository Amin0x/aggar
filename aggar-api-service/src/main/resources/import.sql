-- Sample data for Aggar real estate website
-- This file is automatically loaded by Hibernate if spring.jpa.defer-datasource-initialization=true

USE db_aggar;

-- Insert States
INSERT INTO states (name, code) VALUES
('California', 'CA'),
('New York', 'NY'),
('Texas', 'TX'),
('Florida', 'FL'),
('Washington', 'WA');

-- Insert Cities
INSERT INTO cities (state_id, name) VALUES
(1, 'Los Angeles'),
(1, 'San Francisco'),
(1, 'San Diego'),
(2, 'New York City'),
(2, 'Brooklyn'),
(2, 'Queens'),
(3, 'Houston'),
(3, 'Dallas'),
(3, 'Austin'),
(4, 'Miami'),
(4, 'Orlando'),
(5, 'Seattle'),
(5, 'Bellevue');

-- Insert Neighborhoods
INSERT INTO neighborhoods (city_id, name) VALUES
(1, 'Beverly Hills'),
(1, 'Hollywood'),
(1, 'Downtown LA'),
(2, 'SOMA'),
(2, 'Mission District'),
(2, 'Pacific Heights'),
(4, 'Manhattan'),
(4, 'Upper East Side'),
(4, 'SoHo'),
(7, 'Downtown Houston'),
(7, 'The Heights'),
(12, 'Capitol Hill'),
(12, 'Fremont');

-- Insert Users (passwords are plain text for demo - in production use BCrypt)
INSERT INTO users (username, password, name, email, phone, role) VALUES
('admin', 'admin123', 'Admin User', 'admin@aggar.com', '+1-555-0001', 'admin'),
('john_doe', 'password123', 'John Doe', 'john@example.com', '+1-555-0101', 'owner'),
('jane_smith', 'password123', 'Jane Smith', 'jane@example.com', '+1-555-0102', 'owner'),
('mike_wilson', 'password123', 'Mike Wilson', 'mike@example.com', '+1-555-0103', 'agent'),
('sarah_connor', 'password123', 'Sarah Connor', 'sarah@example.com', '+1-555-0104', 'owner');

-- Insert Amenities
INSERT INTO amenities (name) VALUES
('Swimming Pool'),
('Gym'),
('Parking'),
('Air Conditioning'),
('Heating'),
('Washer/Dryer'),
('Dishwasher'),
('Balcony'),
('Garden'),
('Security System'),
('Elevator'),
('Furnished');

-- Insert Properties
INSERT INTO properties (listing_type, title, description, price, currency, price_period, bedrooms, bathrooms, rooms, floors, area, state_id, city_id, neighborhood_id, owner_id, agent_id, status, location_lat, location_lng, is_deleted) VALUES
('sale', 'Modern Downtown Apartment', 'Beautiful modern apartment in the heart of downtown with stunning city views. Recently renovated with high-end finishes.', 450000.00, 'USD', NULL, 2, 2, 3, 1, 1200.00, 2, 4, 8, 2, 4, 'available', 40.7128, -74.0060, 0),
('rent', 'Spacious Family Home', 'Perfect family home with large backyard, updated kitchen, and close to schools. Quiet neighborhood.', 3500.00, 'USD', 'monthly', 4, 3, 6, 2, 2400.00, 1, 1, 1, 2, 4, 'available', 34.0522, -118.2437, 0),
('sale', 'Luxury Villa with Pool', 'Stunning luxury villa featuring a private pool, spa, and panoramic views. Gourmet kitchen with marble countertops.', 1250000.00, 'USD', NULL, 5, 4, 8, 2, 4500.00, 1, 1, 1, 5, NULL, 'available', 34.0736, -118.4004, 0),
('rent', 'Cozy Studio in SOMA', 'Compact and efficient studio apartment in vibrant SOMA district. Walking distance to tech companies and restaurants.', 2200.00, 'USD', 'monthly', 0, 1, 1, 1, 550.00, 1, 2, 4, 2, NULL, 'available', 37.7749, -122.4194, 0),
('sale', 'Waterfront Condo', 'Luxury waterfront condo with amazing ocean views. Building features gym, pool, and 24/7 security.', 750000.00, 'USD', NULL, 3, 2, 4, 1, 1800.00, 4, 10, NULL, 5, NULL, 'available', 25.7617, -80.1918, 0),
('rent', 'Modern Townhouse', 'Newly built townhouse with modern finishes. Features rooftop terrace with city views.', 4200.00, 'USD', 'monthly', 3, 3, 5, 3, 2000.00, 2, 5, 9, 2, 4, 'available', 40.6782, -73.9442, 0),
('sale', 'Charming House in Austin', 'Beautiful house in desirable Austin neighborhood. Large lot with mature trees and updated interior.', 580000.00, 'USD', NULL, 3, 2, 5, 1, 2100.00, 3, 9, NULL, 5, NULL, 'available', 30.2672, -97.7431, 0),
('rent', 'Downtown Loft', 'Industrial-chic loft in downtown arts district. High ceilings, exposed brick, and modern amenities.', 2800.00, 'USD', 'monthly', 1, 1, 2, 1, 900.00, 3, 7, 10, 2, NULL, 'available', 29.7604, -95.3698, 0);

-- Insert Property Images
INSERT INTO property_images (property_id, url, is_primary, sort_order) VALUES
(1, 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800', 1, 0),
(1, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800', 0, 1),
(1, 'https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800', 0, 2),
(2, 'https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800', 1, 0),
(2, 'https://images.unsplash.com/photo-1556020685-ae41ab02f604?w=800', 0, 1),
(3, 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800', 1, 0),
(3, 'https://images.unsplash.com/photo-1583608205776-b60c6b68b98a?w=800', 0, 1),
(3, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800', 0, 2),
(4, 'https://images.unsplash.com/photo-1522708323597-dfb57e8d3d59?w=800', 1, 0),
(4, 'https://images.unsplash.com/photo-1502672260266-1c616624a332?w=800', 0, 1),
(5, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800', 1, 0),
(5, 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800', 0, 1),
(6, 'https://images.unsplash.com/photo-1583608205776-b60c6b68b98a?w=800', 1, 0),
(6, 'https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800', 0, 1),
(7, 'https://images.unsplash.com/photo-1564013799919-bb13f65a9418?w=800', 1, 0),
(7, 'https://images.unsplash.com/photo-1556020685-ae41ab02f604?w=800', 0, 1),
(8, 'https://images.unsplash.com/photo-1502672260266-1c616624a332?w=800', 1, 0),
(8, 'https://images.unsplash.com/photo-1522708323597-dfb57e8d3d59?w=800', 0, 1);

-- Insert Property Amenities
INSERT INTO property_amenities (property_id, amenity_id) VALUES
(1, 2), (1, 3), (1, 4), (1, 6),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 9),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 6), (3, 8), (3, 9), (3, 10), (3, 11),
(4, 2), (4, 3), (4, 6), (4, 7),
(5, 1), (5, 2), (5, 3), (5, 10), (5, 11),
(6, 2), (6, 3), (6, 4), (6, 6), (6, 8),
(7, 1), (7, 3), (7, 4), (7, 5), (7, 6), (7, 9),
(8, 2), (8, 3), (8, 6), (8, 7), (8, 8);
