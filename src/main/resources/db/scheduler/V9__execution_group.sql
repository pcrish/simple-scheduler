drop table if exists scheduler.execution_group;

create table scheduler.execution_group(
            group_id bigint primary key not null,
            name varchar(200) not null,
            is_available boolean,
            used_by bigint
		);