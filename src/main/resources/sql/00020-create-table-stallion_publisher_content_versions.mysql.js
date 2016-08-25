db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_publisher_content_versions` (
`id` bigint(20) unsigned NOT NULL,
    `slug`  varchar(255)  NULL ,
    `diff`  varchar(255)  NULL ,
    `postid`  bigint(20)  NULL ,
    `versiondate`  datetime  NULL ,
    `checkpoint`  bit(1)  NULL ,
    `versionauthorid`  bigint(20)  NULL ,
    `versionauthorname`  varchar(255)  NULL ,
    `permanentcheckpoint`  bit(1)  NULL ,
    `wordcount`  int  NULL ,
    `type`  varchar(255)  NULL ,
    `elements`  longtext  NULL ,
    `updatedat`  bigint(20)  NULL ,
    `widgets`  longtext  NULL ,
    `blogid`  bigint(20)  NULL ,
    `authorid`  bigint(20)  NULL ,
    `headhtml`  longtext  NULL ,
    `footerhtml`  longtext  NULL ,
    `initialized`  bit(1)  NULL ,
    `scheduled`  bit(1)  NULL ,
    `slugtouched`  bit(1)  NULL ,
    `featuredimage`  longtext  NULL ,
    `content`  longtext  NULL ,
    `title`  varchar(255)  NULL ,
    `template`  varchar(255)  NULL ,
    `publishdate`  datetime  NULL ,
    `metadescription`  varchar(255)  NULL ,
    `oldurls`  longtext  NULL ,
    `draft`  bit(1)  NULL ,
    `author`  varchar(255)  NULL ,
    `relcanonical`  varchar(255)  NULL ,
    `metakeywords`  varchar(255)  NULL ,
    `titletag`  varchar(255)  NULL ,
    `image`  varchar(255)  NULL ,
    `ogtype`  varchar(30)  NULL ,
    `previewkey`  varchar(40)  NULL ,
    `originalcontent`  longtext  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  KEY `postid_key` (`postid`),
  KEY `checkpoint_key` (`checkpoint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');