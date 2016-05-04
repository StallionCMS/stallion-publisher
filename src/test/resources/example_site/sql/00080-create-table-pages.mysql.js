db.execute('''
CREATE TABLE IF NOT EXISTS `pages` (
`id` bigint(20) unsigned NOT NULL,
    `content`  longtext  NULL ,
    `template`  varchar(255)  NULL ,
    `metadescription`  varchar(255)  NULL ,
    `draft`  bit(1)  NULL ,
    `author`  varchar(255)  NULL ,
    `relcanonical`  varchar(255)  NULL ,
    `metakeywords`  varchar(255)  NULL ,
    `titletag`  varchar(255)  NULL ,
    `image`  varchar(255)  NULL ,
    `ogtype`  varchar(30)  NULL ,
    `previewkey`  varchar(40)  NULL ,
    `originalcontent`  longtext  NULL ,
    `oldurls`  longtext  NULL ,
    `publishdate`  datetime  NULL ,
    `title`  varchar(255)  NULL ,
    `slug`  varchar(255)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `slug_key` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');