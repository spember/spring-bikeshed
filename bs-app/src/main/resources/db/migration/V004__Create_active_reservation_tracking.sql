create table current_open_reservations(
    reservation_id varchar(32) not null primary key,
    revision int not null default 0,
    customer_id varchar(32) not null,
    start_time timestamp not null,
    end_time timestamp
);

create index if not exists cor_customer on current_open_reservations(customer_id);

create table reservation_bikes(
    reservation_id varchar(32) not null,
    bike_id varchar(32) not null,
    status varchar(32) not null,
    constraint fk_res_id foreign key(reservation_id) references current_open_reservations(reservation_id),
    primary key(reservation_id, bike_id, status)
);

create table inactive_reservations(
      reservation_id varchar(32) not null primary key,
      revision int not null default 0,
      customer_id varchar(32) not null,
      start_time timestamp not null,
      end_time timestamp not null
);

create index if not exists ir_customer on inactive_reservations(customer_id);