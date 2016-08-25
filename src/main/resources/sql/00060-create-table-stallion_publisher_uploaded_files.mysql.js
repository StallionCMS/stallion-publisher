db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_publisher_uploaded_files` (
`id` bigint(20) unsigned NOT NULL,
    `name`  varchar(100)  NULL ,
    `type`  varchar(30)  NULL ,
    `extension`  varchar(15)  NULL ,
    `height`  int  NULL ,
    `width`  int  NULL ,
    `secret`  varchar(255)  NULL ,
    `ownerid`  bigint(20)  NULL ,
    `rawurl`  varchar(255)  NULL ,
    `cloudkey`  varchar(255)  NULL ,
    `uploadedat`  datetime  NULL ,
    `thumbcloudkey`  varchar(255)  NULL ,
    `thumbrawurl`  varchar(255)  NULL ,
    `thumbwidth`  int  NULL ,
    `thumbheight`  int  NULL ,
    `mediumcloudkey`  varchar(255)  NULL ,
    `mediumwidth`  int  NULL ,
    `mediumheight`  int  NULL ,
    `mediumrawurl`  varchar(255)  NULL ,
    `smallcloudkey`  varchar(255)  NULL ,
    `smallrawurl`  varchar(255)  NULL ,
    `smallwidth`  int  NULL ,
    `smallheight`  int  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');