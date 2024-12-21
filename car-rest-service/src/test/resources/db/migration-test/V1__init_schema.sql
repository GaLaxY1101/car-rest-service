    create table brands
    (
        id   bigint not null
            primary key,
        name varchar(255)
            constraint uq_brand_name
                unique
    );

    create table categories
    (
        id   bigint not null
            primary key,
        name TEXT
            constraint uq_category_name
                unique
    );

    create table engines
    (
        capacity double precision,
        id       bigint not null
            primary key,
        name     TEXT
    );


    create table models
    (
        end_manufacturing   timestamp(6) with time zone,
        start_manufacturing timestamp(6) with time zone,
        brand_id            bigint
            constraint fk_model_brand_id
                references brands,
        id                  bigint not null
            primary key,
        generation          TEXT,
        name                TEXT
    );

    create table cars
    (
        drive              smallint
            constraint cars_drive_check
                check ((drive >= 0) AND (drive <= 2)),

        category_id        bigint
            constraint fk_cars_category_id
                references categories,
        engine_id          bigint
            constraint fk_cars_engine_id
                references engines,
        id                 bigint not null
            primary key,
        manufacturing_date timestamp(6) with time zone,
        model_id           bigint
            constraint fk_cars_model_id
                references models,
        color              TEXT,
        serial_number      TEXT
            constraint uq_car_serial_number
                unique
    );




    create sequence brands_seq
        increment by 50;


    create sequence cars_seq
        increment by 50;


    create sequence category_id_sequence
        increment by 50;


    create sequence engines_id_sequence
        increment by 50;


    create sequence model_id_sequence
        increment by 50;
