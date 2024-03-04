alter table monthly_deductions
    drop column fixed_amount cascade;

alter table monthly_deductions
    drop column percentage cascade;

alter table deduction_accounts
    add fixed_amount numeric(32, 2) not null;

alter table deduction_accounts
    add percentage numeric(3, 2) not null;



