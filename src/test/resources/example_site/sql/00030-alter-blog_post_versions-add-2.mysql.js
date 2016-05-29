db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `versionauthorid`  bigint(20)  NULL,
    ADD COLUMN `versionauthorname`  varchar(255)  NULL;
''');