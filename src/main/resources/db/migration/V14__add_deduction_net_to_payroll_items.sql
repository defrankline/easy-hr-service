alter table payroll_items
    add net numeric(38, 2) not null;

alter table payroll_items
    add deduction numeric(38, 2) not null;

