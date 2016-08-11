db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_publisher_author_profiles` (
`id` bigint(20) unsigned NOT NULL,
    `bio`  longtext  NULL ,
    `biomarkdown`  longtext  NULL ,
    `userid`  bigint(20)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `userid_key` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');