db.execute('''
ALTER TABLE `global_module_versions` 
    ADD COLUMN `type`  varchar(255)  NULL,
    ADD COLUMN `description`  varchar(255)  NULL,
    ADD COLUMN `previewtemplate`  varchar(255)  NULL;
''');