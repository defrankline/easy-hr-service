create table payroll_misc_earnings
(
    id         bigserial primary key,
    staff_id   bigint         not null,
    payroll_id bigint         not null,
    item_id    bigint         not null,
    amount     numeric(38, 2) not null
);

alter table payroll_misc_earnings
    add constraint payroll_misc_earnings_staff_id_fk
        foreign key (staff_id) references staff
            on update cascade on delete cascade;

alter table payroll_misc_earnings
    add constraint payroll_misc_earnings_payroll_id_fk
        foreign key (payroll_id) references payroll
            on update cascade on delete cascade;

alter table payroll_misc_earnings
    add constraint payroll_misc_earnings_mis_earning_item_id_fk
        foreign key (item_id) references misc_earning_items
            on update cascade on delete cascade;

alter table payroll_misc_earnings
    owner to postgres;