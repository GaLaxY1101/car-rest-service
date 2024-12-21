alter table engines add column
    type              smallint
        constraint engines_type_check
            check ((type >= 0) AND (type <= 2))