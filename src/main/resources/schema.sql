DROP TABLE IF EXISTS Production;

CREATE TABLE production (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  machine_name varchar(255) NOT NULL DEFAULT '',
  variable_name varchar(255) NOT NULL DEFAULT '',
  datetime_from datetime NOT NULL,
  datetime_to datetime NOT NULL,
  value decimal(10,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (id)
) AS SELECT * FROM CSVREAD('classpath:production.csv');

DROP TABLE IF EXISTS Runtime;

CREATE TABLE runtime (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  machine_name varchar(255) NOT NULL DEFAULT '',
  datetime datetime NOT NULL,
  isrunning tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) AS SELECT * FROM CSVREAD('classpath:runtime.csv');