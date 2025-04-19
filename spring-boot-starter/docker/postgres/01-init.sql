DO $$
BEGIN
    IF NOT EXISTS(
        SELECT
        FROM
            pg_database
        WHERE
            datname = 'db') THEN
    PERFORM
        dblink_exec('dbname=' || current_database(), 'CREATE DATABASE db');
END IF;
END
$$;

\c db
CREATE TABLE IF NOT EXISTS roles(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE
);

INSERT INTO roles(name)
    VALUES ('ROLE_ADMIN'),
('ROLE_MODERATOR'),
('ROLE_USER')
ON CONFLICT (name)
    DO NOTHING;

