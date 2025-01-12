CREATE or replace PROCEDURE proc_file_delivery_notification(n_filename text)
    language plpgsql
as
$$
begin

    insert into scheduler.source_file_notification (file_code, file_name)
       select file_code, n_filename from scheduler.source_files
         where n_filename like  '%'||replace(file_name,'?'
             ,'_');
end;
$$;