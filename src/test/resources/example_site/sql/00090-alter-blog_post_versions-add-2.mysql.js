db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `headhtml`  longtext  NULL,
    ADD COLUMN `footerhtml`  longtext  NULL;
''');