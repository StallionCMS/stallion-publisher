db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_async_tasks` (
`id` bigint(20) unsigned NOT NULL,
    `secret`  varchar(255)  NULL ,
    `lockedat`  bigint(20)  NULL ,
    `lockuuid`  varchar(255)  NULL ,
    `handlername`  varchar(255)  NULL ,
    `customkey`  varchar(255)  NULL ,
    `originallyscheduledfor`  bigint(20)  NULL ,
    `datajson`  varchar(255)  NULL ,
    `executeat`  bigint(20)  NULL ,
    `trycount`  int  NULL ,
    `updatedat`  bigint(20)  NULL ,
    `neverretry`  bit(1)  NULL ,
    `createdat`  bigint(20)  NULL ,
    `completedat`  bigint(20)  NULL ,
    `failedat`  bigint(20)  NULL ,
    `errormessage`  varchar(255)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `secret_key` (`secret`),
  UNIQUE KEY `customkey_key` (`customkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');