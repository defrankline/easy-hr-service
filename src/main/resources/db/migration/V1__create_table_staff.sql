create table staff
(
    id         bigserial primary key,
    company_id bigint         not null,
    salary     numeric(38, 2) not null,
    title      varchar(255)   not null,
    number     varchar(255)   not null,
    user_id    bigint         not null,
    constraint ukppts660aq1tls2n0fwtqdclh5
        unique (user_id, company_id),
    constraint ukppts660aq1tls2n0fwtqdclh8
        unique (number, company_id)
);

alter table staff
    owner to postgres;

