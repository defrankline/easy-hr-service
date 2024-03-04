create table payroll
(
    id         bigserial primary key,
    name       varchar(255) not null,
    code       varchar(255) not null unique,
    company_id bigint       not null,
    approved   boolean      not null default false
);

create table payroll_items
(
    id                   bigserial primary key,
    payroll_id           bigint         not null,
    staff_id             bigint         not null,
    gross                numeric(38, 2) not null,
    social_security_fund numeric(38, 2) not null,
    pay_as_you_earn      numeric(38, 2) not null,
    health_insurance     numeric(38, 2) not null,
    share                numeric(38, 2) not null,
    saving               numeric(38, 2) not null,
    deposit              numeric(38, 2) not null,
    contribution         numeric(38, 2) not null,
    loan                 numeric(38, 2) not null,
    constraint ukppts660aq1tls2n0fwtqdclz5
        unique (staff_id, payroll_id)
);

create table deduction_accounts
(
    id           bigserial primary key,
    deduction_id bigint not null,
    account_id   bigint not null,
    company_id   bigint not null,
    constraint ukppts660aq1tls2n0fwtqdcl95
        unique (deduction_id, account_id)
);

alter table payroll
    owner to postgres;
alter table payroll_items
    owner to postgres;
alter table deduction_accounts
    owner to postgres;

