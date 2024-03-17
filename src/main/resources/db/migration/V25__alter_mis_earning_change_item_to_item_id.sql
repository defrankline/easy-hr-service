alter table misc_earnings
    rename column item to item_id;

alter table misc_earnings
    alter column item_id type bigint using item_id::bigint;

