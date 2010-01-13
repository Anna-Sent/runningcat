<?php
require_once($CFG->libdir.'/formslib.php');
require_once($CFG->geshi);
//<script src="jquery-1.3.2.min.js" type="text/javascript">

/*function get_languages_for_geshi() {
	$result=array();
	result[]='cpp';
	result[]='delphi';
}*/

function highlight_syntax($code, $langid) {
	$syntax='';
	switch ($langid) {
		case '0': $syntax='cpp'; break;
		case '1': $syntax='delphi'; break;
		case '2': $syntax='java'; break;
		case '3': $syntax='python'; break;
	}
    $geshi = new GeSHi($code, $syntax);
    $geshi->set_header_type(GESHI_HEADER_DIV);

 //   $geshi->enable_classes(true);
    $geshi->set_overall_style('font-family: monospace;');
$linenumbers=1;
    if($linenumbers) {
      $geshi->enable_line_numbers(GESHI_FANCY_LINE_NUMBERS, 5);
      $geshi->set_line_style('color:#222;', 'color:#888;');
      $geshi->set_overall_style('font-size: 14px;font-family: monospace;', true);
    }
	
	$urls=FALSE;
	$indentsize=FALSE;
	$inline=FALSE;
   
    if (!$urls) {
      for ($i = 0; $i < 5; $i++) {
    $geshi->set_url_for_keyword_group($i, '');
      }
    }
       
    if ($indentsize) {
      $geshi->set_tab_width($indentsize);
    }
   
    $parsed = $geshi->parse_code();
    if($inline) {
      $parsed = preg_replace('/^<div/','<span', $parsed);
      $parsed = preg_replace('/<\/div>$/','</span>', $parsed);
    }
//return $geshi->parse_code().$syntax;
	$comment=get_string("programwritten", "problemstatement").get_string("lang_".$langid, "problemstatement");
  return $parsed.$comment;
}

/**
 * Problemstatement class. Programming task.
 * 
 */
class problemstatement {
    var $cm;
    var $course;
    var $problemstatement;
    var $strproblemstatement;
    var $strproblemstatements;
    var $strsubmissions;
    var $strlastmodified;
    var $pagetitle;
    var $usehtmleditor;
    var $defaultformat;
    var $context;
    //var $type;

    /**
     * Constructor for the problemstatement class
     *
     * Constructor for the problemstatement class.
     * If cmid is set create the cm, course, problemstatement objects.
     * If the problemstatement is hidden and the user is not a teacher then
     * this prints a page header and notice.
     *
     * @param cmid   integer, the current course module id - not set for new problemstatements
     * @param problemstatement   object, usually null, but if we have it we pass it to save db access
     * @param cm   object, usually null, but if we have it we pass it to save db access
     * @param course   object, usually null, but if we have it we pass it to save db access
     */
    function problemstatement($cmid='staticonly', $problemstatement=NULL, $cm=NULL, $course=NULL) {
        global $COURSE;

        if ($cmid == 'staticonly') {
            //use static functions only!
            return;
        }

        global $CFG;

        if ($cm) {
            $this->cm = $cm;
        } else if (! $this->cm = get_coursemodule_from_id('problemstatement', $cmid)) {
            error('Course Module ID was incorrect');
        }

        $this->context = get_context_instance(CONTEXT_MODULE, $this->cm->id);

        if ($course) {
            $this->course = $course;
        } else if ($this->cm->course == $COURSE->id) {
            $this->course = $COURSE;
        } else if (! $this->course = get_record('course', 'id', $this->cm->course)) {
            error('Course is misconfigured');
        }

        if ($problemstatement) {
            $this->problemstatement = $problemstatement;
        } else if (! $this->problemstatement = get_record('problemstatement', 'id', $this->cm->instance)) {
            error('problemstatement ID was incorrect');
        }
	//	print "hello";
		$this->problem = get_record('problemstatement_problem','id',$this->problemstatement->problem_id);
		$this->problemstatement->description = $this->problem->description;
		$this->problemstatement->restrictions = $this->problem->restrictions;
		$this->problemstatement->samples = $this->problem->samples;
		$this->problemstatement->name = $this->problemstatement->name." ".$this->problem->name;
		$this->problemstatement->testsdir = $this->problem->testsdir;
	//	var_dump($this->problem);
        $this->problemstatement->cmidnumber = $this->cm->id;     // compatibility with modedit problemstatement obj
        $this->problemstatement->courseid   = $this->course->id; // compatibility with modedit problemstatement obj

        $this->strproblemstatement = get_string('modulename', 'problemstatement');
        $this->strproblemstatements = get_string('modulenameplural', 'problemstatement');
        $this->strsubmissions = get_string('submissions', 'problemstatement');
        $this->strlastmodified = get_string('lastmodified');
        $this->pagetitle = strip_tags($this->course->shortname.': '.$this->strproblemstatement.': '.format_string($this->problemstatement->name,true));

// visibility handled by require_login() with $cm parameter
// get current group only when really needed

    /// Set up things for a HTML editor if it's needed
        /*if ($this->usehtmleditor = can_use_html_editor()) {
            $this->defaultformat = FORMAT_HTML;
        } else {
            $this->defaultformat = FORMAT_MOODLE;
        }*/
		$this->problemstatement->format = FORMAT_HTML;
//var_dump($this->cm);
    }

