create table scheduler.source_files (
   file_id uuid generated always as (  uuid_in(md5(file_code)::cstring) ) stored ,
   file_code varchar(50)  primary key ,
   file_name varchar(100),
   file_path varchar(500),
   file_pattern varchar(500),
   contact varchar(500),
   enabled boolean default true
);
