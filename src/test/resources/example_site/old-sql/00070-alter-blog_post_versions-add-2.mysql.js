db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `diff`  varchar(255)  NULL,
    ADD COLUMN `wordcount`  int  NULL;
''');