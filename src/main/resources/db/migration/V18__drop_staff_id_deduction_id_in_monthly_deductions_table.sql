alter table monthly_deductions
    drop column staff_id;

alter table monthly_deductions
    drop column deduction_id;


alter table monthly_deductions
    add deduction_account_id bigint not null;