db.execute('''
ALTER TABLE `stallion_job_status` 
    ADD COLUMN `failcount`  int  NULL;
''');