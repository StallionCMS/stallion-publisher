db.execute('''
ALTER TABLE `uploaded_files` 
    ADD COLUMN `secret`  varchar(255)  NULL,
    ADD COLUMN `ownerid`  bigint(20)  NULL;
''');