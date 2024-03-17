create table misc_earning_items
(
    id           bigserial primary key,
    name         varchar(255)   not null,
    account_id   bigint         not null,
    fixed_amount numeric(38, 2) not null,
    percentage   numeric(38, 2) not null,
    companyId    bigint         not null
);

alter table misc_earning_items
    owner to postgres;


alter table misc_earning_items
    add unique (name, companyId);

