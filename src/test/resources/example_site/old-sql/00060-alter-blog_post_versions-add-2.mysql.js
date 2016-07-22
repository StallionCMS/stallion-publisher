db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `type`  varchar(255)  NULL,
    ADD COLUMN `blogid`  bigint(20)  NULL;
''');