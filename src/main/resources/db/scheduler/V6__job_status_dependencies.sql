create table scheduler.job_status_dependencies (
    job_id bigint not null,
    depends_on_job_id bigint not null,
    order_date date
);