    function view() {

        global $USER;
	//echo "beginning of view";

        $edit  = optional_param('edit', 0, PARAM_BOOL);
        $saved = optional_param('saved', 0, PARAM_BOOL);

        $context = get_context_instance(CONTEXT_MODULE, $this->cm->id);
	require_capability('mod/problemstatement:view', $context);

        $submission = $this->get_submission();
		//var_dump($this->problemstatement);

        //Guest can not submit nor edit an problemstatement (bug: 4604)
        if (!has_capability('mod/problemstatement:submit', $context)) {
            $editable = null;
        } else {
            $editable = $this->isopen() && (!$submission || $this->problemstatement->allowresubmit || !$submission->timemarked);
        }
        $editmode = ($editable and $edit);

        if ($editmode) {
            //guest can not edit or submit problemstatement
            if (!has_capability('mod/problemstatement:submit', $context)) {
                print_error('guestnosubmit', 'problemstatement');
            }
        }

        add_to_log($this->course->id, "problemstatement", "view", "view.php?id={$this->cm->id}", $this->problemstatement->id, $this->cm->id);

/// prepare form and process submitted data
        $mform = new mod_problemstatement_online_edit_form();
        $defaults = new object();
        $defaults->id = $this->cm->id;
	if (!empty($submission)) {
		$defaults->programtext = $submission->programtext;
		$defaults->langid = $submission->langid;
	}
	/*if (!empty($submission)) {
            if ($this->usehtmleditor) {
                $options = new object();
                $options->smiley = false;
                $options->filter = false;

                //$defaults->text   = format_text($submission->data1, $submission->data2, $options);
                $defaults->text   = $submission->programtext;
                //$defaults->format = FORMAT_TEXT;
            } else {
                $defaults->text   = $submission->programtext;
                //$defaults->format = 0;
            }
        }*/
        $mform->set_data($defaults);

        if ($mform->is_cancelled()) {
            redirect('view.php?id='.$this->cm->id);
        }
        if ($data = $mform->get_data()) {      // No incoming data?
            if ($editable && $this->update_submission($data)) {
                //TODO fix log actions - needs db upgrade
                $submission = $this->get_submission();
                add_to_log($this->course->id, 'problemstatement', 'upload',
                        'view.php?a='.$this->problemstatement->id, $this->problemstatement->id, $this->cm->id);
                //$this->email_teachers($submission);
                //redirect to get updated submission date and word count
                redirect('view.php?id='.$this->cm->id.'&saved=1');
            } else {
                // TODO: add better error message
                notify(get_string("error")); //submitting not allowed!
            }
        }

/// print header, etc. and display form if needed
        if ($editmode) {
            $this->view_header(get_string('editmysubmission', 'problemstatement'));
        } else {
            $this->view_header();
        }
	if ($editmode) {
	echo '
<script language="javascript" type="text/javascript" src="/editarea/edit_area/edit_area_compressor.php?plugins"></script>
<script language="javascript" type="text/javascript">
var _onload=null;

function change_style(sender)
{
	val=sender.options[sender.selectedIndex].value;
	lang="";

	if (val=="1") lang="pas"; else
	if (val=="0") lang="cpp"; else
	if (val=="2") lang="java"; else
	if (val=="3") lang="python";
	editAreaLoader.execCommand(\'id_programtext\',"change_syntax",lang);
	return lang;
};
function add_onchange()
{
	sel=document.getElementById(\'id_langid\')
	sel.onchange=function(){change_style(this)};
	lang=change_style(sel)
editAreaLoader.init({
        id : "id_programtext"        // textarea id
        ,syntax: lang          // syntax to be uses for highlight mode on start-up
        ,start_highlight: true  // to display with highlight mode on start-up
});
	if (_onload!=null) _onload();
};
_onload=window.onload;
window.onload=add_onchange;
</script>
';
	}

        $this->view_intro();

        $this->view_dates();

        if ($saved) {
            notify(get_string('submissionsaved', 'problemstatement'), 'notifysuccess');
        }

        if (has_capability('mod/problemstatement:submit', $context)) {
            if ($editmode) {
                print_box_start('generalbox', 'online');
                $mform->display();
            } else {
                print_box_start('generalbox boxwidthwide boxaligncenter', 'online');
                if ($submission) {
		echo highlight_syntax($submission->programtext,$submission->langid);
		$msg="";
		switch ($submission->processed) {
			case "0": $msg .= get_string("unprocessed", "problemstatement"); break;
			case "1": switch ($submission->succeeded) {
						case "0": $msg .= get_string("unsuccess", "problemstatement"); break;
						case "1": $msg .= get_string("success", "problemstatement"); break;
						case "2": $msg .= get_string("compilationerror", "problemstatement")."\n".$submission->submissioncomment; break;
						case "3": $msg .= get_string("internalerror", "problemstatement"); break;
						case "4": $msg .= get_string("timeout","problemstatement"); break;
						case "5": $msg .= get_string("memoryout","problemstatement"); break;
						case "6": $msg .= get_string("runtimeerror","problemstatement"); break;
					}
					break;
			case "2": $msg .= get_string("inprocess", "problemstatement"); break;
		}
		echo "<div style='text-align:left'>".nl2br($msg)."</div>";
		//echo format_text($submission->programtext, 0);//$submission->data2);
                } else if (!has_capability('mod/problemstatement:submit', $context)) { //fix for #4604
                    echo '<div style="text-align:center">'. get_string('guestnosubmit', 'problemstatement').'</div>';
                } else if ($this->isopen()){    //fix for #4206
                    echo '<div style="text-align:center">'.get_string('emptysubmission', 'problemstatement').'</div>';
                }
            }
            print_box_end();
            if (!$editmode && $editable) {
                echo "<div style='text-align:center'>";
                print_single_button('view.php', array('id'=>$this->cm->id,'edit'=>'1'),
                        get_string('editmysubmission', 'problemstatement'));
                echo "</div>";
            }

        }

        $this->view_feedback();

        $this->view_footer();
	//echo "end of view";
    }

    /**
     * Display the header and top of a page
     *
     * This is used by the view() method to print the header of view.php but
     * it can be used on other pages in which case the string to denote the
     * page in the navigation trail should be passed as an argument
     *
     * @param $subpage string Description of subpage to be used in navigation trail
     */
    function view_header($subpage='') {

        global $CFG;

        if ($subpage) {
            $navigation = build_navigation($subpage, $this->cm);
        } else {
            $navigation = build_navigation('', $this->cm);
        }

        print_header($this->pagetitle, $this->course->fullname, $navigation, '', '',
                     true, update_module_button($this->cm->id, $this->course->id, $this->strproblemstatement),
                     navmenu($this->course, $this->cm));

        groups_print_activity_menu($this->cm, 'view.php?id=' . $this->cm->id);

        echo '<div class="reportlink">'.$this->submittedlink().'</div>';
        echo '<div class="clearer"></div>';
    }

    /**
     * Display the problemstatement intro
     *
     * This will most likely be extended by problemstatement type plug-ins
     * The default implementation prints the problemstatement description in a box
     */
    function view_intro() {
        print_simple_box_start('center', '', '', 0, 'generalbox', 'intro');
        $formatoptions = new stdClass;
        $formatoptions->noclean = true;
        echo format_text(
		"<strong>Постановка задачи</strong><br/>".
		$this->problemstatement->description, $this->problemstatement->format, $formatoptions);
        print_simple_box_end();
        print_simple_box_start('center', '', '', 0, 'generalbox', 'intro');
        $formatoptions = new stdClass;
        $formatoptions->noclean = true;
        echo format_text(
		"<strong>Ограничения</strong><br/>".
		$this->problemstatement->restrictions, $this->problemstatement->format, $formatoptions);
        print_simple_box_end();
        print_simple_box_start('center', '', '', 0, 'generalbox', 'intro');
        $formatoptions = new stdClass;
        $formatoptions->noclean = true;
        echo format_text(
		"<strong>Примеры</strong><br/>".
		$this->problemstatement->samples, $this->problemstatement->format, $formatoptions);
        print_simple_box_end();
}

    /*
     * Display the problemstatement dates
     */
    function view_dates() {
        global $USER, $CFG;

        if (!$this->problemstatement->timeavailable && !$this->problemstatement->timedue) {
            return;
        }

        print_simple_box_start('center', '', '', 0, 'generalbox', 'dates');
        echo '<table>';
        if ($this->problemstatement->timeavailable) {
            echo '<tr><td class="c0">'.get_string('availabledate','problemstatement').':</td>';
            echo '    <td class="c1">'.userdate($this->problemstatement->timeavailable).'</td></tr>';
        }
        if ($this->problemstatement->timedue) {
            echo '<tr><td class="c0">'.get_string('duedate','problemstatement').':</td>';
            echo '    <td class="c1">'.userdate($this->problemstatement->timedue).'</td></tr>';
        }
        $submission = $this->get_submission($USER->id);
        if ($submission) {
            echo '<tr><td class="c0">'.get_string('lastedited').':</td>';
            echo '    <td class="c1">'.userdate($submission->timemodified);
        /// Decide what to count
            /*if ($CFG->problemstatement_itemstocount == PROBLEMSTATEMENT_COUNT_WORDS) {
                echo ' ('.get_string('numwords', '', count_words(format_text($submission->programtext, 0))).')</td></tr>';
            } else if ($CFG->problemstatement_itemstocount == PROBLEMSTATEMENT_COUNT_LETTERS) {
                echo ' ('.get_string('numletters', '', count_letters(format_text($submission->programtext, 0))).')</td></tr>';
            }*/
        }
        echo '</table>';
        print_simple_box_end();
    }

    /**
     * Display the bottom and footer of a page
     *
     * This default method just prints the footer.
     */
    function view_footer() {
        print_footer($this->course);
    }

    /**
     * Display the feedback to the student
     *
     * This default method prints the teacher picture and name, date when marked,
     * grade and teacher submissioncomment.
     *
     * @param $submission object The submission object or NULL in which case it will be loaded
     */
    function view_feedback($submission=NULL) {
        global $USER, $CFG;
        require_once($CFG->libdir.'/gradelib.php');

        if (!has_capability('mod/problemstatement:submit', $this->context, $USER->id, false)) {
            // can not submit problemstatements -> no feedback
            return;
        }

        if (!$submission) { /// Get submission for this problemstatement
            $submission = $this->get_submission($USER->id);
        }

        $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, $USER->id);
        $item = $grading_info->items[0];
        $grade = $item->grades[$USER->id];

        if ($grade->hidden or $grade->grade === false) { // hidden or error
            return;
        }

        if ($grade->grade === null and empty($grade->str_feedback)) {   /// Nothing to show yet
            return;
        }

        $graded_date = $grade->dategraded;
        $graded_by   = $grade->usermodified;

    /// We need the teacher info
        if (!$teacher = get_record('user', 'id', $graded_by)) {
            error('Could not find the teacher');
        }

    /// Print the feedback
        print_heading(get_string('feedbackfromteacher', 'problemstatement', $this->course->teacher)); // TODO: fix teacher string

        echo '<table cellspacing="0" class="feedback">';

        echo '<tr>';
        echo '<td class="left picture">';
        if ($teacher) {
            print_user_picture($teacher, $this->course->id, $teacher->picture);
        }
        echo '</td>';
        echo '<td class="topic">';
        echo '<div class="from">';
        if ($teacher) {
            echo '<div class="fullname">'.fullname($teacher).'</div>';
        }
        echo '<div class="time">'.userdate($graded_date).'</div>';
        echo '</div>';
        echo '</td>';
        echo '</tr>';

        echo '<tr>';
        echo '<td class="left side">&nbsp;</td>';
        echo '<td class="content">';
        echo '<div class="grade">';
        echo get_string("grade").': '.$grade->str_long_grade;
        echo '</div>';
        echo '<div class="clearer"></div>';

        echo '<div class="comment">';
        echo $grade->str_feedback;
        echo '</div>';
        echo '</tr>';

        echo '</table>';
    }

    function update_submission($data) {
        global $CFG, $USER;

        $submission = $this->get_submission($USER->id, true);
        //$update = new object();
        /*$update->id			= $submission->id;*/
		$update=$submission;
		unset($update->id);
        $update->programtext		= $data->programtext;
        $update->langid			= $data->langid;
        $update->timemodified		= time();
		$update->timecreated = time();
	$update->processed		= 0;
	$update->succeeded		= 0;
	$update->submissioncomment	= '';
        /*if (!update_record('problemstatement_submissions', $update)) {
            return false;
        }*/
        if (!insert_record('problemstatement_submissions', $update)) {
            return false;
        }
        $submission = $this->get_submission($USER->id);
        //$this->update_grade($submission);
        return true;
    }

    function print_student_answer($userid, $return=false){
        global $CFG;
        if (!$submission = $this->get_submission($userid)) {
            return '';
        }
        $output = '<div class="files">'.
                  '<img src="'.$CFG->pixpath.'/f/html.gif" class="icon" alt="html" />'.
                  link_to_popup_window ('/mod/problemstatement/file.php?id='.$this->cm->id.'&amp;userid='.
                  $submission->userid, 'file'.$userid, shorten_text(trim(strip_tags(format_text($submission->programtext,0))), 15), 450, 580,
                  get_string('submission', 'problemstatement'), 'none', true).
                  '</div>';
                  return $output;
    }

    /*function print_user_files($userid, $return=false) {
        global $CFG;

        if (!$submission = $this->get_submission($userid)) {
            return '';
        }

        $output = '<div class="files">'.
                  '<img align="middle" src="'.$CFG->pixpath.'/f/html.gif" height="16" width="16" alt="html" />'.
                  link_to_popup_window ('/mod/problemstatement/type/online/file.php?id='.$this->cm->id.'&amp;userid='.
                  $submission->userid, 'file'.$userid, shorten_text(trim(strip_tags(format_text($submission->data1,$submission->data2))), 15), 450, 580,
                  get_string('submission', 'problemstatement'), 'none', true).
                  '</div>';

        ///Stolen code from file.php

        print_simple_box_start('center', '', '', 0, 'generalbox', 'wordcount');
    /// Decide what to count
        if ($CFG->problemstatement_itemstocount == problemstatement_COUNT_WORDS) {
            echo ' ('.get_string('numwords', '', count_words(format_text($submission->data1, $submission->data2))).')';
        } else if ($CFG->problemstatement_itemstocount == problemstatement_COUNT_LETTERS) {
            echo ' ('.get_string('numletters', '', count_letters(format_text($submission->data1, $submission->data2))).')';
        }
        print_simple_box_end();
        print_simple_box(format_text($submission->data1, $submission->data2), 'center', '100%');

        ///End of stolen code from file.php

        if ($return) {
            //return $output;
        }
        //echo $output;
    }*/

    /**
     * Returns a link with info about the state of the problemstatement submissions
     *
     * This is used by view_header to put this link at the top right of the page.
     * For teachers it gives the number of submitted problemstatements with a link
     * For students it gives the time of their submission.
     * 
     * @param bool $allgroup print all groups info if user can access all groups, suitable for index.php
     * @return string
     */
    function submittedlink($allgroups=false) {
        global $USER;

        $submitted = '';

        $context = $this->context;//get_context_instance(CONTEXT_MODULE,$this->cm->id);

        if (has_capability('mod/problemstatement:grade', $context)) {
            if ($allgroups and has_capability('moodle/site:accessallgroups', $context)) {
                $group = 0;
            } else {
                $group = groups_get_activity_group($this->cm);
            }
            if ($count = $this->count_real_submissions($group)) {
                $submitted = '<a href="submissions.php?id='.$this->cm->id.'">'.
                             get_string('viewsubmissions', 'problemstatement', $count).'</a>';
            } else {
                $submitted = '<a href="submissions.php?id='.$this->cm->id.'">'.
                             get_string('noattempts', 'problemstatement').'</a>';
            }
        } else {
            if (!empty($USER->id)) {
                if ($submission = $this->get_submission($USER->id)) {
                    if ($submission->timemodified) {
                        if ($submission->timemodified <= $this->problemstatement->timedue || empty($this->problemstatement->timedue)) {
                            $submitted = '<span class="early">'.userdate($submission->timemodified).'</span>';
                        } else {
                            $submitted = '<span class="late">'.userdate($submission->timemodified).'</span>';
                        }
                    }
                }
            }
        }

        return $submitted;
    }


    /*function setup_elements(&$mform) {

    }*/

    /**
     * Create a new problemstatement activity
     *
     * Given an object containing all the necessary data,
     * (defined by the form in mod.html) this function
     * will create a new instance and return the id number
     * of the new instance.
     * The due data is added to the calendar
     *
     * @param $problemstatement object The data from the form on mod.html
     * @return int The id of the problemstatement
     */
    function add_instance($problemstatement) {
        global $COURSE;

        $problemstatement->timemodified = time();
        $problemstatement->courseid = $problemstatement->course;

        if ($returnid = insert_record("problemstatement", $problemstatement)) {
            $problemstatement->id = $returnid;
/*
            if ($problemstatement->timedue) {
                $event = new object();
                $event->name        = $problemstatement->name;
                $event->description = $problemstatement->description;
                $event->courseid    = $problemstatement->course;
                $event->groupid     = 0;
                $event->userid      = 0;
                $event->modulename  = 'problemstatement';
                $event->instance    = $returnid;
                $event->eventtype   = 'due';
                $event->timestart   = $problemstatement->timedue;
                $event->timeduration = 0;

                add_event($event);
            }

            $problemstatement = stripslashes_recursive($problemstatement);
            problemstatement_grade_item_update($problemstatement);
*/
        }


        return $returnid;
    }

    /**
     * Deletes an problemstatement activity
     *
     * Deletes all database records, files and calendar events for this problemstatement.
     * @param $problemstatement object The problemstatement to be deleted
     * @return boolean False indicates error
     */
    function delete_instance($problemstatement) {
        global $CFG;

        $problemstatement->courseid = $problemstatement->course;

        $result = true;

        if (! delete_records('problemstatement_submissions', 'problemstatement', $problemstatement->id)) {
            $result = false;
        }

        if (! delete_records('problemstatement', 'id', $problemstatement->id)) {
            $result = false;
        }

        /*if (! delete_records('event', 'modulename', 'problemstatement', 'instance', $problemstatement->id)) {
            $result = false;
        }*/

        // delete file area with all attachments - ignore errors
        /*require_once($CFG->libdir.'/filelib.php');
        fulldelete($CFG->dataroot.'/'.$problemstatement->course.'/'.$CFG->moddata.'/problemstatement/'.$problemstatement->id);

        problemstatement_grade_item_delete($problemstatement);
*/
        return $result;
    }

    /**
     * Updates a new problemstatement activity
     *
     * Given an object containing all the necessary data,
     * (defined by the form in mod.html) this function
     * will update the problemstatement instance and return the id number
     * The due date is updated in the calendar
     *
     * @param $problemstatement object The data from the form on mod.html
     * @return int The problemstatement id
     */
    function update_instance($problemstatement) {
        global $COURSE;

        $problemstatement->timemodified = time();

        $problemstatement->id = $problemstatement->instance;
        $problemstatement->courseid = $problemstatement->course;

        if (!update_record('problemstatement', $problemstatement)) {
            return false;
        }

        /*if ($problemstatement->timedue) {
            $event = new object();

            if ($event->id = get_field('event', 'id', 'modulename', 'problemstatement', 'instance', $problemstatement->id)) {

                $event->name        = $problemstatement->name;
                $event->description = $problemstatement->description;
                $event->timestart   = $problemstatement->timedue;

                update_event($event);
            } else {
                $event = new object();
                $event->name        = $problemstatement->name;
                $event->description = $problemstatement->description;
                $event->courseid    = $problemstatement->course;
                $event->groupid     = 0;
                $event->userid      = 0;
                $event->modulename  = 'problemstatement';
                $event->instance    = $problemstatement->id;
                $event->eventtype   = 'due';
                $event->timestart   = $problemstatement->timedue;
                $event->timeduration = 0;

                add_event($event);
            }
        } else {
            delete_records('event', 'modulename', 'problemstatement', 'instance', $problemstatement->id);
        }*/

        // get existing grade item
        //$problemstatement = stripslashes_recursive($problemstatement);

        //problemstatement_grade_item_update($problemstatement);

        return true;
    }

    /**
     * Update grade item for this submission.
     */
    function update_grade($submission) {
        problemstatement_update_grades($this->problemstatement, $submission->userid);
    }

    /**
     * Top-level function for handling of submissions called by submissions.php
     *
     * This is for handling the teacher interaction with the grading interface
     * This should be suitable for most problemstatement types.
     *
     * @param $mode string Specifies the kind of teacher interaction taking place
     */
    function submissions($mode) {
        ///The main switch is changed to facilitate
        ///1) Batch fast grading
        ///2) Skip to the next one on the popup
        ///3) Save and Skip to the next one on the popup

        //make user global so we can use the id
        global $USER;

        $mailinfo = optional_param('mailinfo', null, PARAM_BOOL);
        if (is_null($mailinfo)) {
            $mailinfo = get_user_preferences('problemstatement_mailinfo', 0);
        } else {
            set_user_preference('problemstatement_mailinfo', $mailinfo);
        }
        switch ($mode) {
            case 'grade':                         // We are in a popup window grading
	echo hi1;
                if ($submission = $this->process_feedback()) {
                    //IE needs proper header with encoding
                    print_header(get_string('feedback', 'problemstatement').':'.format_string($this->problemstatement->name));
                    print_heading(get_string('changessaved'));
                    print $this->update_main_listing($submission);
                }
                close_window();
                break;

            case 'single':                        // We are in a popup window displaying submission
	echo hi2;
                $this->display_submission();
                break;

            case 'all':                          // Main window, display everything
                $this->display_submissions();
                break;

            case 'fastgrade':
	echo hi4;
                ///do the fast grading stuff  - this process should work for all 3 subclasses

                $grading    = false;
                $commenting = false;
                $col        = false;
                if (isset($_POST['submissioncomment'])) {
                    $col = 'submissioncomment';
                    $commenting = true;
                }
                if (isset($_POST['menu'])) {
                    $col = 'menu';
                    $grading = true;
                }
                if (!$col) {
                    //both submissioncomment and grade columns collapsed..
                    $this->display_submissions();
                    break;
                }

                foreach ($_POST[$col] as $id => $unusedvalue){

                    $id = (int)$id; //clean parameter name

                    $this->process_outcomes($id);

                    if (!$submission = $this->get_submission($id)) {
                        $submission = $this->prepare_new_submission($id);
                        $newsubmission = true;
                    } else {
                        $newsubmission = false;
                    }
                    unset($submission->programtext);  // Don't need to update this.
                    unset($submission->langid);  // Don't need to update this.

                    //for fast grade, we need to check if any changes take place
                    $updatedb = false;

                    if ($grading) {
                        $grade = $_POST['menu'][$id];
                        $updatedb = $updatedb || ($submission->grade != $grade);
                        $submission->grade = $grade;
                    } else {
                        if (!$newsubmission) {
                            unset($submission->grade);  // Don't need to update this.
                        }
                    }
                    if ($commenting) {
                        $commentvalue = trim($_POST['submissioncomment'][$id]);
                        $updatedb = $updatedb || ($submission->submissioncomment != stripslashes($commentvalue));
                        $submission->submissioncomment = $commentvalue;
                    } else {
                        unset($submission->submissioncomment);  // Don't need to update this.
                    }

                    $submission->teacher    = $USER->id;
                    if ($updatedb) {
                        $submission->mailed = (int)(!$mailinfo);
                    }

                    $submission->timemarked = time();

                    //if it is not an update, we don't change the last modified time etc.
                    //this will also not write into database if no submissioncomment and grade is entered.

                    if ($updatedb){
                        if ($newsubmission) {
                            if (!isset($submission->submissioncomment)) {
                                $submission->submissioncomment = '';
                            }
                            if (!$sid = insert_record('problemstatement_submissions', $submission)) {
                                return false;
                            }
                            $submission->id = $sid;
                        } else {
                            if (!update_record('problemstatement_submissions', $submission)) {
                                return false;
                            }
                        }

                        // triger grade event
                        $this->update_grade($submission);

                        //add to log only if updating
                        add_to_log($this->course->id, 'problemstatement', 'update grades',
                                   'submissions.php?id='.$this->problemstatement->id.'&user='.$submission->userid,
                                   $submission->userid, $this->cm->id);
                    }

                }
                $message = notify(get_string('changessaved'), 'notifysuccess', 'center', true);

                $this->display_submissions($message);
                break;


            case 'next':
	echo hi5;
                /// We are currently in pop up, but we want to skip to next one without saving.
                ///    This turns out to be similar to a single case
                /// The URL used is for the next submission.

                $this->display_submission();
                break;

            case 'saveandnext':
	echo hi6;
                ///We are in pop up. save the current one and go to the next one.
                //first we save the current changes
                if ($submission = $this->process_feedback()) {
                    //print_heading(get_string('changessaved'));
                    $extra_javascript = $this->update_main_listing($submission);
                }

                //then we display the next submission
                $this->display_submission($extra_javascript);
                break;

            default:
                echo "something seriously is wrong!!";
                break;
        }
    }

    /**
    * Helper method updating the listing on the main script from popup using javascript
    *
    * @param $submission object The submission whose data is to be updated on the main page
    */
    /*function update_main_listing($submission) {
        global $SESSION, $CFG;

        $output = '';

        $perpage = get_user_preferences('problemstatement_perpage', 10);

        $quickgrade = get_user_preferences('problemstatement_quickgrade', 0);

        /// Run some Javascript to try and update the parent page
        $output .= '<script type="text/javascript">'."\n<!--\n";
        if (empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['submissioncomment'])) {
            if ($quickgrade){
                $output.= 'opener.document.getElementById("submissioncomment'.$submission->userid.'").value="'
                .trim($submission->submissioncomment).'";'."\n";
             } else {
                $output.= 'opener.document.getElementById("com'.$submission->userid.
                '").innerHTML="'.shorten_text(trim(strip_tags($submission->submissioncomment)), 15)."\";\n";
            }
        }

        if (empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['grade'])) {
            //echo optional_param('menuindex');
            if ($quickgrade){
                $output.= 'opener.document.getElementById("menumenu'.$submission->userid.
                '").selectedIndex="'.optional_param('menuindex', 0, PARAM_INT).'";'."\n";
            } else {
                $output.= 'opener.document.getElementById("g'.$submission->userid.'").innerHTML="'.
                $this->display_grade($submission->grade)."\";\n";
            }
        }
        //need to add student's problemstatements in there too.
        if (empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['timemodified']) &&
            $submission->timemodified) {
            $output.= 'opener.document.getElementById("ts'.$submission->userid.
                 '").innerHTML="'.addslashes_js($this->print_student_answer($submission->userid)).userdate($submission->timemodified)."\";\n";
        }

        if (empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['timemarked']) &&
            $submission->timemarked) {
            $output.= 'opener.document.getElementById("tt'.$submission->userid.
                 '").innerHTML="'.userdate($submission->timemarked)."\";\n";
        }

        if (empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['status'])) {
            $output.= 'opener.document.getElementById("up'.$submission->userid.'").className="s1";';
            $buttontext = get_string('update');
            $button = link_to_popup_window ('/mod/problemstatement/submissions.php?id='.$this->cm->id.'&amp;userid='.$submission->userid.'&amp;mode=single'.'&amp;offset='.(optional_param('offset', '', PARAM_INT)-1),
                      'grade'.$submission->userid, $buttontext, 450, 700, $buttontext, 'none', true, 'button'.$submission->userid);
            $output.= 'opener.document.getElementById("up'.$submission->userid.'").innerHTML="'.addslashes_js($button).'";';
        }

        $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, $submission->userid);

        if (empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['finalgrade'])) {
            $output.= 'opener.document.getElementById("finalgrade_'.$submission->userid.
            '").innerHTML="'.$grading_info->items[0]->grades[$submission->userid]->str_grade.'";'."\n";
        }

        if (!empty($CFG->enableoutcomes) and empty($SESSION->flextable['mod-problemstatement-submissions']->collapse['outcome'])) {

            if (!empty($grading_info->outcomes)) {
                foreach($grading_info->outcomes as $n=>$outcome) {
                    if ($outcome->grades[$submission->userid]->locked) {
                        continue;
                    }

                    if ($quickgrade){
                        $output.= 'opener.document.getElementById("outcome_'.$n.'_'.$submission->userid.
                        '").selectedIndex="'.$outcome->grades[$submission->userid]->grade.'";'."\n";

                    } else {
                        $options = make_grades_menu(-$outcome->scaleid);
                        $options[0] = get_string('nooutcome', 'grades');
                        $output.= 'opener.document.getElementById("outcome_'.$n.'_'.$submission->userid.'").innerHTML="'.$options[$outcome->grades[$submission->userid]->grade]."\";\n";
                    }

                }
            }
        }

        $output .= "\n-->\n</script>";
        return $output;
    }*/

    /**
     *  Return a grade in user-friendly form, whether it's a scale or not
     *
     * @param $grade
     * @return string User-friendly representation of grade
     */
    function display_grade($grade) {

        static $scalegrades = array();   // Cache scales for each problemstatement - they might have different scales!!

        if ($this->problemstatement->grade >= 0) {    // Normal number
            if ($grade == -1) {
                return '-';
            } else {
                return $grade.' / '.$this->problemstatement->grade;
            }

        } else {                                // Scale
            if (empty($scalegrades[$this->problemstatement->id])) {
                if ($scale = get_record('scale', 'id', -($this->problemstatement->grade))) {
                    $scalegrades[$this->problemstatement->id] = make_menu_from_list($scale->scale);
                } else {
                    return '-';
                }
            }
            if (isset($scalegrades[$this->problemstatement->id][$grade])) {
                return $scalegrades[$this->problemstatement->id][$grade];
            }
            return '-';
        }
    }

    /**
     *  Display a single submission, ready for grading on a popup window
     *
     * This default method prints the teacher info and submissioncomment box at the top and
     * the student info and submission at the bottom.
     * This method also fetches the necessary data in order to be able to
     * provide a "Next submission" button.
     * Calls preprocess_submission() to give problemstatement type plug-ins a chance
     * to process submissions before they are graded
     * This method gets its arguments from the page parameters userid and offset
     */
    function display_submission($extra_javascript = '') {

        global $CFG;
        require_once($CFG->libdir.'/gradelib.php');
        require_once($CFG->libdir.'/tablelib.php');

        $userid = required_param('userid', PARAM_INT);
        $offset = required_param('offset', PARAM_INT);//offset for where to start looking for student.

        if (!$user = get_record('user', 'id', $userid)) {
            error('No such user!');
        }

        if (!$submission = $this->get_submission($user->id)) {
            $submission = $this->prepare_new_submission($userid);
        }
        if ($submission->timemodified > $submission->timemarked) {
            $subtype = 'problemstatementnew';
        } else {
            $subtype = 'problemstatementold';
        }

        $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, array($user->id));
        $disabled = $grading_info->items[0]->grades[$userid]->locked || $grading_info->items[0]->grades[$userid]->overridden;

    /// construct SQL, using current offset to find the data of the next student
        $course     = $this->course;
        $problemstatement = $this->problemstatement;
        $cm         = $this->cm;
        $context    = get_context_instance(CONTEXT_MODULE, $cm->id);

        /// Get all ppl that can submit problemstatements

        $currentgroup = groups_get_activity_group($cm);
        if ($users = get_users_by_capability($context, 'mod/problemstatement:submit', 'u.id', '', '', '', $currentgroup, '', false)) {
            $users = array_keys($users);
        }

        // if groupmembersonly used, remove users who are not in any group
        if ($users and !empty($CFG->enablegroupings) and $cm->groupmembersonly) {
            if ($groupingusers = groups_get_grouping_members($cm->groupingid, 'u.id', 'u.id')) {
                $users = array_intersect($users, array_keys($groupingusers));
            }
        }

        $nextid = 0;

        if ($users) {
            $select = 'SELECT u.id, u.firstname, u.lastname, u.picture, u.imagealt,
                              s.id AS submissionid, s.grade, s.submissioncomment,
                              s.timemodified, s.timemarked,
                              COALESCE(SIGN(SIGN(s.timemarked) + SIGN(s.timemarked - s.timemodified)), 0) AS status ';
            $sql = 'FROM '.$CFG->prefix.'user u '.
                   'LEFT JOIN '.$CFG->prefix.'problemstatement_submissions s ON u.id = s.userid
                                                                      AND s.problemstatement = '.$this->problemstatement->id.' '.
                   'WHERE u.id IN ('.implode(',', $users).') ';

            if ($sort = flexible_table::get_sql_sort('mod-problemstatement-submissions')) {
                $sort = 'ORDER BY '.$sort.' ';
            }

            if (($auser = get_records_sql($select.$sql.$sort, $offset+1, 1)) !== false) {
                $nextuser = array_shift($auser);
            /// Calculate user status
                $nextuser->status = ($nextuser->timemarked > 0) && ($nextuser->timemarked >= $nextuser->timemodified);
                $nextid = $nextuser->id;
            }
        }

        print_header(get_string('feedback', 'problemstatement').':'.fullname($user, true).':'.format_string($this->problemstatement->name));

        /// Print any extra javascript needed for saveandnext
        echo $extra_javascript;

        ///SOme javascript to help with setting up >.>

        echo '<script type="text/javascript">'."\n";
        echo 'function setNext(){'."\n";
        echo 'document.getElementById(\'submitform\').mode.value=\'next\';'."\n";
        echo 'document.getElementById(\'submitform\').userid.value="'.$nextid.'";'."\n";
        echo '}'."\n";

        echo 'function saveNext(){'."\n";
        echo 'document.getElementById(\'submitform\').mode.value=\'saveandnext\';'."\n";
        echo 'document.getElementById(\'submitform\').userid.value="'.$nextid.'";'."\n";
        echo 'document.getElementById(\'submitform\').saveuserid.value="'.$userid.'";'."\n";
        echo 'document.getElementById(\'submitform\').menuindex.value = document.getElementById(\'submitform\').grade.selectedIndex;'."\n";
        echo '}'."\n";

        echo '</script>'."\n";
        echo '<table cellspacing="0" class="feedback '.$subtype.'" >';

        ///Start of teacher info row

        echo '<tr>';
        echo '<td class="picture teacher">';
        if ($submission->teacher) {
            $teacher = get_record('user', 'id', $submission->teacher);
        } else {
            global $USER;
            $teacher = $USER;
        }
        print_user_picture($teacher, $this->course->id, $teacher->picture);
        echo '</td>';
        echo '<td class="content">';
        echo '<form id="submitform" action="submissions.php" method="post">';
        echo '<div>'; // xhtml compatibility - invisiblefieldset was breaking layout here
        echo '<input type="hidden" name="offset" value="'.($offset+1).'" />';
        echo '<input type="hidden" name="userid" value="'.$userid.'" />';
        echo '<input type="hidden" name="id" value="'.$this->cm->id.'" />';
        echo '<input type="hidden" name="mode" value="grade" />';
        echo '<input type="hidden" name="menuindex" value="0" />';//selected menu index

        //new hidden field, initialized to -1.
        echo '<input type="hidden" name="saveuserid" value="-1" />';

        if ($submission->timemarked) {
            echo '<div class="from">';
            echo '<div class="fullname">'.fullname($teacher, true).'</div>';
            echo '<div class="time">'.userdate($submission->timemarked).'</div>';
            echo '</div>';
        }
        echo '<div class="grade"><label for="menugrade">'.get_string('grade').'</label> ';
        choose_from_menu(make_grades_menu($this->problemstatement->grade), 'grade', $submission->grade, get_string('nograde'), '', -1, false, $disabled);
        echo '</div>';

        echo '<div class="clearer"></div>';
        echo '<div class="finalgrade">'.get_string('finalgrade', 'grades').': '.$grading_info->items[0]->grades[$userid]->str_grade.'</div>';
        echo '<div class="clearer"></div>';

        if (!empty($CFG->enableoutcomes)) {
            foreach($grading_info->outcomes as $n=>$outcome) {
                echo '<div class="outcome"><label for="menuoutcome_'.$n.'">'.$outcome->name.'</label> ';
                $options = make_grades_menu(-$outcome->scaleid);
                if ($outcome->grades[$submission->userid]->locked) {
                    $options[0] = get_string('nooutcome', 'grades');
                    echo $options[$outcome->grades[$submission->userid]->grade];
                } else {
                    choose_from_menu($options, 'outcome_'.$n.'['.$userid.']', $outcome->grades[$submission->userid]->grade, get_string('nooutcome', 'grades'), '', 0, false, false, 0, 'menuoutcome_'.$n);
                }
                echo '</div>';
                echo '<div class="clearer"></div>';
            }
        }


        $this->preprocess_submission($submission);

        if ($disabled) {
            echo '<div class="disabledfeedback">'.$grading_info->items[0]->grades[$userid]->str_feedback.'</div>';

        } else {
            print_textarea($this->usehtmleditor, 14, 58, 0, 0, 'submissioncomment', $submission->submissioncomment, $this->course->id);
            if ($this->usehtmleditor) {
                echo '<input type="hidden" name="format" value="'.FORMAT_HTML.'" />';
            } else {
                echo '<div class="format">';
                choose_from_menu(format_text_menu(), "format", $submission->format, "");
                helpbutton("textformat", get_string("helpformatting"));
                echo '</div>';
            }
        }

        $lastmailinfo = get_user_preferences('problemstatement_mailinfo', 1) ? 'checked="checked"' : '';

        ///Print Buttons in Single View
        echo '<input type="hidden" name="mailinfo" value="0" />';
        echo '<input type="checkbox" id="mailinfo" name="mailinfo" value="1" '.$lastmailinfo.' /><label for="mailinfo">'.get_string('enableemailnotification','problemstatement').'</label>';
        echo '<div class="buttons">';
        echo '<input type="submit" name="submit" value="'.get_string('savechanges').'" onclick = "document.getElementById(\'submitform\').menuindex.value = document.getElementById(\'submitform\').grade.selectedIndex" />';
        echo '<input type="submit" name="cancel" value="'.get_string('cancel').'" />';
        //if there are more to be graded.
        if ($nextid) {
            echo '<input type="submit" name="saveandnext" value="'.get_string('saveandnext').'" onclick="saveNext()" />';
            echo '<input type="submit" name="next" value="'.get_string('next').'" onclick="setNext();" />';
        }
        echo '</div>';
        echo '</div></form>';

        $customfeedback = $this->custom_feedbackform($submission, true);
        if (!empty($customfeedback)) {
            echo $customfeedback;
        }

        echo '</td></tr>';

        ///End of teacher info row, Start of student info row
        echo '<tr>';
        echo '<td class="picture user">';
        print_user_picture($user, $this->course->id, $user->picture);
        echo '</td>';
        echo '<td class="topic">';
        echo '<div class="from">';
        echo '<div class="fullname">'.fullname($user, true).'</div>';
        if ($submission->timemodified) {
            echo '<div class="time">'.userdate($submission->timemodified).
                                     $this->display_lateness($submission->timemodified).'</div>';
        }
        echo '</div>';
        //$this->print_user_files($user->id);
        echo '</td>';
        echo '</tr>';

        ///End of student info row

        echo '</table>';

        if (!$disabled and $this->usehtmleditor) {
            use_html_editor();
        }

        print_footer('none');
    }

