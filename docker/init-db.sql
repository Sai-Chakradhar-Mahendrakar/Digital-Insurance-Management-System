-- Initialize the insurance database
-- This script ensures the database exists and is properly configured

-- The database is already created by POSTGRES_DB environment variable
-- Just ensure we're connected to the right database
\c insurance_db;

-- Grant all privileges to postgres user (should already have them)
GRANT ALL PRIVILEGES ON DATABASE insurance_db TO postgres;

-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS public;

-- Grant usage on schema
GRANT ALL ON SCHEMA public TO postgres;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO postgres;

-- Log successful initialization
SELECT 'Database insurance_db initialized successfully' as status;