INSERT INTO brands(id, name)
VALUES (nextval('brands_seq'), 'audi');

INSERT INTO engines(id, name, capacity, type)
VALUES (nextval('engines_id_sequence'), 'engine 1', 3.0, 'DIESEL');

INSERT INTO categories(id, name)
VALUES (nextval('category_id_sequence'), 'sedan');

INSERT INTO models (id, name, start_manufacturing, end_manufacturing, generation, brand_id)
VALUES (nextval('model_id_sequence'),
        'Camry',
        '2010-01-01 00:00:00+00',
        '2020-01-01 00:00:00+00',
        'Gen 7',
        1);

INSERT INTO cars (id, drive, category_id, engine_id, manufacturing_date, model_id, color, serial_number)
VALUES (nextval('cars_seq'),
        'FRONT',
        1,
        1,
        '2015-05-20 00:00:00+00',
        1,
        'Red',
        'SN12345ABC');
