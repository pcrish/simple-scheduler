create or replace procedure scheduler.proc_process_listener()
    language plpgsql
as
$$
declare
begin


    -- find failed job and rerun if retry time is met
    update scheduler.job_status js
    set status = 'Waiting',
        status_description = 'Job reran after failure',
        started = null,
        completed = null,
        error = null,
        retry = js.retry - 1,
        audit_user = 'listener_failed_to_waiting'
    where js.status = 'Failed'
      and js.completed + interval '15 minutes' < clock_timestamp()
      and js.retry > 0;

        drop table if exists temp_new_statuses;

        -- Get all jobs that are not in success or failed status
        create temp table temp_new_statuses as
        select js.job_id, js.order_date, case when
                                                  not exists (
                                                      select 1
                                                      from scheduler.job_status djs
                                                      where jsd.depends_on_job_id = djs.job_id
                                                        and jsd.order_date >= djs.order_date
                                                        and djs.status <> 'Success'
                                                  )and exists (select 1
                                                               from scheduler.job_status djs
                                                               where jsd.depends_on_job_id = djs.job_id
                                                                 and jsd.order_date >= djs.order_date)
                                                    and eg.is_available is null
                                                  then 'Ready'

                                              when clock_timestamp() > js.sla then 'Late'
                                              else 'Waiting'
            end as status,case when eg.is_available is not null
                then 'Execution group used by '|| eg.used_by
            end as status_description,egm.group_id,egm.priority
            from scheduler.job_status js
            inner join scheduler.job_status_dependencies jsd
                on js.job_id = jsd.job_id
            and js.order_date = jsd.order_date
            left join scheduler.execution_group_members egm
                on egm.job_id = js.job_id
            left join scheduler.execution_group eg
                on eg.group_id = egm.group_id
                and not eg.is_available
            where js.status not in ('Success', 'Failed', 'Running');

            if exists(select 1 from temp_new_statuses where group_id is not null) then
                delete from temp_new_statuses t
                 where exists (select job_id, order_date
                               from (
                                        select row_number() over (partition by group_id order by priority, job_id) as row_number, job_id, order_date
                                            from temp_new_statuses
                                            where group_id is not null and status = 'Ready'
                                    ) as tns
                                        where tns.job_id = t.job_id and tns.order_date = t.order_date
                                         and tns.row_number > 1
                                );

            end if;



        -- update job status to ready if all dependencies are met

        update scheduler.job_status js
        set status = tns.status,
            status_description = case when tns.status = 'Ready' then 'Ready to run'
                                        when tns.status_description is not null then tns.status_description
                                      when js.executable_type = 'FileWatcher' then 'Waiting for file '||js.parameters
                                      when js.executable_type = 'FileNotification' then 'Waiting for notification '||js.parameters
                                      else 'Waiting for dependency'
            end,
            audit_user = 'listener_status'
        from temp_new_statuses tns
        where tns.job_id = js.job_id
          and tns.order_date = js.order_date
            and js.status <> tns.status;




        -- update job status to success if file is received
        update scheduler.job_status js
        set status = 'Success',
            status_description = 'File received',
            started = clock_timestamp(),
            completed = clock_timestamp(),
            audit_user = 'listener_file_received'
            from scheduler.source_file_monitor sfm
        where js.executable_name = sfm.file_code
          and js.parameters = sfm.file_name
          and js.status <> 'Success'
          and js.executable_type = 'FileWatcher'
          and sfm.is_available = true;


    -- update job status to success if notificaton is received
    create temp table temp_notification_received as
    with cte as (
        update scheduler.job_status js
            set status = 'Success',
                status_description = 'Notification received',
                started = clock_timestamp(),
                completed = clock_timestamp(),
                audit_user = 'listener_notification_received'
            from scheduler.source_file_notification sfn
            where js.executable_name = sfn.file_code
                and js.parameters = sfn.file_name
                and js.status <> 'Success'
                and js.executable_type = 'FileNotification'
                and sfn.is_processed = false
            returning sfn.notification_uuid
               )select notification_uuid from cte;


     update scheduler.source_file_notification sfn
        set is_processed = true
    where notification_uuid in (select notification_uuid from temp_notification_received);


    -- find orphan job and mark them as failed
        update scheduler.job_status js
            set status = 'Failed',
                status_description = 'Orphan job',
                started = clock_timestamp(),
                completed = clock_timestamp(),
                audit_user = 'listener_orphan_status'
       where js.status = 'Running'
            and js.executable_type = 'pg_db'
         and not exists (SELECT 1
                         FROM pg_stat_activity
                         WHERE pid = js.db_pid
                           and backend_start = js.db_start_time);





end;
$$

