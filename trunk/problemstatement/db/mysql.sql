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

INSERT INTO `mdl_problemstatement_data_type` VALUES (1,'Integer'),(2,'Real'),(3,'Character'),(4,'String'),(5,'Array of Integer'),(6,'Array of Real'),(7,'Array of Character'),(8,'Array of String'),(9,'Array of array of Integer'),(10,'Array of array of Real'),(11,'Array of array of Character'),(12,'Array of array of String');

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

INSERT INTO `mdl_problemstatement_parse_format` VALUES (1,'Integer',1,'readInteger','writeInteger'),(2,'Real',2,'readDouble','writeDouble'),(3,'Character',3,'readCharacter','writeCharacter'),(4,'String',4,'readString','writeString'),(5,'Array of Integer',5,'readArrayOfInteger','writeArrayOfInteger'),(6,'Array of Real',6,'readArrayOfDouble','writeArrayOfDouble'),(7,'Array of Character',7,'readArrayOfCharacter','writeArrayOfCharacter'),(8,'Array of String',8,'readArrayOfString','writeArrayOfString'),(9,'Array of array of Integer',9,'readArrayOfArrayOfInteger','writeArrayOfArrayOfInteger'),(10,'Array of array of Real',10,'readArrayOfArrayOfDouble','writeArrayOfArrayOfDouble'),(11,'Array of array of Character',11,'readArrayOfArrayOfCharacter','writeArrayOfArrayOfCharacter'),(12,'Array of array of String',12,'readArrayOfArrayOfString','writeArrayOfArrayOfString');

