db.execute('''
CREATE TABLE IF NOT EXISTS `subscriptions` (
`id` bigint(20) unsigned NOT NULL,
    `name`  varchar(255)  NULL ,
    `enabled`  bit(1)  NULL ,
    `createdat`  bigint(20)  NULL ,
    `contactid`  bigint(20)  NULL ,
    `optoutdate`  bigint(20)  NULL ,
    `frequency`  varchar(30)  NULL ,
    `optindate`  bigint(20)  NULL ,
    `canchangefrequency`  bit(1)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  KEY `contactid_key` (`contactid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');