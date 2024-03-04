create table staff_deductions
(
    id           bigserial primary key,
    staff_id     bigint         not null,
    deduction_id bigint         not null,
    amount       numeric(38, 2) not null,
    constraint ukppts660aq1tls2n0fwtqdcla5
        unique (staff_id, deduction_id)
);

alter table staff_deductions
    owner to postgres;

