create table event_journal(
      entity_id varchar(32) not null, -- uuid is usual great
      revision int not null default 0,
      source text not null,
      event_type varchar(256) not null,
      time_occurred timestamp with time zone not null,
      time_observed timestamp with time zone not null,
      data jsonb not null,
      PRIMARY KEY (entity_id, revision)
);

create index if not exists ej_type on event_journal(event_type);
create index if not exists ej_occurred on event_journal(time_occurred);
create index if not exists ej_observed on event_journal(time_observed);
