create table deductions
(
    id   bigserial primary key,
    name varchar(255) not null,
    code varchar(255) not null,
    constraint ukppts660aq1tls2n0fwtqdclh1
        unique (code)
);

alter table deductions
    owner to postgres;

