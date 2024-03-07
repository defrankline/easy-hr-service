alter table deduction_accounts
    add unique (deduction_id, company_id);