alter table misc_earnings
    add constraint misc_earnings_staff_id_fk
        foreign key (staff_id) references staff
            on update cascade on delete cascade;

alter table monthly_deductions
    add constraint monthly_deductions_deductions_id_fk
        foreign key (deduction_id) references deductions
            on update cascade on delete cascade;

alter table monthly_deductions
    add constraint monthly_deductions_staff_id_fk
        foreign key (staff_id) references staff
            on update cascade on delete cascade;

alter table payroll_deductions
    add constraint payroll_deductions_staff_id_fk
        foreign key (staff_id) references staff
            on update cascade on delete cascade;

alter table payroll_deductions
    add constraint payroll_deductions_deduction_id_fk
        foreign key (deduction_id) references deductions
            on update cascade on delete cascade;

alter table payroll_deductions
    add constraint payroll_deductions_payroll_id_fk
        foreign key (payroll_id) references payroll
            on update cascade on delete cascade;

alter table payroll_items
    add constraint payroll_items_staff_id_fk
        foreign key (staff_id) references staff
            on update cascade on delete cascade;

alter table payroll_items
    add constraint payroll_items_payroll_id_fk
        foreign key (payroll_id) references payroll
            on update cascade on delete cascade;