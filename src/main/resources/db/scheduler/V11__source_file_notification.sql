

create table scheduler.source_file_notification (
   notification_uuid uuid default uuid_in(md5(random()::text || clock_timestamp()::text)::cstring) primary key ,
   file_code varchar(50) ,
   file_name varchar(100),
    is_processed boolean default false,
   notification_date timestamp without time zone default clock_timestamp()
);



create index ix_source_file_notification_file_id on scheduler.source_file_notification(file_code);
create index ix_source_file_notification_is_available on scheduler.source_file_notification(is_processed) ;

