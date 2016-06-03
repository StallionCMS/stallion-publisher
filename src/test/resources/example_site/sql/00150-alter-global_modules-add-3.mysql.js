db.execute('''
ALTER TABLE `global_modules` 
    ADD COLUMN `type`  varchar(255)  NULL,
    ADD COLUMN `description`  varchar(255)  NULL,
    ADD COLUMN `previewtemplate`  varchar(255)  NULL;
''');