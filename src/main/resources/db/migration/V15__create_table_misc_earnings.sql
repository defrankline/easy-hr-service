create table misc_earnings
(
    id        bigserial primary key,
    staff_id  bigint         not null,
    item      varchar(255)   not null,
    amount    numeric(38, 2) not null,
    recurrent boolean default true
);

alter table misc_earnings
    owner to postgres;

