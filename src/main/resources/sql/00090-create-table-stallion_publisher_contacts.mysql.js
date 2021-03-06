db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_publisher_contacts` (
`id` bigint(20) unsigned NOT NULL,
    `displayname`  varchar(200)  NULL ,
    `email`  varchar(150)  NULL ,
    `honorific`  varchar(50)  NULL ,
    `optedout`  bit(1)  NULL ,
    `totallyoptedout`  bit(1)  NULL ,
    `givenname`  varchar(100)  NULL ,
    `familyname`  varchar(100)  NULL ,
    `disabled`  bit(1)  NULL ,
    `extra`  longtext  NULL ,
    `secrettoken`  varchar(60)  NULL ,
    `website`  varchar(255)  NULL ,
    `evercookie`  varchar(60)  NULL ,
    `optoutdate`  bigint(20)  NULL ,
    `verifiedoptin`  bit(1)  NULL ,
    `optinat`  bigint(20)  NULL ,
    `verifysentat`  bigint(20)  NULL ,
    `verifyrejectedat`  bigint(20)  NULL ,
    `verifiedemail`  bit(1)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  UNIQUE KEY `email_key` (`email`),
  UNIQUE KEY `secrettoken_key` (`secrettoken`),
  UNIQUE KEY `evercookie_key` (`evercookie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');