    /**
     *  Preprocess submission before grading
     *
     * Called by display_submission()
     * The default type does nothing here.
     * @param $submission object The submission object
     */
    /*function preprocess_submission(&$submission) {
    }*/

    /**
     *  Display all the submissions ready for grading
     */
    function display_submissions($message='') {
        global $CFG, $db, $USER;
        require_once($CFG->libdir.'/gradelib.php');

        /* first we check to see if the form has just been submitted
         * to request user_preference updates
         */

        /*if (isset($_POST['updatepref'])){
            $perpage = optional_param('perpage', 10, PARAM_INT);
            $perpage = ($perpage <= 0) ? 10 : $perpage ;
            set_user_preference('problemstatement_perpage', $perpage);
            set_user_preference('problemstatement_quickgrade', optional_param('quickgrade', 0, PARAM_BOOL));
        }*/

        /* next we get perpage and quickgrade (allow quick grade) params
         * from database
         */
        $perpage    = get_user_preferences('problemstatement_perpage', 10);

        $quickgrade = get_user_preferences('problemstatement_quickgrade', 0);

        $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id);

        if (!empty($CFG->enableoutcomes) and !empty($grading_info->outcomes)) {
            $uses_outcomes = true;
        } else {
            $uses_outcomes = false;
        }

        $page    = optional_param('page', 0, PARAM_INT);
        $strsaveallfeedback = get_string('saveallfeedback', 'problemstatement');

