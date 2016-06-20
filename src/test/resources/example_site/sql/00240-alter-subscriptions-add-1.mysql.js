db.execute('''
ALTER TABLE `subscriptions` 
    ADD COLUMN `ownerkey`  varchar(255)  NULL;
''');