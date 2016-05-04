db.execute('''
CREATE TABLE IF NOT EXISTS `uploaded_files` (
`id` bigint(20) unsigned NOT NULL,
    `name`  varchar(255)  NULL ,
    `type`  varchar(255)  NULL ,
    `extension`  varchar(255)  NULL ,
    `url`  varchar(255)  NULL ,
    `cloudkey`  varchar(255)  NULL ,
    `uploadedat`  datetime  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');