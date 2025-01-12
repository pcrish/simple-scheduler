

select job.create_etl_job(1,'trades','loh',null,null,'05:00:00','weekly','tue,wed,thu,fri,sat',null,null,20,false,false);

select job.create_etl_job(10,'extract','imis',null,null,'05:00:00','monthly',NULL,'03',null,20,false,false);

