alter table payroll_deductions
    add unique (payroll_id, staff_id, deduction_id);

