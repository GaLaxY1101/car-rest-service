CREATE TABLE users(
    id BIGINT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT CONSTRAINT uq_email UNIQUE,
    password TEXT NOT NULL,
    role TEXT constraint users_role_check
        check ((role = 'ADMIN') OR (role = 'USER'))
);

CREATE SEQUENCE user_id_sequence INCREMENT BY 10;