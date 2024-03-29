alter table payroll_misc_earnings
    add constraint payroll_misc_earnings_pk
        unique (payroll_id, staff_id, item_id);