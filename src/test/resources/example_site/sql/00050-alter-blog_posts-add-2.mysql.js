db.execute('''
ALTER TABLE `blog_posts` 
    ADD COLUMN `type`  varchar(255)  NULL,
    ADD COLUMN `blogid`  bigint(20)  NULL;
''');