    /// Some shortcuts to make the code read better

        $course     = $this->course;
        $problemstatement = $this->problemstatement;
        $cm         = $this->cm;

        $tabindex = 1; //tabindex for quick grading tabbing; Not working for dropdowns yet

        add_to_log($course->id, 'problemstatement', 'view submission', 'submissions.php?id='.$this->problemstatement->id, $this->problemstatement->id, $this->cm->id);

        $navigation = build_navigation($this->strsubmissions, $this->cm);
        print_header_simple(format_string($this->problemstatement->name,true), "", $navigation,
                '', '', true, update_module_button($cm->id, $course->id, $this->strproblemstatement), navmenu($course, $cm));

        $course_context = get_context_instance(CONTEXT_COURSE, $course->id);
        if (has_capability('gradereport/grader:view', $course_context) && has_capability('moodle/grade:viewall', $course_context)) {
            echo '<div class="allcoursegrades"><a href="' . $CFG->wwwroot . '/grade/report/grader/index.php?id=' . $course->id . '">'
                . get_string('seeallcoursegrades', 'grades') . '</a></div>';
        }

        if (!empty($message)) {
            echo $message;   // display messages here if any
        }

        $context = get_context_instance(CONTEXT_MODULE, $cm->id);

    /// Check to see if groups are being used in this problemstatement

