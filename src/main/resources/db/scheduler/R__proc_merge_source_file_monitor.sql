create or replace procedure scheduler.proc_merge_source_file_monitor()
    language plpgsql
as
$$
declare
begin
    update scheduler.source_file_monitor t
        set is_available = false,
            available_to = clock_timestamp()
        where not exists(select 1 from scheduler.st_source_file_monitor as s where s.file_uuid = t.file_uuid and s.file_id = t.file_id  and t.file_name = s.file_name  and s.is_available);

    insert into scheduler.source_file_monitor (file_id, file_code, file_name, file_path, file_uuid, modified_by, modified_date, available_from, available_to, is_available, readable, writeable, enabled, size)
        select file_id, file_code, file_name, file_path, file_uuid, modified_by, modified_date, available_from, available_to, is_available, readable, writeable, enabled, size
        from scheduler.st_source_file_monitor t
            where not exists (select 1 from scheduler.source_file_monitor as s where s.file_uuid = t.file_uuid and s.file_id = t.file_id and t.file_name = s.file_name and s.is_available);

end;
$$
