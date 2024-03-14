alter table deduction_accounts
    alter column percentage type numeric(38, 2) using percentage::numeric(38, 2);