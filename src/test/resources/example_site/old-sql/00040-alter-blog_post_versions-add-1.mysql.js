db.execute('''
ALTER TABLE `blog_post_versions` 
    ADD COLUMN `permanentcheckpoint`  bit(1)  NULL;
''');