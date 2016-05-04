db.execute('''
CREATE TABLE IF NOT EXISTS `contacts` (
`id` bigint(20) unsigned NOT NULL,
    `displayname`  varchar(255)  NULL ,
    `disabled`  bit(1)  NULL ,
    `givenname`  varchar(255)  NULL ,
    `familyname`  varchar(255)  NULL ,
    `honorific`  varchar(255)  NULL ,
    `optedout`  bit(1)  NULL ,
    `totallyoptedout`  bit(1)  NULL ,
    `website`  varchar(255)  NULL ,
    `evercookie`  varchar(255)  NULL ,
    `secrettoken`  varchar(255)  NULL ,
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
  UNIQUE KEY `evercookie_key` (`evercookie`),
  UNIQUE KEY `secrettoken_key` (`secrettoken`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');