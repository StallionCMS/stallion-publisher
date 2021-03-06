db.execute('''
CREATE TABLE IF NOT EXISTS `stallion_publisher_comments` (
`id` bigint(20) unsigned NOT NULL,
    `state`  varchar(30)  NULL ,
    `threadid`  bigint(20)  NULL ,
    `notificationsregisteredat`  bigint(20)  NULL ,
    `approved`  bit(1)  NULL ,
    `contactid`  bigint(20)  NULL ,
    `authoremail`  varchar(150)  NULL ,
    `bodyhtml`  longtext  NULL ,
    `authordisplayname`  varchar(200)  NULL ,
    `bodymarkdown`  longtext  NULL ,
    `parenttitle`  varchar(255)  NULL ,
    `authorfirstname`  varchar(100)  NULL ,
    `authorlastname`  varchar(100)  NULL ,
    `authorwebsite`  varchar(255)  NULL ,
    `akismetapproved`  bit(1)  NULL ,
    `moderatorapproved`  bit(1)  NULL ,
    `akismetcheckedat`  bigint(20)  NULL ,
    `moderatedat`  bigint(20)  NULL ,
    `parentid`  bigint(20)  NULL ,
    `editingtoken`  varchar(65)  NULL ,
    `parentpermalink`  varchar(255)  NULL ,
    `captcharesponse`  varchar(75)  NULL ,
    `createdticks`  bigint(20)  NULL ,
    `authorsecret`  varchar(75)  NULL ,
    `approvedat`  bigint(20)  NULL ,
    `previouslyapproved`  bit(1)  NULL ,
    `threadsubscribe`  bit(1)  NULL ,
    `mentionsubscribe`  bit(1)  NULL ,
    `deleted`  bit(1)  NULL ,
  `row_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `row_updated_at_key` (`row_updated_at`),
  KEY `threadid_key` (`threadid`),
  KEY `createdticks_key` (`createdticks`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 
''');