CREATE TABLE `mdl_problemstatement_problem` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `description` text NOT NULL,
  `restrictions` text,
  `samples` text,
  `name` char(255) NOT NULL,
  `testsdir` char(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

INSERT INTO `mdl_problemstatement_problem` VALUES (1,' <font size=\"3\">Изучение любого языка программирования начинаетя с написания программы &quot;hello world&quot;.<br />Вывести на стандартный вывод слова &quot;hello world&quot;.</font><br />',NULL,NULL,'Задача № 1. Привет, мир!','problem1'),(2,'<font size=\"3\"> Написать программу, которая складывает два целых числа. Числа вводятся со стандартного ввода.Числа подобраны таким образом, что их сумма находится в пределе от -2\'147\'483\'648 до 2\'147\'483\'647 включительно.</font><br />',NULL,NULL,'Задача № 2. Сумма двух чисел','problem2'),(3,' <font size=\"3\">На стандартный ввод подается число <span style=\"font-weight: bold;\">n</span> и матрица <span style=\"font-weight: bold;\">n</span>x<span style=\"font-weight: bold;\">n</span> целых чисел. Посчитать их сумму. Числа подобраны таким образом, что их сумма будет находиться в пределах от -2\'147\'483\'648 до 2\'147\'483\'647.</font><br />',NULL,NULL,'Задача № 3. Сумма элементов матрицы','problem3'),(4,' <font size=\"3\"><span style=\"font-weight: bold;\"></span>Иван думает, что 4 и 7 - счастливые цифры, а все остальные цифры - несчастливые. Счастливое число - это число, которое содержит только счастливые цифры (в десятичной системе счисления).<br />Вам дается целое число <span style=\"font-weight: bold;\">N</span>. Необходимо вернуть самое большое счастливое число, меньшее или равное <span style=\"font-weight: bold;\">N</span>.<br />Число поступает со стандартного ввода. Необходимо вывести ответ (число) на стандартный вывод.<span style=\"font-weight: bold;\"></span></font><br /> ',' <font size=\"3\"><span style=\"font-weight: bold;\">N</span> находится в пределах 4 и 1000000000 включительно.</font> ',' <font size=\"3\">0)<br />Входные данные: 100<br />Выходные данные: 77<br />1)<br />Входные данные: 75<br /> Выходные данные: 74<br /> 2)<br />Входные данные: 5<br /> Выходные данные: 4<br /> 3)<br />Входные данные: 474747<br /> Выходные данные: 474747</font><br /><font size=\"1\"></font> ','Задача № 4. Счастливые числа','problem4'),(5,' <font size=\"3\"><span style=\"font-weight: bold;\"></span>Иван думает, что 4 и 7 - счастливые цифры, а все остальные цифры - несчастливые. Счастливое число - это число, которое содержит только счастливые цифры (в десятичной системе счисления).<br /> Вам дается два целых числа <span style=\"font-weight: bold;\">a</span> и <span style=\"font-weight: bold;\">b</span>. Необходимо вернуть количество счастливых чисел между <span style=\"font-weight: bold;\">a</span> и <span style=\"font-weight: bold;\">b</span> включительно.<br /> Числа <span style=\"font-weight: bold;\">a</span> и <span style=\"font-weight: bold;\">b</span> разделены пробелом и поступают со стандартного ввода. Необходимо вывести ответ (число) на стандартный вывод.</font><font size=\"1\"></font>','<font size=\"3\"><span style=\"font-weight: bold;\">a</span> находится в пределах 1 и 1000000000 включительно;<br /><span style=\"font-weight: bold;\">b</span></font><font size=\"3\"> находится в пределах a и 1000000000 включительно.</font><br /><font size=\"3\"> <span style=\"font-weight: bold;\"></span></font> ','<font size=\"3\"><span style=\"font-weight: bold;\"></span></font><font size=\"3\"><span style=\"font-weight: bold;\"></span>0)<br /> Входные данные: 1 10<br /> Выходные данные: 2<br /> 1)<br /> Входные данные: 11 20<br /> Выходные данные: 0<br /> 2)<br /> Входные данные: 74 77<br /> Выходные данные: 2<br /> 3)<br /> Входные данные: 1000000 5000000<br /> Выходные данные: 64</font><br />','Задача № 5. Счастливые числа-2','problem5'),(6,'<span style=\"font-weight: bold;\"></span>По кругу располагаются <span style=\"font-weight: bold;\">n</span> человек. Ведущий считает по кругу, начиная с первого, и выводит (&quot;казнит&quot;) <span style=\"font-weight: bold;\">m</span>-го человека. Круг смыкается, счет возобновляется со следующего после &quot;казненного&quot;; так продолжается, пока &quot;в живых&quot; не останется только один человек. Найти номер <span style=\"font-weight: bold;\">k</span> оставшегося &quot;в живых&quot; человека.<br />Входные данные - числа <span style=\"font-weight: bold;\">n</span> и <span style=\"font-weight: bold;\">m</span> - поступают со стандартного ввода. Вывести на стандартный вывод число <span style=\"font-weight: bold;\">k</span>. Примечание: игроки нумеруются от 1.<span style=\"font-weight: bold;\"></span><span style=\"font-weight: bold;\"></span><br /> ','<span style=\"font-weight: bold;\">n </span>и<span style=\"font-weight: bold;\"> m </span>находятся в пределах от 1 до 1\'000\'000\'000 включительно.<br /><span style=\"font-weight: bold;\"></span> ','<span style=\"font-weight: bold;\"></span>0)<br />Входные данные: 1 5<br />Выходные данные: 1<br />В игру вступил 1 человек, и он - последний, кто остался &quot;в живых&quot;.<br />1)<br />Входные данные: 5 3<br /> Выходные данные: 4<br /> Последовательность вывода &quot;игроков&quot;:<br />o o o o o<br />o o x o o<br />x o x o o<br />x o x o x<br />x x x o x<br />Номер последнего оставшегося &quot;в живых&quot; - 4.<br />2)<br />Входные данные: 7 2<br /> Выходные данные: 7<br /> Последовательность вывода &quot;игроков&quot;:<br />o o o o o o o<br />o x o o o o o<br />o x o x o o o<br />o x o x o x o<br />x x o x o x o<br />x x o x x x o<br />x x x x x x o<br />Номер последнего оставшегося &quot;в живых&quot; - 7.<br />3)<br />Входные данные: 5 6<br /> Выходные данные: 4<br /> Последовательность вывода &quot;игроков&quot;:<br />o o o o o<br />x o o o o<br />x o x o o<br />x x x o o<br />x x x o x<br />Номер последнего оставшегося &quot;в живых&quot; - 4.<br /> ','Задача № 6. Задача Иосифа','problem6'),(7,'<font size=\"3\"><span style=\"font-weight: bold;\"></span>Вводятся два числа <span style=\"font-weight: bold;\">b</span> и <span style=\"font-weight: bold;\">n</span>. <span style=\"font-weight: bold;\">b</span> - основание системы счисления, в которую необходимо перевести десятичное число <span style=\"font-weight: bold;\">n</span>. Вывести строку, которая является представлением числа <span style=\"font-weight: bold;\">n</span> в системе счисления с основанием <span style=\"font-weight: bold;\">b</span>. Для представления цифр больших 10 в системе счисления <span style=\"font-weight: bold;\">b</span>&gt;=10, использовать прописные буквы латинского алфавита. Так, в системе счисления 16 число 10 - это \'A\', 11 - \'B\', 12 - \'C\', 13 - \'D\', 14 - \'E\', 15 - \'F\'.<span style=\"font-weight: bold;\"></span></font> ','<font size=\"3\"><span style=\"font-weight: bold;\">b</span> принимает целые значения из отрезка [2; 36];<br /><span style=\"font-weight: bold;\">n</span> принимает целые значения из отрезка [0; 1\'000\'000\'000].</font> ','<font size=\"3\"><span style=\"font-weight: bold;\"></span><span style=\"font-weight: bold;\"></span><span style=\"font-weight: bold;\"></span>0)<br />Входные данные: 2 256<br />Выходные данные: 100000000<br />1)<br />Входные данные: 3 45666<br />Выходные данные: 2022122100<br />2)<br />Входные данные: 16 10<br />Выходные данные: A<br />3)<br />Входные данные: 36 100<br />Выходные данные: 2S<br />S - 19я буква английского алфавита, сооветствует десятичной цифре 28, поэтому 2S<sub>36</sub> = 2 * 36<sup>1</sup> + 28 * 36<sup>0</sup> = 72 + 28 = 100</font> ','Задача № 7. Системы счисления','problem7'),(8,'<font size=\"3\"><span style=\"font-weight: bold;\"></span>Дан массив <span style=\"font-weight: bold;\">A</span> из<span style=\"font-weight: bold;\"> N</span> элементов. Нужно упорядочить этот массив по возрастанию. Со стандартного устройства ввода в первой строке вводится число <span style=\"font-weight: bold;\">N</span>. Во второй строке через пробел даны <span style=\"font-weight: bold;\">N</span> целых чисел - массив. Нужно выдать на стандартное устройство вывода в одну строку упорядоченный массив, после каждого элемента ставя пробел.</font><br /> ','<font size=\"3\"><span style=\"font-weight: bold;\"></span><span style=\"font-weight: bold;\">N</span> находится в пределах [1..50000].<br /><span style=\"font-weight: bold;\">A[i] </span>находится в пределах </font><font size=\"3\">[1..50000].</font> ','<font size=\"3\"><span style=\"font-weight: bold;\"></span>Входные данные<br />10<br />6 4 67 3 345 76 35 65 6 346 <br /><br />Выходные данные<br />3 4 6 6 35 65 67 76 345 346 </font> ','Задача №8: Сортировка слиянием','problem8'),(9,' <style type=\"text/css\"> &amp;amp;lt;!-- @page { margin: 2cm } P { margin-bottom: 0.21cm } --&amp;amp;gt; </style>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><span style=\"font-weight: bold;\">Постановка задачи</span><br /></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Найти наибольший общий делитель двух чисел.</p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Input</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Со стандартного устройства ввода вводятся 2 целых неотрицательных числа, не превосходящих 10000.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Output</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Нужно выдать на стандартное устройство вывода 1 целое число – НОД введенных чисел.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><b> </b></font><font size=\"4\"><span lang=\"en-US\"><b>Input</b></span></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">60 225</font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><span lang=\"en-US\"><b> Output</b></span></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">15</font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\"><br /></font></p> ',NULL,NULL,'Задача №9: НОД','problem9'),(10,' <style type=\"text/css\"> &amp;amp;lt;!-- @page { margin: 2cm } P { margin-bottom: 0.21cm } --&amp;amp;gt;</style><span style=\"font-weight: bold;\">Постановка задачи</span><br />\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Определить, является ли натуральное число <span lang=\"en-US\">N</span> палиндромом. Число является палиндромом в том случае, если при перебирании цифр в обратном направлении получается то же самое число.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Input</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Со стандартного устройства ввода вводится 1 натуральное число, не превосходящее 2000000000.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Output</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Нужно выдать на стандартное устройство вывода “<span lang=\"en-US\">Yes</span>” или “<span lang=\"en-US\">No</span>”, в зависимости от того, является введенное число палиндромом или нет. Кавычки выводить не следует.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><b> </b></font><font size=\"4\"><span lang=\"en-US\"><b>Input</b></span></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">923454329</font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><span lang=\"en-US\"><b> Output</b></span></font></p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">Yes<br /><br /></font></p> ',NULL,NULL,'Задача №10: Палиндром','problem10'),(11,' <style type=\"text/css\"> &amp;amp;amp;lt;!-- @page { margin: 2cm } P { margin-bottom: 0.21cm } --&amp;amp;amp;gt; </style>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><span style=\"font-weight: bold;\">Постановка задачи</span><br /></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Нужно вывести на экран нечетные чисел из отрезка от <span lang=\"en-US\">A</span> до <span lang=\"en-US\">B</span>.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Input</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Со стандартного устройства ввода вводятся 2 натуральных числа – <span lang=\"en-US\">A</span> и <span lang=\"en-US\">B</span>.<span lang=\"en-US\"></span><span lang=\"en-US\"></span></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Output</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Нужно выдать на стандартное устройство вывода все требуемые чисел в порядке возрастания через пробел. В конце строки пробел ставить не следует.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><span lang=\"en-US\"><b> Input</b></span></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">6 13</font></p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><span lang=\"en-US\"><b> Output</b></span></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\"><span lang=\"en-US\">7 9 11</span></font><font face=\"Courier New, monospace\"> 13</font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"></p>',NULL,NULL,'Задача №11: Нечетные числа из интервала','problem11'),(12,' <style type=\"text/css\"> &amp;amp;amp;lt;!-- @page { margin: 2cm } P { margin-bottom: 0.21cm } --&amp;amp;amp;gt;</style><span style=\"font-weight: bold;\"></span>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Найти сумму двух «длинных» чисел. Т.е. чисел с большим количеством знаков.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Input</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Со стандартного устройства ввода вводятся в первой строке 2 натуральных числа, не превышающих 1000 – длины первого и второго чисел соответственно. Затем во второй строке идет первое число, в третьей – второе число.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><b>Output</b></font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\">Нужно выдать на стандартное устройство сумму этих двух чисел – так же длинное число.</p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><span lang=\"en-US\"><b> Input</b></span></font></p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">17 17</font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">56356356456456456</font></p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">87987987897897987</font></p>\r\n<p lang=\"en-US\" style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><br /> </p>\r\n<p style=\"text-indent: 0.32cm; margin-bottom: 0cm;\"><font size=\"4\"><span lang=\"en-US\"><b>Sample</b></span></font><font size=\"4\"><span lang=\"en-US\"><b> Output</b></span></font></p>\r\n<p lang=\"en-US\" style=\"margin-left: 0.32cm; margin-bottom: 0cm;\"><font face=\"Courier New, monospace\">144344344354354443<br /><br /></font></p> ','','','Задача №12: Длинное сложение','problem12');

CREATE TABLE `mdl_problemstatement_problem_input_generator` (
  `problem_id` int(10) unsigned NOT NULL,
  `input_generator_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`problem_id`,`input_generator_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_problem_input_output` (
  `problem_id` int(10) unsigned NOT NULL,
  `n` int(10) unsigned NOT NULL,
  `data_type_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`problem_id`,`n`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `mdl_problemstatement_problem_input_output_params` (
  `problem_id` int(10) unsigned NOT NULL,
  `n` int(10) unsigned NOT NULL,
  `format_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`problem_id`,`n`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `mdl_problemstatement_problem_input_output_params` VALUES (1,0,4);

CREATE TABLE `mdl_problemstatement_problem_solution` (
  `problem_id` int(10) unsigned NOT NULL,
  `solution_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`problem_id`,`solution_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `mdl_problemstatement_problem_solution` VALUES (1,1),(1,2),(1,3);

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

INSERT INTO `mdl_problemstatement_solution` VALUES (1,'begin\n	writeln(\'hello world\');\nend.',1),(2,'#include <stdio.h>\nmain() {\n	printf(\"hello world\");\n	return 0;\n}\n',0),(3,'#include <iostream>\nusing namespace std;\nmain() {\n	cout<<\"hello world\"<<endl;\n	return 0;\n}\n',0);

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
