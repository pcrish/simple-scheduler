
        create schema if not exists scheduler;
		create table scheduler.jobs(
		    job_id bigint primary key,
            name varchar(50) not null,
            description varchar(200) not null,
		    tree text not null,
            start_at time,
			end_before time, --todo do we need it
            sla time not null,
            schedule_type varchar(20) not null,
            job_days varchar(100),
			nth_day   varchar(4),
            nth_working_day varchar(4),
            is_active boolean not null,
            executable_type varchar(50) not null,
            executable_name text,
            parameters text,
			retry int not null,
            odate_start_at boolean not null,
            odate_sla boolean not null
		);