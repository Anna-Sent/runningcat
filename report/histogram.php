<?php
$vals=explode(';',$_REQUEST['vals']);
$count=count($vals);
$grsize=$_REQUEST['grsize'];
putenv('GDFONTPATH=' . realpath('.'));

$height=400;
$width=600;
$margin=array(20,40,120,30);
$img=imagecreatetruecolor($width,$height);
$clientwidth=$width-$margin[0]-$margin[2];
$clientheight=$height-$margin[1]-$margin[3];
$colwidth=$clientwidth/$count;
$max=max($vals);
$scale=$clientheight/$max;
$color=imagecolorallocate($img,255,0,0);
$y1=$height-$margin[3];
$bgcolor=imagecolorallocate($img,255,255,255);
$fillcolor=imagecolorallocate($img,0,200,200);
$textcolor=imagecolorallocate($img,0,0,0);
imagefill($img,0,0,$bgcolor);
imagefttext($img,9,0,10,10,$textcolor,"font","Количество студентов");
imagefttext($img,9,0,$width-120,$height-$margin[3]+30,$textcolor,"font","Количество баллов");
for($i=0;$i<$count;++$i) {
	$x1=$colwidth*$i+$margin[0];
	$x2=$x1+$colwidth;
	$y2=$y1-$vals[$i]*$scale;
	imagefilledrectangle($img,$x1,$y1,$x2,$y2,$fillcolor);
}
for($i=0;$i<$count;++$i) {
	$x1=$colwidth*$i+$margin[0];
	$x2=$x1+$colwidth;
	$y2=$y1-$vals[$i]*$scale;
	imageline($img,$x2,$height-$margin[3]+3,$x2,$height-$margin[3]-3,$textcolor);
	imagefttext($img,8,0,$x2,$height-$margin[3]+15,$textcolor,"font",($i+1)*$grsize);
	imageline($img,$margin[0]-3,$y2,$margin[0]+3,$y2,$textcolor);
	imagefttext($img,8,0,$margin[0]-10,$y2,$textcolor, "font",$vals[$i]);
}
imageline($img,$margin[0],$margin[1]-20,$margin[0],$height-$margin[3],$textcolor);
imageline($img,$margin[0],$height-$margin[3],$width-$margin[2]+110,$height-$margin[3],$textcolor);

header('Content-type: image/png');

imagepng($img);
imagedestroy($img);
