alter table deduction_accounts
    add constraint deduction_accounts_deductions_id_fk
        foreign key (deduction_id) references deductions
            on update cascade on delete cascade;