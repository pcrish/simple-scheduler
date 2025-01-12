create table scheduler.job_dependencies (
    job_id bigint not null,
    depends_on_job_id bigint not null,
    is_active boolean default true
);
