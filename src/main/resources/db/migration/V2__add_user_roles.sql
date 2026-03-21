-- Add index on role column for role-based queries
CREATE INDEX idx_users_role ON users (role);
