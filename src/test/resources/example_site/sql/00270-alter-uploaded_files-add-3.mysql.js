db.execute('''
ALTER TABLE `uploaded_files` 
    ADD COLUMN `rawurl`  varchar(255)  NULL,
    ADD COLUMN `thumbrawurl`  varchar(255)  NULL,
    ADD COLUMN `mediumrawurl`  varchar(255)  NULL;
''');