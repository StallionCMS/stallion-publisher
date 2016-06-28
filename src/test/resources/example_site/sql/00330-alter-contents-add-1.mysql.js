db.execute('''
ALTER TABLE `contents` 
    ADD COLUMN `featuredimage`  longtext  NULL;
''');