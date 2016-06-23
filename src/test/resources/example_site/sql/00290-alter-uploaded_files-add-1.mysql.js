db.execute('''
ALTER TABLE `uploaded_files` 
    ADD COLUMN `mediumcloudkey`  varchar(255)  NULL;
''');