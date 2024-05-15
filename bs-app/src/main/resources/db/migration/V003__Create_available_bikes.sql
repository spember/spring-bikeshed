create table available_bikes(
    bike_id varchar(32) not null primary key,
    revision int not null default 0
);