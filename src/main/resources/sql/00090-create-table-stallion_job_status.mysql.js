db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_job_status` (
`id` bigint(20) unsigned NOT NULL,
    `name`  varchar(255)  NULL ,
    `startedat`  bigint(20)  NULL ,
    `completedat`  bigint(20)  NULL ,
    `failedat`  bigint(20)  NULL ,
    `shouldsucceedby`  bigint(20)  NULL ,
    `lastdurationseconds`  bigint(20)  NULL ,
    `error`  varchar(255)  NULL ,
    `failcount`  int  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `name_key` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');