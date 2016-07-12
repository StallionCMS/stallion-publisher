db.execute('''
CREATE TABLE IF NOT EXISTS `blog_configs` (
`id` bigint(20) unsigned NOT NULL,
    `listingtemplate`  varchar(150)  NULL ,
    `posttemplate`  varchar(150)  NULL ,
    `editable`  bit(1)  NOT NULL ,
    `postsperpage`  int  NULL ,
    `title`  varchar(200)  NULL ,
    `metadescription`  longtext  NULL ,
    `slug`  varchar(200)  NULL ,
    `internalname`  varchar(75)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `slug_key` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');