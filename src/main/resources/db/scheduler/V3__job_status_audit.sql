        --drop table if exists scheduler.job_status_audit;

		create table scheduler.job_status_audit(
		job_audit_id serial primary key, job_id bigint,job_date date,order_date date,start_at timestamp,
		end_before timestamp, sla time,schedule_type varchar(20),executable_name varchar(100),
		parameters text, retry int,started timestamp, completed timestamp,status varchar(10),
		status_description varchar(100), std_out text,error text,audit_time timestamp default clock_timestamp(),
		audit_user varchar(50),
        db_pid bigint,
        db_start_time timestamp
		);

        create or replace function trigger_job_status_audit() returns trigger as
        $body$
        begin
            insert into scheduler.job_status_audit ( job_id, job_date, order_date, start_at, end_before, sla,
                                                     executable_name, parameters, retry, started, completed, status, status_description,
                                                     std_out, error,  audit_user, db_pid, db_start_time)
            select  new.job_id, new.job_date, new.order_date, new.start_at, new.end_before, new.sla,
                    new.executable_name, new.parameters, new.retry, new.started, new.completed, new.status, new.status_description,
                    new.std_out, new.error,  new.audit_user, new.db_pid, new.db_start_time;


                     return null;
        end;
        $body$
        language plpgsql;


        create trigger trig_job_status_audit
            after insert or update or delete on scheduler.job_status
            for each row execute function trigger_job_status_audit();