        /// find out current groups mode
        $groupmode = groups_get_activity_groupmode($cm);
        $currentgroup = groups_get_activity_group($cm, true);
        groups_print_activity_menu($cm, 'submissions.php?id=' . $this->cm->id);

        /// Get all ppl that are allowed to submit problemstatements
        if ($users = get_users_by_capability($context, 'mod/problemstatement:submit', 'u.id', '', '', '', $currentgroup, '', false)) {
            $users = array_keys($users);
        }

        // if groupmembersonly used, remove users who are not in any group
        if ($users and !empty($CFG->enablegroupings) and $cm->groupmembersonly) {
            if ($groupingusers = groups_get_grouping_members($cm->groupingid, 'u.id', 'u.id')) {
                $users = array_intersect($users, array_keys($groupingusers));
            }
        }

        $tablecolumns = array('picture', 'fullname', 'grade', 'submissioncomment', 'timemodified', 'timemarked', 'status', 'finalgrade');
        if ($uses_outcomes) {
            $tablecolumns[] = 'outcome'; // no sorting based on outcomes column
        }

        $tableheaders = array('',
                              get_string('fullname'),
                              get_string('grade'),
                              get_string('comment', 'problemstatement'),
                              get_string('lastmodified').' ('.$course->student.')',
                              get_string('lastmodified').' ('.$course->teacher.')',
                              get_string('status'),
                              get_string('finalgrade', 'grades'));
        if ($uses_outcomes) {
            $tableheaders[] = get_string('outcome', 'grades');
        }

        require_once($CFG->libdir.'/tablelib.php');
        $table = new flexible_table('mod-problemstatement-submissions');

        $table->define_columns($tablecolumns);
        $table->define_headers($tableheaders);
        $table->define_baseurl($CFG->wwwroot.'/mod/problemstatement/submissions.php?id='.$this->cm->id.'&amp;currentgroup='.$currentgroup);

        $table->sortable(true, 'lastname');//sorted by lastname by default
        $table->collapsible(true);
        $table->initialbars(true);

        $table->column_suppress('picture');
        $table->column_suppress('fullname');

        $table->column_class('picture', 'picture');
        $table->column_class('fullname', 'fullname');
        $table->column_class('grade', 'grade');
        $table->column_class('submissioncomment', 'comment');
        $table->column_class('timemodified', 'timemodified');
        $table->column_class('timemarked', 'timemarked');
        $table->column_class('status', 'status');
        $table->column_class('finalgrade', 'finalgrade');
        if ($uses_outcomes) {
            $table->column_class('outcome', 'outcome');
        }

        $table->set_attribute('cellspacing', '0');
        $table->set_attribute('id', 'attempts');
        $table->set_attribute('class', 'submissions');
        $table->set_attribute('width', '100%');
        //$table->set_attribute('align', 'center');

        $table->no_sorting('finalgrade');
        $table->no_sorting('outcome');

        // Start working -- this is necessary as soon as the niceties are over
        $table->setup();

        if (empty($users)) {
            print_heading(get_string('nosubmitusers','problemstatement'));
            return true;
        }

    /// Construct the SQL

        if ($where = $table->get_sql_where()) {
            $where .= ' AND ';
        }

        if ($sort = $table->get_sql_sort()) {
            $sort = ' ORDER BY '.$sort;
        }

        $select = 'SELECT u.id, u.firstname, u.lastname, u.picture, u.imagealt,
                          s.id AS submissionid, s.grade, s.submissioncomment,
                          s.timemodified,
                          s.processed, s.succeeded ';
        $sql = 'FROM '.$CFG->prefix.'user u '.
               'LEFT JOIN '.$CFG->prefix.'problemstatement_submissions s ON u.id = s.userid
                                                                  AND s.problemstatement = '.$this->problemstatement->id.' '.
               'WHERE '.$where.'u.id IN ('.implode(',',$users).') ';

        $table->pagesize($perpage, count($users));

        ///offset used to calculate index of student in that particular query, needed for the pop up to know who's next
        $offset = $page * $perpage;

