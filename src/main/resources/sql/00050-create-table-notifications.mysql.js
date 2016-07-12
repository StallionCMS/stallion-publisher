db.execute('''
CREATE TABLE IF NOT EXISTS `notifications` (
`id` bigint(20) unsigned NOT NULL,
    `key`  varchar(60)  NULL ,
    `createdat`  bigint(20)  NULL ,
    `contactid`  bigint(20)  NULL ,
    `frequency`  varchar(30)  NULL ,
    `seen`  bit(1)  NULL ,
    `callbackclassname`  varchar(255)  NULL ,
    `callbackplugin`  varchar(75)  NULL ,
    `extradata`  longtext  NULL ,
    `sendat`  bigint(20)  NULL ,
    `periodkey`  varchar(50)  NULL ,
    `subscriptionid`  bigint(20)  NULL ,
    `sent`  bit(1)  NULL ,
    `sentat`  bigint(20)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `key_key` (`key`),
  KEY `periodkey_key` (`periodkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');