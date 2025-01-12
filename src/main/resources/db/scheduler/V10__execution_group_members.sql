drop table if exists scheduler.execution_group_members;
		create table scheduler.execution_group_members(
            group_id bigint not null,
            job_id bigint not null,
            priority integer not null default 5,
            primary key (group_id, job_id)
		);