db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_publisher_global_module_versions` (
`id` bigint(20) unsigned NOT NULL,
    `name`  varchar(255)  NULL ,
    `authorname`  varchar(255)  NULL ,
    `versiondate`  datetime  NULL ,
    `globalmoduleid`  bigint(20)  NULL ,
    `authorid`  bigint(20)  NULL ,
    `type`  varchar(40)  NULL ,
    `content`  longtext  NULL ,
    `description`  varchar(255)  NULL ,
    `rawcontent`  longtext  NULL ,
    `widgets`  longtext  NULL ,
    `previewtemplate`  varchar(200)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');