db.execute('''
CREATE TABLE IF NOT EXISTS `form_submissions` (
`id` bigint(20) unsigned NOT NULL,
    `email`  varchar(150)  NULL ,
    `pagetitle`  varchar(255)  NULL ,
    `contactid`  bigint(20)  NULL ,
    `evercookie`  varchar(60)  NULL ,
    `formname`  varchar(100)  NULL ,
    `pageurl`  varchar(255)  NULL ,
    `formid`  varchar(50)  NULL ,
    `submittedat`  bigint(20)  NULL ,
    `data`  longtext  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  KEY `email_key` (`email`),
  KEY `contactid_key` (`contactid`),
  KEY `evercookie_key` (`evercookie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');