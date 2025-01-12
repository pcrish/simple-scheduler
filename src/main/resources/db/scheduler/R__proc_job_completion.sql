CREATE or replace PROCEDURE scheduler.proc_job_completion(
    p_job_id bigint,
    p_order_date date,
    p_job_status text,
    p_error text default null
)
language plpgsql
as
$$
    declare rows  bigint;
    declare error_message text = '';
begin

    update scheduler.execution_group eg
        set is_available = true,
           used_by = null
    from scheduler.execution_group_members egm
    where eg.group_id = egm.group_id
    and egm.job_id = p_job_id;

    update scheduler.job_status
    set status = p_job_status,
        status_description = case when p_job_status = 'Success' then 'Job successful' else 'Job failed with error' end,
        error = p_error,
        completed = clock_timestamp(),
        audit_user = case when p_job_status = 'Success' then 'job_complete' else 'job_failed' end
    where job_id = p_job_id
      and order_date = p_order_date;

    if rows = 0 then
                error_message = format('Error updating job_id %s for order_date %s status/n',p_job_id,p_order_date)
                    || error_message;

                raise exception '%', error_message;
    end if;

end;
$$;