# This file contains a complete database schema for all the 
# tables used by this module, written in SQL
# It may also contain INSERT statements for particular data 
# that may be used, especially new entries in the table log_display

CREATE TABLE `mdl_problemstatement` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `course` int(10) unsigned NOT NULL default '0',
  `maxbytes` int(10) unsigned NOT NULL default '100000',
  `timedue` int(10) unsigned NOT NULL default '0',
  `timeavailable` int(10) unsigned NOT NULL default '0',
  `grade` int(10) NOT NULL default '0',
  `timemodified` int(10) unsigned NOT NULL default '0',
  `preventlate` int(1) NOT NULL default '0',
  `allowresubmit` int(1) NOT NULL default '1',
  `problem_id` int(10) default NULL,
  `name` char(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_data_type` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `data_type_name_en` varchar(30) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_input_generator` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `source` text NOT NULL,
  `language_id` int(10) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_parse_format` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `comment` varchar(255) default NULL,
  `data_type_id` int(10) unsigned NOT NULL,
  `read_function` varchar(30) default NULL,
  `write_function` varchar(30) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_problem` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `description` text NOT NULL,
  `restrictions` text,
  `samples` text,
  `name` char(255) NOT NULL,
  `testsdir` char(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_problem_input_generator` (
  `problem_id` int(10) unsigned NOT NULL,
  `input_generator_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`problem_id`,`input_generator_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_problem_input_output_params` (
  `problem_id` int(10) unsigned NOT NULL,
  `n` int(10) unsigned NOT NULL,
  `format_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`problem_id`,`n`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_problem_solution` (
  `problem_id` int(10) unsigned NOT NULL,
  `solution_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`problem_id`,`solution_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_programming_language` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `language_name` varchar(30) DEFAULT NULL,
  `geshi` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

INSERT INTO `mdl_problemstatement_programming_language` VALUES (0,'C/C++','cpp'),(1,'Pascal/Delphi (FPC)','delphi'),(2,'Java','java'),(3,'Python','python'),(4,'C# (Mono)','csharp');

CREATE TABLE `mdl_problemstatement_solution` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `source` text NOT NULL,
  `language_id` int(10) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_submissions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `problemstatement` int(10) unsigned NOT NULL default '0',
  `userid` int(10) unsigned NOT NULL default '0',
  `timecreated` int(10) unsigned NOT NULL default '0',
  `timemodified` int(10) unsigned NOT NULL default '0',
  `programtext` text,
  `grade` int(10) NOT NULL default '0',
  `submissioncomment` text,
  `langid` int(4) unsigned NOT NULL default '0',
  `processed` int(2) NOT NULL default '0',
  `succeeded` int(3) NOT NULL default '0',
  `errormessage` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=219 DEFAULT CHARSET=utf8;

insert mdl_log_display (module, action, mtable, field) VALUES ('problemstatement', 'view', 'problemstatement', 'name');
insert mdl_log_display (module, action, mtable, field) VALUES ('problemstatement', 'add', 'problemstatement', 'name');
insert mdl_log_display (module, action, mtable, field) VALUES ('problemstatement', 'update', 'problemstatement', 'name');
insert mdl_log_display (module, action, mtable, field) VALUES ('problemstatement', 'view submission', 'problemstatement', 'name');
insert mdl_log_display (module, action, mtable, field) VALUES ('problemstatement', 'upload', 'problemstatement', 'name');

#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','6','mod/problemstatement:view','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','5','mod/problemstatement:view','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','4','mod/problemstatement:view','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','3','mod/problemstatement:view','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','1','mod/problemstatement:view','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','5','mod/problemstatement:submit','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','4','mod/problemstatement:grade','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','3','mod/problemstatement:grade','1',now(),0);
#insert mdl_role_capabilities(contextid,roleid,capability,permission,timemodified,modifierid) values('1','1','mod/problemstatement:grade','1',now(),0);
