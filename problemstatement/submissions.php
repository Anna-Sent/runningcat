<?php  // $Id: submissions.php,v 1.43 2006/08/28 08:42:30 toyomoyo Exp $
	error_reporting(E_ALL);
    require_once("../../config.php");
    require_once("lib.php");

    $id   = optional_param('id', 0, PARAM_INT);          // Course module ID
    $a    = optional_param('a', 0, PARAM_INT);           // problemstatement ID
    $mode = optional_param('mode', 'all', PARAM_ALPHA);  // What mode are we in?

    if ($id) {
        if (! $cm = get_coursemodule_from_id('problemstatement', $id)) {
            error("Course Module ID was incorrect");
        }

        if (! $problemstatement = get_record("problemstatement", "id", $cm->instance)) {
            error("problemstatement ID was incorrect");
        }

        if (! $course = get_record("course", "id", $problemstatement->course)) {
            error("Course is misconfigured");
        }
    } else {
        if (!$problemstatement = get_record("problemstatement", "id", $a)) {
            error("Course module is incorrect");
        }
        if (! $course = get_record("course", "id", $problemstatement->course)) {
            error("Course is misconfigured");
        }
        if (! $cm = get_coursemodule_from_instance("problemstatement", $problemstatement->id, $course->id)) {
            error("Course Module ID was incorrect");
        }
    }

    require_login($course->id, false, $cm);

    require_capability('mod/problemstatement:grade', get_context_instance(CONTEXT_MODULE, $cm->id));

/// Load up the required problemstatement code
    require($CFG->dirroot.'/mod/problemstatement/problemstatement.class.php');
    $problemstatementclass = 'problemstatement';
    $problemstatementinstance = new $problemstatementclass($cm->id, $problemstatement, $cm, $course);

    $problemstatementinstance->submissions($mode);   // Display or process the submissions

?>
