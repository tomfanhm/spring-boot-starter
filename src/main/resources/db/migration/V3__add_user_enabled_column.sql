ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- Drop the low-cardinality role index (wastes write overhead on a 2-value column)
DROP INDEX IF EXISTS idx_users_role;
