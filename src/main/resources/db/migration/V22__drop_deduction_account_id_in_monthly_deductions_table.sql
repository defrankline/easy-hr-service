alter table monthly_deductions
    drop column deduction_account_id;

alter table monthly_deductions
    add deduction_id bigint not null;

alter table monthly_deductions
    add unique (deduction_id, staff_id);