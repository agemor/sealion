DROP TABLE IF EXISTS `lionse`.`user`;
CREATE TABLE  `lionse`.`user` (
  `no` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id` varchar(20) NOT NULL,
  `password` varchar(256) NOT NULL,
  `character` varchar(100) NOT NULL,
  `email` varchar(80) NOT NULL,
  `level` varchar(5) NOT NULL DEFAULT '0',
  `banned` int(10) unsigned NOT NULL DEFAULT '0',
  `date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastlogin` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`no`,`id`,`character`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;