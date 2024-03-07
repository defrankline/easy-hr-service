alter table monthly_deductions
    add staff_id bigint not null;

alter table monthly_deductions
    add unique (staff_id, deduction_account_id);