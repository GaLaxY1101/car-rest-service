    create table brands
    (
        id   bigint not null
            primary key,
        name varchar(255)
            constraint uq_brand_name
                unique
    );

    alter table brands
        owner to postgres;

    create table categories
    (
        id   bigint not null
            primary key,
        name TEXT
            constraint uq_category_name
                unique
    );

    alter table categories
        owner to postgres;

    create table engines
    (
        capacity double precision,
        id       bigint not null
            primary key,
        name     TEXT
    );

    alter table engines
        owner to postgres;

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

    alter table models
        owner to postgres;

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

    alter table cars
        owner to postgres;



    alter table cars
        owner to postgres;


    create sequence brands_seq
        increment by 50;

    alter sequence brands_seq owner to postgres;

    create sequence cars_seq
        increment by 50;

    alter sequence cars_seq owner to postgres;

    create sequence category_id_sequence
        increment by 50;

    alter sequence category_id_sequence owner to postgres;

    create sequence engines_id_sequence
        increment by 50;

    alter sequence engines_id_sequence owner to postgres;

    create sequence model_id_sequence
        increment by 50;

    alter sequence model_id_sequence owner to postgres;



