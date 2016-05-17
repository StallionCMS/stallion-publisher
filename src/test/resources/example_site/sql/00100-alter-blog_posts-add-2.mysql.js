db.execute('''
ALTER TABLE `blog_posts` 
    ADD COLUMN `updatedat`  bigint(20)  NULL,
    ADD COLUMN `authorid`  bigint(20)  NULL;
''');