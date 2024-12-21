ALTER TABLE brands
    ADD CONSTRAINT brand_name_not_empty
        CHECK (name IS NOT NULL AND LENGTH(TRIM(name)) > 0);

ALTER TABLE engines
    ADD CONSTRAINT engine_name_not_empty
        CHECK (LENGTH(TRIM(name)) > 0);

ALTER TABLE engines
    ALTER column capacity SET NOT NULL;


ALTER TABLE models
    ADD CONSTRAINT model_name_not_empty
        CHECK (name IS NOT NULL AND LENGTH(TRIM(name)) > 0);

ALTER TABLE models
    ALTER column  start_manufacturing SET NOT NULL;

ALTER TABLE models
    ALTER column  end_manufacturing SET NOT NULL;

ALTER TABLE models
    ADD CONSTRAINT brand_name_not_empty
        CHECK (generation IS NOT NULL AND LENGTH(TRIM(generation)) > 0);


ALTER TABLE cars
    ADD CONSTRAINT car_color_not_empty
        CHECK (color IS NOT NULL AND LENGTH(TRIM(color)) > 0);

ALTER TABLE cars
    ALTER column manufacturing_date SET NOT NULL;

ALTER TABLE cars
    ALTER column drive SET NOT NULL;

ALTER TABLE categories
    ADD CONSTRAINT category_name_not_empty
        CHECK (LENGTH(TRIM(name)) > 0);


