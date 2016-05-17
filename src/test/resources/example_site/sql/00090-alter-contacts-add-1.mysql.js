db.execute('''
ALTER TABLE `contacts` 
    ADD COLUMN `email`  varchar(255)  NULL;
''');