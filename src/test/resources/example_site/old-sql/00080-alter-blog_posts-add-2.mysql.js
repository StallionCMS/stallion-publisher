db.execute('''
ALTER TABLE `blog_posts` 
    ADD COLUMN `headhtml`  longtext  NULL,
    ADD COLUMN `footerhtml`  longtext  NULL;
''');