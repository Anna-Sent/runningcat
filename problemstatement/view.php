<?php
error_reporting(E_ALL); 
	require_once("../../config.php");
    require_once("lib.php");

    $id = optional_param('id', 0, PARAM_INT); // Course Module ID, or
    $a  = optional_param('a', 0, PARAM_INT);  // problemstatement ID

    if ($id) {
        if (! $cm = get_record("course_modules", "id", $id)) {
            error("Course Module ID was incorrect");
        }
    
        if (! $course = get_record("course", "id", $cm->course)) {
            error("Course is misconfigured");
        }
    
        if (! $problemstatement = get_record("problemstatement", "id", $cm->instance)) {
            error("Problemstatement ID is incorrect");
        }

    } else {
        if (! $problemstatement = get_record("problemstatement", "id", $a)) {
            error("Course module is incorrect");
        }
        if (! $course = get_record("course", "id", $problemstatement->course)) {
            error("Course is misconfigured");
        }
        if (! $cm = get_coursemodule_from_instance("problemstatement", $problemstatement->id, $course->id)) {
            error("Course Module ID was incorrect");
        }
    }

    require_login($course->id);

    add_to_log($course->id, "problemstatement", "view", "view.php?id=$cm->id", "$problemstatement->id");

/// Print the page header

    if ($course->category) {
        $navigation = "<a href=\"../../course/view.php?id=$course->id\">$course->shortname</a> ->";
    } else {
        $navigation = '';
    }

    $strproblemstatements = get_string("modulenameplural", "problemstatement");
    $strproblemstatement  = get_string("modulename", "problemstatement");

    print_header("$course->shortname: $problemstatement->name", "$course->fullname",
                 "$navigation <a href=index.php?id=$course->id>$strproblemstatements</a> -> $problemstatement->name", 
                  "", "", true, update_module_button($cm->id, $course->id, $strproblemstatement), 
                  navmenu($course, $cm));

/// Print the main part of the page

    //echo "YOUR CODE GOES HERE";
    require ("$CFG->dirroot/mod/problemstatement/problemstatement.class.php");
    $problemstatementinstance = new problemstatement($cm->id, $problemstatement, $cm, $course);
    $problemstatementinstance->view();   // Actually display the problemstatement!

/// Finish the page
    //print_footer($course);

?>
