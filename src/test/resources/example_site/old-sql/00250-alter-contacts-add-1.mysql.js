db.execute('''
ALTER TABLE `contacts` 
    ADD COLUMN `extra`  longtext  NULL;
''');