db.execute('''
ALTER TABLE `blog_configs` 
    ADD COLUMN `editable`  bit(1)  NULL;
''');