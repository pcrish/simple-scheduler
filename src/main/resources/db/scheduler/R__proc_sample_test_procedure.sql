
CREATE or replace PROCEDURE scheduler.proc_sample_test_procedure(

)
language plpgsql
as
$$
    declare rows  bigint;
begin
        RAISE NOTICE ' Start %',5;

        create temp table temp_data as
        SELECT
            *
        FROM
            generate_series(1,100000000) i;
        RAISE NOTICE ' Completed %',5;
end;
$$;

