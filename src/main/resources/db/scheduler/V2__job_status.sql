        --drop table if exists scheduler.job_status;

		create table scheduler.job_status(
		    job_id bigint,
            job_date date,
            order_date date,
            start_at timestamp,
            end_before timestamp,
            sla timestamp,
            executable_type varchar(50),
            executable_name text,
		    parameters text,
			retry int,
            started timestamp with time zone,
			completed timestamp with time zone,
            status varchar(10),
            status_description varchar(100),
		    std_out text,
            error text,
            audit_time timestamp  with time zone,
            audit_user varchar(50),
            db_pid bigint,
            db_start_time timestamp with time zone,
		    primary key(job_id,order_date));
