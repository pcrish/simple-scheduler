--drop function scheduler.func_execute_expression(expression text)
create or replace function scheduler.func_execute_expression(expression text,odate date)
    returns text
    language plpgsql
as $$
    declare result text;
begin
        execute (expression) into result using odate;
    return result;
end
$$;