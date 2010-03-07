<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru" dir="ltr">
	<head>
		<?php if (intval($_REQUEST['update'])!=0 && $_REQUEST['update']>0) print '<meta http-equiv="refresh" content="'.$_REQUEST['update'].'">'; ?>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Отчет по самостоятельной работе</title>
		<script src="jquery.js" type="text/javascript"></script>
		<script src="ui.tablesorter.js" type="text/javascript"></script>
		<script src="date.js" type="text/javascript"></script>
		<script src="jquery.datePicker.js" type="text/javascript"></script>
		<script src="dates.js" type="text/javascript"></script>
		<link href="datePicker.css" media="all" rel="stylesheet" type="text/css"></link>
		<link href="style.css" media="all" rel="stylesheet" type="text/css"></link>
	</head>
<?php
require "config.php";
$M=intval($_REQUEST['M'])!=0&&$_REQUEST['M']>0?$_REQUEST['M']:20;
$grsize=intval($_REQUEST['grsize'])!=0&&$_REQUEST['grsize']>0?$_REQUEST['grsize']:4;
$f=isset($_REQUEST['f'])&&($_REQUEST['f']==1||$_REQUEST['f']==2)?$_REQUEST['f']:1;

function get_colored_status($processed, $succeeded, $count) {
	$msg = '';
	$color = "#000000";
	switch ($processed) {
		case 0: $msg .= 'unprocessed'; $color = "#AAAA00"; break;
		case 1:	switch ($succeeded) {
				case 0: $msg .= 'unsuccess'; $color = "#AA0000"; break;
				case 1: $msg .= 'success'; $color = "#00AA00"; break;
				case 2: $msg .= 'compilation error'; $color = "#AA0000"; break;
				case 3: $msg .= 'internal error'; $color = "#0000AA"; break;
				case 4: $msg .= 'time out error'; $color = "#AA0000"; break;
				case 5: $msg .= 'memory out error'; $color = "#AA0000"; break;
				case 6: $msg .= 'runtime error'; $color = "#AA0000"; break;
			}; break;
		case 2: $msg .= 'waiting'; $color = "#00AAAA"; break;
	}
	return '<font color="'.$color.'"><strong>'.$msg.' ('.$count.')</strong></font><br>';
}

function equal($processed1, $succeeded1, $processed2, $succeeded2) {
	if ($processed1==$processed2)
		if ($processed1==0 || $processed1==2)
			return true;
		else if ($succeeded1==$succeeded2)
			return true;
		else
			return false;
	else
		return false;
}

function osv_row($userid,$lastname,$firstname,&$results)
{
	global $conf;
	//$from=$_REQUEST['from'];
	//$to=$_REQUEST['to'];
	mysql_connect($conf['dbhost'],$conf['dbuser'],$conf['dbpassword']) or die(mysql_error());
	mysql_select_db($conf['dbase']) or die(mysql_error());
	$result[0] = $lastname.' '.$firstname;

	$res=mysql_query("select id,name from mdl_problemstatement order by id");//where course=...
	if (!$res) print mysql_error();
	while ($problemstatementrow=mysql_fetch_assoc($res)) {
		$problemstatementid = $problemstatementrow['id'];

		$res1=mysql_query("select submissions.processed,submissions.succeeded from mdl_user user join mdl_problemstatement_submissions submissions on (submissions.userid=user.id) where (submissions.problemstatement={$problemstatementid} and user.id={$userid}) order by submissions.timemodified");
		if (!$res1) print mysql_error();

		$result[$problemstatementid]='';
		$oldprocessed = null;
		$oldsucceeded = null;
		$count = 0;
		$succ = FALSE;
		while ($row = mysql_fetch_assoc($res1)) {
			$processed = $row['processed'];
			$succeeded = $row['succeeded'];
			if ($processed==1&&$succeeded==1) $succ=TRUE;
			if (($count>0) && !equal($processed,$succeeded,$oldprocessed,$oldsucceeded)) {
				$result[$problemstatementid] .= get_colored_status($oldprocessed,$oldsucceeded,$count);
				$count = 1;
			} else
				++$count;
			$oldprocessed = $processed;
			$oldsucceeded = $succeeded;
		}
		if ($count>0) {
			$result[$problemstatementid] .= get_colored_status($oldprocessed,$oldsucceeded,$count);
		}
		$results[$problemstatementid]=$succ;
	}

	// count max index
	$res=mysql_query("select max(id) from mdl_problemstatement"); // where course=...
	if (!$res) print mysql_error();
	$maxindex = mysql_fetch_row($res);
	$maxindex = $maxindex[0];

	$res=mysql_query("select id from mdl_problemstatement"); // where course=...
	if (!$res) print mysql_error();
	$i=0;
	while ($problemstatementrow=mysql_fetch_assoc($res)) {
		$problemstatementid = $problemstatementrow['id'];
		$res1=mysql_query("select count(submissions.succeeded) from mdl_problemstatement_submissions submissions where (submissions.problemstatement={$problemstatementid}) and (submissions.userid={$userid}) and (submissions.succeeded=1) and (submissions.processed=1)");
		if (!$res1) print mysql_error();
		$count = mysql_fetch_row($res1);
		$count = $count[0];
		if ($count>0) ++$i;
	}

	$result[$maxindex+1] = $i;

	return $result;
}

function format_row($row)
{
	$result='';
	foreach($row as $column) {
		$result.="<td>{$column}</td>";
	}
	return $result;
}

function f($k, $x, $n) {
	global $f;
	if ($f==1) return $k/$x;
	else if ($f==2) return $k*sqrt($n+1-$x);
	else return 0;
}

