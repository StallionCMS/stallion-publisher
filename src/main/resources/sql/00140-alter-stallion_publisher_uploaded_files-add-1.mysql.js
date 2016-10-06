db.execute('''
ALTER TABLE `stallion_publisher_uploaded_files` 
    ADD COLUMN `extra`  longtext  NULL;
''');