db.execute('''
ALTER TABLE `blog_configs` 
    ADD COLUMN `listingtemplate`  varchar(255)  NULL,
    ADD COLUMN `posttemplate`  varchar(255)  NULL;
''');