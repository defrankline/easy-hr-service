create table payroll_expense_accounts
(
    id            bigserial primary key,
    account_id    bigint       not null,
    projection_id bigint       null,
    item          varchar(255) not null
);

create table payroll_payment_accounts
(
    id         bigserial primary key,
    account_id bigint         not null,
    payroll_id bigint         not null,
    amount     numeric(38, 2) not null
);

create table payroll_expense_amounts
(
    id         bigserial primary key,
    item_id    bigint         not null,
    payroll_id bigint         not null,
    amount     numeric(38, 2) not null
);


alter table payroll_payment_accounts
    add constraint payroll_payment_accounts_payroll_id_fk
        foreign key (payroll_id) references payroll
            on update cascade on delete cascade;

alter table payroll_expense_amounts
    add constraint payroll_expense_amounts_item_id_fk
        foreign key (item_id) references payroll_expense_accounts
            on update cascade on delete cascade;

alter table payroll_expense_amounts
    add constraint payroll_expense_amounts_payroll_id_fk
        foreign key (payroll_id) references payroll
            on update cascade on delete cascade;

alter table payroll_payment_accounts
    owner to postgres;

alter table payroll_expense_accounts
    owner to postgres;

alter table payroll_expense_amounts
    owner to postgres;