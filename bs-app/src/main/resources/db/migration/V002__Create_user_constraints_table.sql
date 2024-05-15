create table user_constraints(
    user_id varchar(32) not null primary key,
    revision int not null default 0,
    email varchar(256) not null,
    is_employee boolean not null,
    constraint uc_email unique(email)
);

create index if not exists uc_em on user_constraints(email);
create index if not exists uc_ie on user_constraints(is_employee);