function getBalls(&$A, &$B, &$C, &$D) {
	global $M, $grsize;
	foreach($A as $uid=>$row)
		foreach($row as $prid=>$succ)
			if ($succ) {
				if (array_key_exists($prid,$B)) $B[$prid]+=1; else $B[$prid]=1;
			} else if (!array_key_exists($prid,$B)) $B[$prid]=0;
	foreach($A as $uid=>$row)
		foreach($row as $prid=>$succ)
			if (!array_key_exists($uid,$C)) $C[$uid]=0;
	$k=0;
	foreach($C as $uid=>$c) {
		$tmp=0;
		foreach ($B as $prid=>$b)
			if ($b>0)
				$tmp+= f($A[$uid][$prid], $b, count($C));
		if ($tmp>$k)
			$k=$tmp;
	}
	$k = $M / $k;
	foreach($B as $prid=>&$b)
		if ($b) $b = round(f($k, $b, count($C)),2);
		else $b=0;
	foreach($C as $uid=>&$c) {
		$c=0;
		foreach($B as $prid=>$b)
			$c+=$A[$uid][$prid]*$b;
		$c = intval(round($c));
		$index = intval($c/$grsize);
		// calculate distribution D count of points=>count of users
		if (array_key_exists($index,$D)) $D[$index]+=1;
		else $D[$index]=1;
	}
	//foreach($D as $x=>$y)
	//	echo $x.' => '.$y.'; ';
}

function format_table(&$osv)
{
	$rows=$osv['text'];
	$b = array();
	$c = array();
	$d = array();
	getBalls($osv['results'],$b,$c,$d);
//===begin table===
	$result='<table border="1">';
//===head===
	$result.='<thead><tr><th>Фамилия, имя</th>';
	$res=mysql_query("select problem.name from mdl_problemstatement problemstatement join mdl_problemstatement_problem problem on (problem.id=problemstatement.problem_id) order by problemstatement.id"); // where course=...
	if (!$res) print mysql_error();
	while ($row=mysql_fetch_assoc($res)) {
		$result.='<th>'.$row['name'].'</th>';
	}
	$result.='<th>Количество решенных задач</th><th>Баллы за решение задач</th></tr></thead>';
//===foot===
	$result .= '<tfoot><tr><td>Количество верных решений</td>';
	$res=mysql_query("select id,name from mdl_problemstatement order by id");//where course=...
	if (!$res) print mysql_error();
	while ($problemstatementrow=mysql_fetch_assoc($res)) {
		$problemstatementid = $problemstatementrow['id'];
		$i=0;
		$res1=mysql_query("select id,deleted from mdl_user where id>25 and deleted=0"); // where group = ...
		if (!$res1) print mysql_error();
		while ($users=mysql_fetch_assoc($res1)) {
			$userid = $users['id'];
			$res2=mysql_query("select count(submissions.succeeded) from mdl_problemstatement_submissions submissions where (submissions.problemstatement={$problemstatementid}) and (submissions.userid={$userid}) and (submissions.succeeded=1) and (submissions.processed=1)");
			if (!$res2) print mysql_error();
			$count = mysql_fetch_row($res2);
			$count = $count[0];
			if ($count>0) ++$i;
		}
		$result .= '<td>'.$i.'</td>';
	}
	$result .= '<td></td><td></td></tr>';
	$result .= '<tr><td>Баллы за задачу</td>';
	foreach($b as $id=>$ball) {
		$result.="<td>$ball</td>";
	}
	$result.='<td></td><td></td></tr>';
	$result .= '</tfoot>';
//===content===
	$result.='<tbody>';
	foreach($rows as $key=>$row)
		if ($row) $result.='<tr>'.format_row($row)."<td>".$c[$key]."</td>".'</tr>';
//===end table===
	$result.='</tbody></table>';
	$d2=array();
	$max_key=max(array_keys($d));
	for($i=0;$i<=$max_key;++$i)
		if (array_key_exists($i,$d))
			$d2[$i]=$d[$i];
		else
			$d2[$i]=0;
	$str=implode(';',$d2);
	global $grsize;
	$result.='<p><font size="6"><strong>Распределение баллов</strong></font></p>';
	$result.="<img src='histogram.php?grsize={$grsize}&vals={$str}'></img>";
	return $result;
}

function osv() {
	global $conf;
	mysql_connect($conf['dbhost'],$conf['dbuser'],$conf['dbpassword']) or die(mysql_error());
	mysql_set_charset("utf8");
	mysql_select_db($conf['dbase']) or die(mysql_error());
	$results=array();
	$res=mysql_query("select id,deleted,firstname,lastname from mdl_user where id>25 and deleted=0 order by lastname,firstname"); // from some group!!
	while ($row=mysql_fetch_assoc($res)) {
		$usersubmissions=osv_row($row['id'],$row['lastname'],$row['firstname'],$results[$row['id']]);
		$result[$row['id']]=$usersubmissions;
	}
	return array('text'=>$result,'results'=>$results);
}
/*if (!$_REQUEST['from']) {
	$_REQUEST['from']='2009.01.01';
}
if (!$_REQUEST['to']) {
	$_REQUEST['to']='2011.01.01';
}*/
?>
<body>
<!--p>Выберите расчетный период</p-->
<form action='db.php'>
<!--input type='text' id='s-from' name='from' value='<!?print $_REQUEST['from']?>'></input-->
<!--input type='text' id='s-to' name='to' value='<!?print $_REQUEST['to']?>'></input-->
<!--input type='submit'></input-->
<!--p><strong>Оборотно-сальдовая ведомость</strong></p-->
</form>
<?php
print format_table(osv());
?>
</body>
</html>
