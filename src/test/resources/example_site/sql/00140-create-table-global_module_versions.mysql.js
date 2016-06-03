db.execute('''
CREATE TABLE IF NOT EXISTS `global_module_versions` (
`id` bigint(20) unsigned NOT NULL,
    `authorname`  varchar(255)  NULL ,
    `authorid`  bigint(20)  NULL ,
    `versiondate`  datetime  NULL ,
    `globalmoduleid`  bigint(20)  NULL ,
    `name`  varchar(255)  NULL ,
    `content`  longtext  NULL ,
    `rawcontent`  longtext  NULL ,
    `widgets`  longtext  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `name_key` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');