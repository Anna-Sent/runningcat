<?php
	require_once("../../config.php");

   if (!$data = get_record("problemstatement_problem", "id", $_REQUEST['id'])) {
        error("Problem ID is incorrect");
   }
//print_r($data);
$data->description=str_replace("'","\'",$data->description);
$data->restrictions=str_replace("'","\'",$data->restrictions);
$data->samples=str_replace("'","\'",$data->samples);

print "{ description:'{$data->description}', restrictions:'{$data->restrictions}', samples:'{$data->samples}' }";
?>
