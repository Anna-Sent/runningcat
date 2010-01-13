<?php  // $Id: file.php,v 1.6 2006/08/31 08:51:09 toyomoyo Exp $
error_reporting(E_ALL); 
    require("../../config.php");
    require("lib.php");
    require("problemstatement.class.php");
 
    $id     = required_param('id', PARAM_INT);      // Course Module ID
    $userid = required_param('userid', PARAM_INT);  // User ID

    if (! $cm = get_coursemodule_from_id('problemstatement', $id)) {
        error("Course Module ID was incorrect");
    }

    if (! $problemstatement = get_record("problemstatement", "id", $cm->instance)) {
        error("Problemstatement ID was incorrect");
    }

    if (! $course = get_record("course", "id", $problemstatement->course)) {
        error("Course is misconfigured");
    }

    if (! $user = get_record("user", "id", $userid)) {
        error("User is misconfigured");
    }

    require_login($course->id, false, $cm);

    if (($USER->id != $user->id) && !has_capability('mod/problemstatement:grade', get_context_instance(CONTEXT_MODULE, $cm->id))) {
        error("You can not view this problemstatement");
    }

    $problemstatementinstance = new problemstatement($cm->id, $problemstatement, $cm, $course);

    if ($submission = $problemstatementinstance->get_submission($user->id)) {
        print_header(fullname($user,true).': '.$problemstatement->name);

        print_simple_box_start('center', '', '', '', 'generalbox', 'dates');
        echo '<table>';
        if ($problemstatement->timedue) {
            echo '<tr><td class="c0">'.get_string('duedate','problemstatement').':</td>';
            echo '    <td class="c1">'.userdate($problemstatement->timedue).'</td></tr>';
        }
        echo '<tr><td class="c0">'.get_string('lastedited').':</td>';
        echo '    <td class="c1">'.userdate($submission->timemodified);
        /// Decide what to count
            //if ($CFG->problemstatement_itemstocount == ASSIGNMENT_COUNT_WORDS) {
                echo ' ('.get_string('numwords', '', count_words($submission->programtext)).')</td></tr>';
            //} else if ($CFG->problemstatement_itemstocount == ASSIGNMENT_COUNT_LETTERS) {
                echo ' ('.get_string('numletters', '', count_letters($submission->programtext)).')</td></tr>';
            //}
        echo '</table>';
        print_simple_box_end();

        print_simple_box(highlight_syntax($submission->programtext, $submission->langid), 'center', '100%');

        print_simple_box(format_text($submission->submissioncomment,0), 'center', '100%');

        close_window_button();
        print_footer('none');
    } else {
        print_string('emptysubmission', 'problemstatement');
    }

?>
