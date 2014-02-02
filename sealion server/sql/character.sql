DROP TABLE IF EXISTS `lionse`.`character`;
CREATE TABLE  `lionse`.`character` (
  `name` varchar(100) NOT NULL,
  `level` int(5) unsigned NOT NULL,
  `maxhealth` int(8) unsigned NOT NULL,
  `health` int(8) unsigned NOT NULL,
  `money` int(10) unsigned NOT NULL,
  `hat` int(5) unsigned NOT NULL,
  `clothes` int(5) unsigned NOT NULL,
  `weapon` int(5) unsigned NOT NULL,
  `creature` int(8) unsigned NOT NULL,
  `item` int(8) unsigned NOT NULL,
  `quest` int(8) unsigned NOT NULL,
  `exp` int(10) unsigned NOT NULL,
  `location` varchar(20) NOT NULL,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;