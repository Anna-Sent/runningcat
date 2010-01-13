<?php // $Id: index.php,v 1.5 2006/08/28 16:41:20 mark-nielsen Exp $
error_reporting(E_ALL); 
/**
 * This page lists all the instances of problemstatement in a particular course
 *
 * @author Sentyakova Anna
 * @version $Id: index.php,v 1.5 2006/08/28 16:41:20 mark-nielsen Exp $
 * @package problemstatement
 **/

    require_once("../../config.php");
    require_once("lib.php");

    $id = required_param('id', PARAM_INT);   // course

    if (! $course = get_record("course", "id", $id)) {
        error("Course ID is incorrect");
    }

    require_login($course->id);

    add_to_log($course->id, "problemstatement", "view all", "index.php?id=$course->id", "");


/// Get all required strings

    $strproblemstatements = get_string("modulenameplural", "problemstatement");
    $strproblemstatement  = get_string("modulename", "problemstatement");


/// Print the header

    if ($course->category) {
        $navigation = "<a href=\"../../course/view.php?id=$course->id\">$course->shortname</a> ->";
    } else {
        $navigation = '';
    }

    print_header("$course->shortname: $strproblemstatements", "$course->fullname", "$navigation $strproblemstatements", "", "", true, "", navmenu($course));

/// Get all the appropriate data

    if (! $problemstatements = get_all_instances_in_course("problemstatement", $course)) {
        notice("There are no problemstatements", "../../course/view.php?id=$course->id");
        die;
    }

/// Print the list of instances (your module will probably extend this)

    $timenow = time();
    $strname  = get_string("name");
    $strweek  = get_string("week");
    $strtopic  = get_string("topic");

    if ($course->format == "weeks") {
        $table->head  = array ($strweek, $strname);
        $table->align = array ("center", "left");
    } else if ($course->format == "topics") {
        $table->head  = array ($strtopic, $strname);
        $table->align = array ("center", "left", "left", "left");
    } else {
        $table->head  = array ($strname);
        $table->align = array ("left", "left", "left");
    }

    foreach ($problemstatements as $problemstatement) {
        if (!$problemstatement->visible) {
            //Show dimmed if the mod is hidden
            $link = "<a class=\"dimmed\" href=\"view.php?id=$problemstatement->coursemodule\">$problemstatement->name</a>";
        } else {
            //Show normal if the mod is visible
            $link = "<a href=\"view.php?id=$problemstatement->coursemodule\">$problemstatement->name</a>";
        }

        if ($course->format == "weeks" or $course->format == "topics") {
            $table->data[] = array ($problemstatement->section, $link);
        } else {
            $table->data[] = array ($link);
        }
    }

    echo "<br />";

    print_table($table);

/// Finish the page

    print_footer($course);

?>
