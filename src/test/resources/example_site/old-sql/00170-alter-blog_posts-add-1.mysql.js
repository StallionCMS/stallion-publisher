db.execute('''
ALTER TABLE `blog_posts` 
    ADD COLUMN `elements`  longtext  NULL;
''');