        $strupdate = get_string('update');
        $strgrade  = get_string('grade');
        $grademenu = make_grades_menu($this->problemstatement->grade);
//echo $select.$sql.$sort;
        if (($ausers = get_records_sql($select.$sql.$sort, $table->get_page_start(), $table->get_page_size())) !== false) {
            $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, array_keys($ausers));
            foreach ($ausers as $auser) {
                $final_grade = $grading_info->items[0]->grades[$auser->id];
                $grademax = $grading_info->items[0]->gradprocess_feedbackemax;
                $final_grade->formatted_grade = round($final_grade->grade,2) .' / ' . round($grademax,2);
                $locked_overridden = 'locked';
                if ($final_grade->overridden) {
                    $locked_overridden = 'overridden';
                }

            /// Calculate user status
                //$auser->status = $auser->status;//($auser->timemarked > 0) && ($auser->timemarked >= $auser->timemodified);
                $picture = print_user_picture($auser, $course->id, $auser->picture, false, true);

                if (empty($auser->submissionid)) {
                    $auser->grade = -1; //no submission yet
                }

                if (!empty($auser->submissionid)) {
                ///Prints student answer and student modified date
                ///attach file or print link to student answer, depending on the type of the problemstatement.
                ///Refer to print_student_answer in inherited classes.
                    if ($auser->timemodified > 0) {
                        $studentmodified = '<div id="ts'.$auser->id.'">'.$this->print_student_answer($auser->id)
                                         . userdate($auser->timemodified).'</div>';
                    } else {
                        $studentmodified = '<div id="ts'.$auser->id.'">&nbsp;</div>';
                    }
                ///Print grade, dropdown or text
                    if ($auser->timemarked > 0) {
                        $teachermodified = '<div id="tt'.$auser->id.'">'.userdate($auser->timemarked).'</div>';

                        if ($final_grade->locked or $final_grade->overridden) {
                            $grade = '<div id="g'.$auser->id.'" class="'. $locked_overridden .'">'.$final_grade->formatted_grade.'</div>';
                        } else if ($quickgrade) {
                            $menu = choose_from_menu(make_grades_menu($this->problemstatement->grade),
                                                     'menu['.$auser->id.']', $auser->grade,
                                                     get_string('nograde'),'',-1,true,false,$tabindex++);
                            $grade = '<div id="g'.$auser->id.'">'. $menu .'</div>';
                        } else {
                            $grade = '<div id="g'.$auser->id.'">'.$this->display_grade($auser->grade).'</div>';
                        }

                    } else {
                        $teachermodified = '<div id="tt'.$auser->id.'">&nbsp;</div>';
                        if ($final_grade->locked or $final_grade->overridden) {
                            $grade = '<div id="g'.$auser->id.'" class="'. $locked_overridden .'">'.$final_grade->formatted_grade.'</div>';
                        } else if ($quickgrade) {
                            $menu = choose_from_menu(make_grades_menu($this->problemstatement->grade),
                                                     'menprocess_feedbacku['.$auser->id.']', $auser->grade,
                                                     get_string('nograde'),'',-1,true,false,$tabindex++);
                            $grade = '<div id="g'.$auser->id.'">'.$menu.'</div>';
                        } else {
                            $grade = '<div id="g'.$auser->id.'">'.$this->display_grade($auser->grade).'</div>';
                        }
                    }
                ///Print Comment
                    if ($final_grade->locked or $final_grade->overridden) {
                        $comment = '<div id="com'.$auser->id.'">'.shorten_text(strip_tags($final_grade->str_feedback),15).'</div>';

                    } else if ($quickgrade) {
                        $comment = '<div id="com'.$auser->id.'">'
                                 . '<textarea tabindex="'.$tabindex++.'" name="submissioncomment['.$auser->id.']" id="submissioncomment'
                                 . $auser->id.'" rows="2" cols="20">'.($auser->submissioncomment).'</textarea></div>';
                    } else {
		$msg = ""; $color="#000000";
		switch ($auser->processed) {
			case "0": $msg .= "unprocessed"; $color = "#AAAA00"; break;
			case "1": switch ($auser->succeeded) {
						case "4": ; //timeout
						case "6": ; //runtimeerror
						case "2": ; //compilationerror
						case "5": ; //memoryout
						case "0": $msg .= "failed"; $color = "#AA0000"; break;
						case "1": $msg .= "passed"; $color = "#00AA00"; break;
						case "3": $msg .= "internalerror"; $color = "#0000AA"; break;
					} break;
			case "2": $msg .= "waiting"; $color = "#00AAAA"; break;
		}
//$comment='<div id="com'.$auser->id.'">'.shorten_text(strip_tags($auser->submissioncomment),15).'</div>';
$comment = '<div id="com'.$auser->id.'">'.

'<div class="files"><font color="'.$color.'"><strong>'.
$msg.
/*'<img src="'.$CFG->pixpath.'/f/html.gif" class="icon" alt="html" />'.
link_to_popup_window(
'/mod/problemstatement/file.php?id='.$this->cm->id.'&amp;userid='.$auser->id,
'file'.$auser->id,
shorten_text(trim(strip_tags(format_text($auser->submissioncomment,0))), 15),
450, 580, get_string('submissioncomment', 'problemstatement'), 'none', true).
*/
'</strong></font></div>'.

'</div>';
                    }
                } else {
                    $studentmodified = '<div id="ts'.$auser->id.'">&nbsp;</div>';
                    $teachermodified = '<div id="tt'.$auser->id.'">&nbsp;</div>';
                    $status          = '<div id="st'.$auser->id.'">&nbsp;</div>';

                    if ($final_grade->locked or $final_grade->overridden) {
                        $grade = '<div id="g'.$auser->id.'">'.$final_grade->formatted_grade . '</div>';
                    } else if ($quickgrade) {   // allow editing
                        $menu = choose_from_menu(make_grades_menu($this->problemstatement->grade),
                                                 'menu['.$auser->id.']', $auser->grade,
                                                 get_string('nograde'),'',-1,true,false,$tabindex++);
                        $grade = '<div id="g'.$auser->id.'">'.$menu.'</div>';
                    } else {
                        $grade = '<div id="g'.$auser->id.'">-</div>';
                    }

                    if ($final_grade->locked or $final_grade->overridden) {
                        $comment = '<div id="com'.$auser->id.'">'.$final_grade->str_feedback.'</div>';
                    } else if ($quickgrade) {
                        $comment = '<div id="com'.$auser->id.'">'
                                 . '<textarea tabindex="'.$tabindex++.'" name="submissioncomment['.$auser->id.']" id="submissioncomment'
                                 . $auser->id.'" rows="2" cols="20">'.($auser->submissioncomment).'</textarea></div>';
                    } else {
                        $comment = '<div id="com'.$auser->id.'">&nbsp;</div>';
                    }
                }

                if (empty($auser->processed)) { /// Confirm we have exclusively 0 or 1
                    //$auser->status = 0;
                } else {
                    //$auser->status = 1;
                }

                $buttontext = ($auser->processed == 1) ? $strupdate : $strgrade;

                ///No more buttons, we use popups ;-).
                $popup_url = '/mod/problemstatement/submissions.php?id='.$this->cm->id
                           . '&amp;userid='.$auser->id.'&amp;mode=single'.'&amp;offset='.$offset++;
                $button = link_to_popup_window ($popup_url, 'grade'.$auser->id, $buttontext, 600, 780,
                                                $buttontext, 'none', true, 'button'.$auser->id);

                $status  = '<div id="up'.$auser->id.'" class="s'.$auser->processed.'">'.$button.'</div>';

                $finalgrade = '<span id="finalgrade_'.$auser->id.'">'.$final_grade->str_grade.'</span>';

                $outcomes = '';

                if ($uses_outcomes) {

                    foreach($grading_info->outcomes as $n=>$outcome) {
                        $outcomes .= '<div class="outcome"><label>'.$outcome->name.'</label>';
                        $options = make_grades_menu(-$outcome->scaleid);

                        if ($outcome->grades[$auser->id]->locked or !$quickgrade) {
                            $options[0] = get_string('nooutcome', 'grades');
                            $outcomes .= ': <span id="outcome_'.$n.'_'.$auser->id.'">'.$options[$outcome->grades[$auser->id]->grade].'</span>';
                        } else {
                            $outcomes .= ' ';
                            $outcomes .= choose_from_menu($options, 'outcome_'.$n.'['.$auser->id.']',
                                        $outcome->grades[$auser->id]->grade, get_string('nooutcome', 'grades'), '', 0, true, false, 0, 'outcome_'.$n.'_'.$auser->id);
                        }
                        $outcomes .= '</div>';
                    }
                }

				$userlink = '<a href="' . $CFG->wwwroot . '/user/view.php?id=' . $auser->id . '&amp;course=' . $course->id . '">' . fullname($auser) . '</a>';
                $row = array($picture, $userlink, $grade, $comment, $studentmodified, $teachermodified, $status, $finalgrade);
                if ($uses_outcomes) {
                    $row[] = $outcomes;
                }

                $table->add_data($row);
            }
        }

        /// Print quickgrade form around the table
        if ($quickgrade){
            echo '<form action="submissions.php" id="fastg" method="post">';
            echo '<div>';
            echo '<input type="hidden" name="id" value="'.$this->cm->id.'" />';
            echo '<input type="hidden" name="mode" value="fastgrade" />';
            echo '<input type="hidden" name="page" value="'.$page.'" />';
            echo '</div>';
        }

        $table->print_html();  /// Print the whole table

        if ($quickgrade){
            $lastmailinfo = get_user_preferences('problemstatement_mailinfo', 1) ? 'checked="checked"' : '';
            echo '<div class="fgcontrols">';
            echo '<div class="emailnotification">';
            echo '<label for="mailinfo">'.get_string('enableemailnotification','problemstatement').'</label>';
            echo '<input type="hidden" name="mailinfo" value="0" />';
            echo '<input type="checkbox" id="mailinfo" name="mailinfo" value="1" '.$lastmailinfo.' />';
            helpbutton('emailnotification', get_string('enableemailnotification', 'problemstatement'), 'problemstatement').'</p></div>';
            echo '</div>';
            echo '<div class="fastgbutton"><input type="submit" name="fastg" value="'.get_string('saveallfeedback', 'problemstatement').'" /></div>';
            echo '</div>';
            echo '</form>';
        }
        /// End of fast grading form

        /// Mini form for setting user preference
        echo '<div class="qgprefs">';
        echo '<form id="options" action="submissions.php?id='.$this->cm->id.'" method="post"><div>';
        echo '<input type="hidden" name="updatepref" value="1" />';
        echo '<table id="optiontable">';
        echo '<tr><td>';
        echo '<label for="perpage">'.get_string('pagesize','problemstatement').'</label>';
        echo '</td>';
        echo '<td>';
        echo '<input type="text" id="perpage" name="perpage" size="1" value="'.$perpage.'" />';
        helpbutton('pagesize', get_string('pagesize','problemstatement'), 'problemstatement');
        echo '</td></tr>';
        echo '<tr><td>';
        echo '<label for="quickgrade">'.get_string('quickgrade','problemstatement').'</label>';
        echo '</td>';
        echo '<td>';
        $checked = $quickgrade ? 'checked="checked"' : '';
        echo '<input type="checkbox" id="quickgrade" name="quickgrade" value="1" '.$checked.' />';
        helpbutton('quickgrade', get_string('quickgrade', 'problemstatement'), 'problemstatement').'</p></div>';
        echo '</td></tr>';
        echo '<tr><td colspan="2">';
        echo '<input type="submit" value="'.get_string('savepreferences').'" />';
        echo '</td></tr></table>';
        echo '</div></form></div>';
        ///End of mini form
        print_footer($this->course);
    }

    /**
     *  Process teacher feedback submission
     *
     * This is called by submissions() when a grading even has taken place.
     * It gets its data from the submitted form.
     * @return object The updated submission object
     */
    /*function process_feedback() {
        global $CFG, $USER;
        require_once($CFG->libdir.'/gradelib.php');

        if (!$feedback = data_submitted()) {      // No incoming data?
            return false;
        }

        ///For save and next, we need to know the userid to save, and the userid to go
        ///We use a new hidden field in the form, and set it to -1. If it's set, we use this
        ///as the userid to store
        if ((int)$feedback->saveuserid !== -1){
            $feedback->userid = $feedback->saveuserid;
        }

        if (!empty($feedback->cancel)) {          // User hit cancel button
            return false;
        }

        $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, $feedback->userid);

        // store outcomes if needed
        $this->process_outcomes($feedback->userid);

        $submission = $this->get_submission($feedback->userid, true);  // Get or make one

        if (!$grading_info->items[0]->grades[$feedback->userid]->locked and
            !$grading_info->items[0]->grades[$feedback->userid]->overridden) {

            $submission->grade      = $feedback->grade;
            $submission->submissioncomment    = $feedback->submissioncomment;
            $submission->format     = $feedback->format;
            $submission->teacher    = $USER->id;
            $mailinfo = get_user_preferences('problemstatement_mailinfo', 0);
            if (!$mailinfo) {
                $submission->mailed = 1;       // treat as already mailed
            } else {
                $submission->mailed = 0;       // Make sure mail goes out (again, even)
            }
            $submission->timemarked = time();

            unset($submission->data1);  // Don't need to update this.
            unset($submission->data2);  // Don't need to update this.

            if (empty($submission->timemodified)) {   // eg for offline problemstatements
                // $submission->timemodified = time();
            }

            if (! update_record('problemstatement_submissions', $submission)) {
                return false;
            }

            // triger grade event
            $this->update_grade($submission);

            add_to_log($this->course->id, 'problemstatement', 'update grades',
                       'submissions.php?id='.$this->problemstatement->id.'&user='.$feedback->userid, $feedback->userid, $this->cm->id);
        }

        return $submission;

    }*/

    /*function process_outcomes($userid) {
        global $CFG, $USER;

        if (empty($CFG->enableoutcomes)) {
            return;
        }

        require_once($CFG->libdir.'/gradelib.php');

        if (!$formdata = data_submitted()) {
            return;
        }

        $data = array();
        $grading_info = grade_get_grades($this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, $userid);

        if (!empty($grading_info->outcomes)) {
            foreach($grading_info->outcomes as $n=>$old) {
                $name = 'outcome_'.$n;
                if (isset($formdata->{$name}[$userid]) and $old->grades[$userid]->grade != $formdata->{$name}[$userid]) {
                    $data[$n] = $formdata->{$name}[$userid];
                }
            }
        }
        if (count($data) > 0) {
            grade_update_outcomes('mod/problemstatement', $this->course->id, 'mod', 'problemstatement', $this->problemstatement->id, $userid, $data);
        }

    }*/

    /**
     * Load the submission object for a particular user
     *
     * @param $userid int The id of the user whose submission we want or 0 in which case USER->id is used
     * @param $createnew boolean optional Defaults to false. If set to true a new submission object will be created in the database
     * @param bool $teachermodified student submission set if false
     * @return object The submission
     */
    function get_submission($userid=0, $createnew=false/*, $teachermodified=false*/) {
        global $USER;
        if (empty($userid)) {
            $userid = $USER->id;
        }
        //$submission = get_record('problemstatement_submissions', 'problemstatement', $this->problemstatement->id, 'userid', $userid);
		
		$submission = get_record_select('problemstatement_submissions', "problemstatement={$this->problemstatement->id} and userid={$userid} order by timecreated desc");
        if ($submission || !$createnew) {
            return $submission;
        }
        $newsubmission = $this->prepare_new_submission($userid/*, $teachermodified*/);
        if (!insert_record("problemstatement_submissions", $newsubmission)) {
            error("Could not insert a new empty submission");
        }
        return get_record('problemstatement_submissions', 'problemstatement', $this->problemstatement->id, 'userid', $userid);
    }

    /**
     * Instantiates a new submission object for a given user
     *
     * Sets the problemstatement, userid and times, everything else is set to default values.
     * @param $userid int The userid for which we want a submission object
     * @param bool $teachermodified student submission set if false
     * @return object The submission
     */
    function prepare_new_submission($userid/*, $teachermodified=false*/) {
        $submission = new Object;
        $submission->problemstatement	= $this->problemstatement->id;
        $submission->userid		= $userid;
        //$submission->timecreated  = time();
        $submission->timecreated = '';
        // teachers should not be modifying modified date, except offline problemstatements
        //if ($teachermodified) {
            $submission->timemodified = 0;
       // } else {
            //$submission->timemodified = $submission->timecreated;
       // }
        $submission->grade		= -1;
        $submission->submissioncomment	= '';
        $submission->programtext	= '';
        $submission->langid		= 0;
        $submission->processed		= 0;
	$submission->succeeded		= 0;
        return $submission;
    }

    /**
     * Return all problemstatement submissions by ENROLLED students (even empty)
     *
     * @param $sort string optional field names for the ORDER BY in the sql query
     * @param $dir string optional specifying the sort direction, defaults to DESC
     * @return array The submission objects indexed by id
     */
    /*function get_submissions($sort='', $dir='DESC') {
        return problemstatement_get_all_submissions($this->problemstatement, $sort, $dir);
    }*/

    /**
     * Counts all real problemstatement submissions by ENROLLED students (not empty ones)
     *
     * @param $groupid int optional If nonzero then count is restricted to this group
     * @return int The number of submissions
     */
    function count_real_submissions($groupid=0) {
        //return problemstatement_count_real_submissions($this->cm, $groupid);
    global $CFG;

    $context = get_context_instance(CONTEXT_MODULE, $this->cm->id);

    // this is all the users with this capability set, in this context or higher
    if ($users = get_users_by_capability($context, 'mod/problemstatement:submit', 'u.id', '', '', '', $groupid, '', false)) {
        $users = array_keys($users);
    }

    // if groupmembersonly used, remove users who are not in any group
    if ($users and !empty($CFG->enablegroupings) and $this->cm->groupmembersonly) {
        if ($groupingusers = groups_get_grouping_members($this->cm->groupingid, 'u.id', 'u.id')) {
            $users = array_intersect($users, array_keys($groupingusers));
        }
    }

    if (empty($users)) {
        return 0;
    }

    $userlists = implode(',', $users);/*
	var_dump($userlists);
	echo $cm->instance;
	echo "SELECT COUNT('x')
                                FROM {$CFG->prefix}problemstatement_submissions
                               WHERE problemstatement = ".$this->cm->instance." AND
                                     timemodified > 0 AND
                                     userid IN ($userlists)";*/
    return count_records_sql("SELECT COUNT('x')
                                FROM {$CFG->prefix}problemstatement_submissions
                               WHERE problemstatement = ".$this->cm->instance." AND
                                     timemodified > 0 AND
                                     userid IN ($userlists)");
    }

    /**
     * Alerts teachers by email of new or changed problemstatements that need grading
     *
     * First checks whether the option to email teachers is set for this problemstatement.
     * Sends an email to ALL teachers in the course (or in the group if using separate groups).
     * Uses the methods email_teachers_text() and email_teachers_html() to construct the content.
     * @param $submission object The submission that has changed
     */
    /*function email_teachers($submission) {
        global $CFG;

        if (empty($this->problemstatement->emailteachers)) {          // No need to do anything
            return;
        }

        $user = get_record('user', 'id', $submission->userid);

        if ($teachers = $this->get_graders($user)) {

            $strproblemstatements = get_string('modulenameplural', 'problemstatement');
            $strproblemstatement  = get_string('modulename', 'problemstatement');
            $strsubmitted  = get_string('submitted', 'problemstatement');

            foreach ($teachers as $teacher) {
                $info = new object();
                $info->username = fullname($user, true);
                $info->problemstatement = format_string($this->problemstatement->name,true);
                $info->url = $CFG->wwwroot.'/mod/problemstatement/submissions.php?id='.$this->cm->id;

                $postsubject = $strsubmitted.': '.$info->username.' -> '.$this->problemstatement->name;
                $posttext = $this->email_teachers_text($info);
                $posthtml = ($teacher->mailformat == 1) ? $this->email_teachers_html($info) : '';

                @email_to_user($teacher, $user, $postsubject, $posttext, $posthtml);  // If it fails, oh well, too bad.
            }
        }
    }*/

    /**
     * Returns a list of teachers that should be grading given submission
     */
    /*function get_graders($user) {
        //potential graders
        $potgraders = get_users_by_capability($this->context, 'mod/problemstatement:grade', '', '', '', '', '', '', false, false);

        $graders = array();
        if (groups_get_activity_groupmode($this->cm) == SEPARATEGROUPS) {   // Separate groups are being used
            if ($groups = groups_get_all_groups($this->course->id, $user->id)) {  // Try to find all groups
                foreach ($groups as $group) {
                    foreach ($potgraders as $t) {
                        if ($t->id == $user->id) {
                            continue; // do not send self
                        }
                        if (groups_is_member($group->id, $t->id)) {
                            $graders[$t->id] = $t;
                        }
                    }
                }
            } else {
                // user not in group, try to find graders without group
                foreach ($potgraders as $t) {
                    if ($t->id == $user->id) {
                        continue; // do not send self
                    }
                    if (!groups_get_all_groups($this->course->id, $t->id)) { //ugly hack
                        $graders[$t->id] = $t;
                    }
                }
            }
        } else {
            foreach ($potgraders as $t) {
                if ($t->id == $user->id) {
                    continue; // do not send self
                }
                $graders[$t->id] = $t;
            }
        }
        return $graders;
    }*/

    /**
     * Creates the text content for emails to teachers
     *
     * @param $info object The info used by the 'emailteachermail' language string
     * @return string
     */
    /*function email_teachers_text($info) {
        $posttext  = format_string($this->course->shortname).' -> '.$this->strproblemstatements.' -> '.
                     format_string($this->problemstatement->name)."\n";
        $posttext .= '---------------------------------------------------------------------'."\n";
        $posttext .= get_string("emailteachermail", "problemstatement", $info)."\n";
        $posttext .= "\n---------------------------------------------------------------------\n";
        return $posttext;
    }*/

     /**
     * Creates the html content for emails to teachers
     *
     * @param $info object The info used by the 'emailteachermailhtml' language string
     * @return string
     */
    /*function email_teachers_html($info) {
        global $CFG;
        $posthtml  = '<p><font face="sans-serif">'.
                     '<a href="'.$CFG->wwwroot.'/course/view.php?id='.$this->course->id.'">'.format_string($this->course->shortname).'</a> ->'.
                     '<a href="'.$CFG->wwwroot.'/mod/problemstatement/index.php?id='.$this->course->id.'">'.$this->strproblemstatements.'</a> ->'.
                     '<a href="'.$CFG->wwwroot.'/mod/problemstatement/view.php?id='.$this->cm->id.'">'.format_string($this->problemstatement->name).'</a></font></p>';
        $posthtml .= '<hr /><font face="sans-serif">';
        $posthtml .= '<p>'.get_string('emailteachermailhtml', 'problemstatement', $info).'</p>';
        $posthtml .= '</font><hr />';
        return $posthtml;
    }*/

    /**
     * Produces a list of links to the files uploaded by a user
     *
     * @param $userid int optional id of the user. If 0 then $USER->id is used.
     * @param $return boolean optional defaults to false. If true the list is returned rather than printed
     * @return string optional
     */
    /*function print_user_files($userid=0, $return=false) {
        global $CFG, $USER;

        if (!$userid) {
            if (!isloggedin()) {
                return '';
            }
            $userid = $USER->id;
        }

        $filearea = $this->file_area_name($userid);

        $output = '';

        if ($basedir = $this->file_area($userid)) {
            if ($files = get_directory_list($basedir)) {
                require_once($CFG->libdir.'/filelib.php');
                foreach ($files as $key => $file) {

                    $icon = mimeinfo('icon', $file);
                    $ffurl = get_file_url("$filearea/$file", array('forcedownload'=>1));

                    $output .= '<img src="'.$CFG->pixpath.'/f/'.$icon.'" class="icon" alt="'.$icon.'" />'.
                            '<a href="'.$ffurl.'" >'.$file.'</a><br />';
                }
            }
        }

        $output = '<div class="files">'.$output.'</div>';

        if ($return) {
            return $output;
        }
        echo $output;
    }*/

    /**
     * Count the files uploaded by a given user
     *
     * @param $userid int The user id
     * @return int
     */
    /*function count_user_files($userid) {
        global $CFG;

        $filearea = $this->file_area_name($userid);

        if ( is_dir($CFG->dataroot.'/'.$filearea) && $basedir = $this->file_area($userid)) {
            if ($files = get_directory_list($basedir)) {
                return count($files);
            }
        }
        return 0;
    }*/

    /**
     * Creates a directory file name, suitable for make_upload_directory()
     *
     * @param $userid int The user id
     * @return string path to file area
     */
    /*function file_area_name($userid) {
        global $CFG;

        return $this->course->id.'/'.$CFG->moddata.'/problemstatement/'.$this->problemstatement->id.'/'.$userid;
    }*/

    /**
     * Makes an upload directory
     *
     * @param $userid int The user id
     * @return string path to file area.
     */
    /*function file_area($userid) {
        return make_upload_directory( $this->file_area_name($userid) );
    }*/

    /**
     * Returns true if the student is allowed to submit
     *
     * Checks that the problemstatement has started and, if the option to prevent late
     * submissions is set, also checks that the problemstatement has not yet closed.
     * @return boolean
     */
    function isopen() {
        $time = time();
        if ($this->problemstatement->preventlate && $this->problemstatement->timedue) {
            return ($this->problemstatement->timeavailable <= $time && $time <= $this->problemstatement->timedue);
        } else {
            return ($this->problemstatement->timeavailable <= $time);
        }
    }


    /**
     * Return true if is set description is hidden till available date
     *
     * This is needed by calendar so that hidden descriptions do not
     * come up in upcoming events.
     *
     * Check that description is hidden till available date
     * By default return false
     * problemstatements types should implement this method if needed
     * @return boolen
     */
    /*function description_is_hidden() {
        return false;
    }*/

    /**
     * Return an outline of the user's interaction with the problemstatement
     *
     * The default method prints the grade and timemodified
     * @param $user object
     * @return object with properties ->info and ->time
     */
    function user_outline($user) {
        if ($submission = $this->get_submission($user->id)) {

            $result = new object();
            $result->info = get_string('grade').': '.$this->display_grade($submission->grade);
            $result->time = $submission->timemodified;
            return $result;
        }
        return NULL;
    }

    /**
     * Print complete information about the user's interaction with the problemstatement
     *
     * @param $user object
     */
    function user_complete($user) {
        if ($submission = $this->get_submission($user->id)) {
            if ($basedir = $this->file_area($user->id)) {
                if ($files = get_directory_list($basedir)) {
                    $countfiles = count($files)." ".get_string("uploadedfiles", "problemstatement");
                    foreach ($files as $file) {
                        $countfiles .= "; $file";
                    }
                }
            }

            print_simple_box_start();
            echo get_string("lastmodified").": ";
            echo userdate($submission->timemodified);
            echo $this->display_lateness($submission->timemodified);

            //$this->print_user_files($user->id);

            echo '<br />';

            if (empty($submission->timemarked)) {
                print_string("notgradedyet", "problemstatement");
            } else {
                $this->view_feedback($submission);
            }

            print_simple_box_end();

        } else {
            print_string("notsubmittedyet", "problemstatement");
        }
    }

    /**
     * Return a string indicating how late a submission is
     *
     * @param $timesubmitted int
     * @return string
     */
    /*function display_lateness($timesubmitted) {
        return problemstatement_display_lateness($timesubmitted, $this->problemstatement->timedue);
    }*/

    /**
     * Empty method stub for all delete actions.
     */
    /*function delete() {
        //nothing by default
        redirect('view.php?id='.$this->cm->id);
    }*/

    /**
     * Empty custom feedback grading form.
     */
    /*function custom_feedbackform($submission, $return=false) {
        //nothing by default
        return '';
    }*/

    /**
     * Add a get_coursemodule_info function in case any problemstatement type wants to add 'extra' information
     * for the course (see resource).
     *
     * Given a course_module object, this function returns any "extra" information that may be needed
     * when printing this activity in a course listing.  See get_array_of_activities() in course/lib.php.
     *
     * @param $coursemodule object The coursemodule object (record).
     * @return object An object on information that the coures will know about (most noticeably, an icon).
     *
     */
    /*function get_coursemodule_info($coursemodule) {
        return false;
    }*/

    /**
     * Plugin cron method - do not use $this here, create new problemstatement instances if needed.
     * @return void
     */
    /*function cron() {
        //no plugin cron by default - override if needed
    }*/

    /**
     * Reset all submissions
     */
    /*function reset_userdata($data) {
        global $CFG;
        require_once($CFG->libdir.'/filelib.php');

        if (!count_records('problemstatement', 'course', $data->courseid, 'problemstatementtype', $this->type)) {
            return array(); // no problemstatements of this type present
        }

        $componentstr = get_string('modulenameplural', 'problemstatement');
        $status = array();

        $typestr = get_string('type'.$this->type, 'problemstatement');

        if (!empty($data->reset_problemstatement_submissions)) {
            $problemstatementssql = "SELECT a.id
                                 FROM {$CFG->prefix}problemstatement a
                                WHERE a.course={$data->courseid} AND a.problemstatementtype='{$this->type}'";

            delete_records_select('problemstatement_submissions', "problemstatement IN ($problemstatementssql)");

            if ($problemstatements = get_records_sql($problemstatementssql)) {
                foreach ($problemstatements as $problemstatementid=>$unused) {
                    fulldelete($CFG->dataroot.'/'.$data->courseid.'/moddata/problemstatement/'.$problemstatementid);
                }
            }

            $status[] = array('component'=>$componentstr, 'item'=>get_string('deleteallsubmissions','problemstatement').': '.$typestr, 'error'=>false);

            if (empty($data->reset_gradebook_grades)) {
                // remove all grades from gradebook
                problemstatement_reset_gradebook($data->courseid, $this->type);
            }
        }

        /// updating dates - shift may be negative too
        if ($data->timeshift) {
            shift_course_mod_dates('problemstatement', array('timedue', 'timeavailable'), $data->timeshift, $data->courseid);
            $status[] = array('component'=>$componentstr, 'item'=>get_string('datechanged').': '.$typestr, 'error'=>false);
        }

        return $status;
    }*/
}

