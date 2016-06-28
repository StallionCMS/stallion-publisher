db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `featuredimage`  longtext  NULL;
''');