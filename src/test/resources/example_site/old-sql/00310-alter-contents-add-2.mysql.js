db.execute('''
ALTER TABLE `contents` 
    ADD COLUMN `scheduled`  bit(1)  NULL,
    ADD COLUMN `slugtouched`  bit(1)  NULL;
''');