create or replace function scheduler.create_etl_notification_job(
    job_id int,
    type_of_data varchar(50),
    site varchar(50),
    start_at time default null,
    end_before time  default null,
    sla time default null,
    schedule_type varchar(50) default null,
    job_days varchar(50) default null,
    nth_day varchar(50) default null,
    nth_working_day varchar(50) default null,
    retry int default 20,
    odate_start_at boolean default false,
    odate_sla boolean default false,
    is_active boolean default true,
    file_name varchar(100) default '',
    file_pattern varchar(100) default null,
    file_path varchar(1000) default null,
    contact varchar(500) default null
)
    returns void
    language plpgsql
as $$
declare job_date date;
    begin

    insert into scheduler.source_files (file_code, file_name, file_path, file_pattern, contact)
        select type_of_data||'_'||site as file_code, file_name, file_path, file_pattern, contact;

    insert into scheduler.jobs(job_id, name, description, tree, start_at, end_before, sla, schedule_type, job_days, nth_day, nth_working_day, is_active, executable_type, executable_name,parameters , retry, odate_start_at, odate_sla)
    select job_id, upper('Check_'||type_of_data||'_'||site||'_file')  as name, 'Job check if raw '||type_of_data||'_'||site||' from file to load table' as description,
            type_of_data ||'_'||site as tree, start_at :: TIME, end_before :: TIME, sla :: TIME, schedule_type, job_Days, nth_day,nth_working_day,is_active,
             'FileNotification' as executable_type, type_of_data||'_'||site as executable_name,case when file_pattern is null then file_name else file_pattern end as parameter,
             retry, odate_start_at, odate_sla
            union all
    select job_id + 1 as job_id, upper('Raw_'||type_of_data||'_'||site||'_load')  as name, 'Job loades raw '||type_of_data||' from file to load table' as description,
            type_of_data ||'_'||site as tree, start_at :: TIME, end_before :: TIME, sla :: TIME, schedule_type, job_days, nth_day,nth_working_day,is_active,
             'pg_db' as executable_type, 'com.krish.etl.scheduler.jobs.RunDBJob' as executable_name,'{"schemaName": "scheduler",
            "objectName": "proc_sample_test_procedure",
            "objectType": "Procedure"
            }' as parameter, retry, odate_start_at, odate_sla
            union all
    select job_id + 2 as job_id, upper('Raw_'||type_of_data||'_'||site||'_parsing')  as name, 'Job parse '||type_of_data||' raw data from load table to parsed load table' as description,
            type_of_data ||'_'||site as tree, start_at :: TIME, end_before :: TIME, sla :: TIME, schedule_type, job_days, nth_day,nth_working_day,is_active,
             'pg_db' as executable_type, 'com.krish.etl.scheduler.jobs.RunDBJob' as executable_name,'{"schemaName": "scheduler",
            "objectName": "proc_sample_test_procedure",
            "objectType": "Procedure"
            }' as parameter, retry, odate_start_at, odate_sla
            union all
    select job_id + 3 as job_id, upper('LD_st_'||type_of_data||'_'||site)  as name, 'Job loades '||type_of_data||' from load table to stage table' as description,
            type_of_data ||'_'||site as tree, start_at :: TIME, end_before :: TIME, sla :: TIME, schedule_type, job_days, nth_day,nth_working_day,is_active,
             'pg_db' as executable_type, 'com.krish.etl.scheduler.jobs.RunDBJob' as executable_name,'{"schemaName": "scheduler",
            "objectName": "proc_sample_test_procedure",
            "objectType": "Procedure"
            }' as parameter, retry, odate_start_at, odate_sla
            union all
    select job_id + 4 as job_id, upper('st_main_'||type_of_data||'_'||site)  as name,'Job loades '||type_of_data||' from stage table to main table'  as description,
            type_of_data ||'_'||site as tree, start_at :: TIME, end_before :: TIME, sla :: TIME, schedule_type, job_days, nth_day,nth_working_day,is_active,
             'pg_db' as executable_type, 'com.krish.etl.scheduler.jobs.RunDBJob' as executable_name,'{"schemaName": "scheduler",
            "objectName": "proc_sample_test_procedure",
            "objectType": "Procedure"
            }' as parameter, retry, odate_start_at, odate_sla;


    insert into scheduler.job_dependencies(job_id, depends_on_job_id,is_active)
    select job_id,0 as depends_on_job_id, true as is_active union
    select job_id + 1  as job_id, job_id as depends_on_job_id, true as is_active union
    select job_id + 2  as job_id, job_id + 1 as depends_on_job_id, true as is_active union
    select job_id + 3  as job_id, job_id + 2 as depends_on_job_id, true as is_active union
    select job_id + 4  as job_id, job_id + 3 as depends_on_job_id, true as is_active;


end;
$$;