
create table scheduler.st_source_file_monitor (
   file_id uuid,
   file_code varchar(50) ,
   file_name varchar(100),
   file_path varchar(500),
   file_uuid uuid,
   modified_by varchar(50),
   modified_date timestamp without time zone,
   available_from timestamp without time zone,
   available_to timestamp without time zone,
   is_available boolean,
   readable  boolean,
   writeable  boolean,
   enabled boolean,
   size bigint

);
create index ix_st_source_file_monitor_file_uuid on scheduler.st_source_file_monitor(file_uuid);

create table scheduler.source_file_monitor (
   monitor_uuid uuid default uuid_in(md5(random()::text || clock_timestamp()::text)::cstring) primary key ,
   file_id uuid,
   file_code varchar(50) ,
   file_name varchar(100),
   file_path varchar(500),
   file_uuid uuid,
   modified_by varchar(50),
   modified_date timestamp without time zone,
   available_from timestamp without time zone,
   available_to timestamp without time zone,
   is_available boolean,
   readable  boolean,
   writeable  boolean,
   enabled boolean,
   size bigint,
   monitor_date timestamp without time zone default clock_timestamp()
);



create index ix_source_file_monitor_file_id on scheduler.source_file_monitor(file_id);
create index ix_source_file_monitor_is_available on scheduler.source_file_monitor(is_available) include (file_name,modified_date);
create index ix_source_file_monitor_file_uuid on scheduler.source_file_monitor(file_uuid);

