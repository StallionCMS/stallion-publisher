db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `scheduled`  bit(1)  NULL,
    ADD COLUMN `slugtouched`  bit(1)  NULL;
''');