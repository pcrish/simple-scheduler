    --drop table if exists scheduler.job_status_history;

    create table scheduler.job_status_history(
    job_id bigint,job_date date,order_date date,start_at timestamp,end_before timestamp, sla time,
    schedule_type varchar(20),executable_name varchar(100), parameters text, retry int,
    start_time timestamp, end_time timestamp,status varchar(10),status_description varchar(100),
    std_out text,error text,audit_time timestamp,audit_user varchar(50), primary key(job_id,job_date),
    db_pid bigint,
    db_start_time timestamp
    );