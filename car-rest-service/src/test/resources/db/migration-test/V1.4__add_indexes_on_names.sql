CREATE INDEX idx_models_name ON models (LOWER(name));
CREATE INDEX idx_models_generation ON models (LOWER(generation));

CREATE INDEX idx_category_name ON categories (LOWER(name));


