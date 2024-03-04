create table monthly_deductions
(
    id           bigserial primary key,
    staff_id     bigint         not null,
    deduction_id bigint         not null,
    fixed_amount numeric(38, 2) not null,
    percentage   numeric(3, 2)  not null,
    constraint ukppts660aq1tls2n0fwtqdclg5
        unique (staff_id, deduction_id)
);

alter table monthly_deductions
    owner to postgres;

