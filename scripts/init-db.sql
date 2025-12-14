-- Initialize PostgreSQL database for Hyperativa Tech Test
-- This script runs automatically when the container starts

-- Create database if it doesn't exist
CREATE DATABASE hyperativa;

-- Connect to the database
\c hyperativa;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hyperativa TO postgres;

