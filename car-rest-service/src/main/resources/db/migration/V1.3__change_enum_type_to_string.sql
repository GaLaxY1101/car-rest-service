ALTER TABLE engines
    DROP CONSTRAINT engines_type_check;

ALTER TABLE engines
    ALTER COLUMN type TYPE TEXT;

ALTER TABLE engines
    ADD CONSTRAINT engines_type_enum_check
        CHECK (type IN ('PETROL', 'DIESEL', 'ELECTRIC'));



ALTER TABLE cars
    DROP CONSTRAINT cars_drive_check;

ALTER TABLE cars
    ALTER COLUMN drive TYPE TEXT;

ALTER TABLE cars
    ADD CONSTRAINT cars_drive_enum_check
        CHECK (drive IN ('ALL', 'FRONT', 'BACK'));
