drop function if exists scheduler.create_daily_scheduler(date);
create or replace function scheduler.create_daily_scheduler(odate date default null)
    returns void
    language plpgsql
as $$
declare job_date date;
begin
    if odate is null then
        odate := (select max(odate) from scheduler.job_status);
            if odate is null then
                odate := current_date;
            end if;
    end if;

    job_date := odate + 1;

   if exists (select 1 from scheduler.job_status where order_date = odate) then
        RAISE EXCEPTION  'scheduler already exists for %  date ' , odate;
    end if;



    insert into scheduler.job_status(job_id,job_date,order_date,start_at,end_before,sla,executable_type,executable_name,parameters,retry,status,status_description,audit_time,audit_user)
    select job_id,odate + 1 as job_date, odate as order_date,
           case when odate_start_at then odate + coalesce(start_at, '00:00') else ( job_date) + start_at end as start_at,
           case when odate_sla then odate + end_before else ( job_date ) + end_before end as end_before,
           case when odate_sla then odate + sla else ( job_date ) + sla end as sla,
           executable_type,executable_name,
           case when executable_type in ('FileWatcher','FileNotification') and parameters like 'expr%'
                    then  scheduler.func_execute_expression(replace(replace(parameters,'expr',''),'order_date','$1'),odate) else parameters end as parameters
           ,retry,'created' status,'job created' as status_description,current_timestamp as audit_time, 'scheduler_creator' audit_user
    from scheduler.jobs
    where is_active = true
    and (
            (
                schedule_type = 'daily'
            )
            or
            (
                schedule_type = 'weekly'
                and strpos(job_days, to_char(job_date, 'dy')) > 0
            )
            or
            (
                schedule_type = 'monthly'
                and nth_day = to_char(job_date, 'dd')
            )
            or
            (
                schedule_type = 'yearly'
                and nth_day = to_char(job_date, 'mmdd')
            )
        );

        insert into scheduler.job_status_dependencies(job_id, depends_on_job_id, order_date)
        select job_id,depends_on_job_id,odate as order_date
        from scheduler.job_dependencies
        where is_active;

    end;
$$;