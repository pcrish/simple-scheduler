CREATE or replace PROCEDURE proc_run_pgsql(parameter text)
    language plpgsql
as
$$
    declare executable          json;
    declare schema_name         text;
    declare object_name         text;
    declare object_type         text;
    declare parameters          json;
    declare parameters_string   text;
    declare p_job_id            bigint;
    declare p_order_date        date;
    declare error_message       text;
    declare v_state             text;
    declare v_msg               text;
    declare v_detail            text;
    declare v_hint              text;
    declare v_context           text;
    declare rows                int;
begin
            executable  = parameter :: json;
            schema_name = executable  ->> 'schemaName';
            object_name = executable  ->> 'objectName';
            object_type = executable  ->> 'objectType';

            p_job_id    = (executable  ->> 'jobId')::int;
            p_order_date  = (executable ->> 'orderDate') :: date;

            parameters_string = case when parameters is null then ''
                else quote_literal(parameter) end;

            if ( coalesce(trim(schema_name),'') = '' or coalesce(trim(object_name),'') = ''
                     or  coalesce(trim(object_type),'') = '' ) then
                raise exception  invalid_parameter_value
                    using message = 'SchemaName, ObjectType and ObjectName are mandatory';
            end if;

            update scheduler.job_status
                set db_pid = pg_backend_pid(),
                    db_start_time = (SELECT backend_start FROM pg_stat_activity WHERE pid =pg_backend_pid()),
                    audit_user = 'proc_run_pgsql'
                where job_id = p_job_id and order_date = p_order_date;

            if ( object_type = 'Procedure' ) then
                execute format('call %s.%s (%s)', schema_name, object_name, parameters_string);
            elseif ( object_type = 'Function' ) then
                execute format('Select %s.%s (%s)', schema_name, object_name, parameters_string);
            else
                raise exception 'Invalid object type %', object_type;
            end if;

            call scheduler.proc_job_completion(p_job_id, p_order_date, 'Success');

    exception
    when others then
        get stacked diagnostics
            v_state   = returned_sqlstate,
            v_msg     = message_text,
            v_detail  = pg_exception_detail,
            v_hint    = pg_exception_hint,
            v_context = pg_exception_context;

        error_message = format('Error executing ' || coalesce(schema_name,'') || '.' || coalesce(object_name,'') || ': ' || coalesce(sqlstate,'') ||'
                     Error Name:%s
                     Error State:%s
                     state  : %s
                     message: %s
                     detail : %s
                     hint   : %s
                     context: %s', coalesce(SQLERRM,''), coalesce(SQLSTATE,''), coalesce(v_state,''), coalesce(v_msg,''), coalesce(v_detail,''), coalesce(v_hint,''), coalesce(v_context,'') );

        call scheduler.proc_job_completion(p_job_id, p_order_date, 'Failed', error_message);

end;
$$;