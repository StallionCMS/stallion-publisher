db.execute('''
ALTER TABLE `uploaded_files` 
    ADD COLUMN `height`  int  NULL,
    ADD COLUMN `width`  int  NULL,
    ADD COLUMN `thumbcloudkey`  varchar(255)  NULL,
    ADD COLUMN `thumburl`  varchar(255)  NULL,
    ADD COLUMN `thumbwidth`  int  NULL,
    ADD COLUMN `thumbheight`  int  NULL,
    ADD COLUMN `mediumwidth`  int  NULL,
    ADD COLUMN `mediumheight`  int  NULL,
    ADD COLUMN `mediumurl`  varchar(255)  NULL;
''');