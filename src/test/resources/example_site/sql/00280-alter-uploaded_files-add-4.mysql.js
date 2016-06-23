db.execute('''
ALTER TABLE `uploaded_files` 
    ADD COLUMN `smallcloudkey`  varchar(255)  NULL,
    ADD COLUMN `smallrawurl`  varchar(255)  NULL,
    ADD COLUMN `smallwidth`  int  NULL,
    ADD COLUMN `smallheight`  int  NULL;
''');