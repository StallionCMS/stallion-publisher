db.execute('''
ALTER TABLE `blog_posts` 
    ADD COLUMN `initialized`  bit(1)  NULL;
''');