class mod_problemstatement_online_edit_form extends moodleform {
    function definition() {
        $mform =& $this->_form;

        // visible elements
        $langs = array( 0 => get_string("lang_0", 'problemstatement'),
		1 => get_string("lang_1", 'problemstatement'),
		2 => get_string("lang_2", 'problemstatement'),
		3 => get_string("lang_3", 'problemstatement'));
        $mform->addElement('select', 'langid', get_string('language', 'problemstatement'), $langs);
        $mform->setDefault('langid', 1);
        //$mform->addRule('langid', get_string('required'), 'required', null, 'client');

        $mform->addElement('textarea', 'programtext', get_string('submission', 'problemstatement'), array('cols'=>60, 'rows'=>30));
        //$mform->setType('programtext', PARAM_RAW); // to be cleaned before display
        //$mform->setHelpButton('programtext', array('reading', 'writing', 'richtext'), false, 'editorhelpbutton');
        $mform->addRule('programtext', get_string('required'), 'required', null, 'client');

        //$mform->addElement('format', 'format', get_string('format'));
        //$mform->setHelpButton('format', array('textformat', get_string('helpformatting')));

        // hidden params
        $mform->addElement('hidden', 'id', 0);
        $mform->setType('id', PARAM_INT);

        // buttons
        $this->add_action_buttons();

    }
}
?>
