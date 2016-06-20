db.execute('''
ALTER TABLE `blog_configs` 
    ADD COLUMN `postsperpage`  int  NULL;
''');