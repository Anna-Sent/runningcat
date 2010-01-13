<?php  // $Id: lib.php,v 1.4 2006/08/28 16:41:20 mark-nielsen Exp $
error_reporting(E_ALL); 
/**
 * Library of functions and constants for module problemstatement
 *
 * @author Sentyakova Anna
 * @version $Id: lib.php,v 1.4 2006/08/28 16:41:20 mark-nielsen Exp $
 * @package problemstatement
 **/

$problemstatement_CONSTANT = 7;     /// for example
DEFINE ('PROBLEMSTATEMENT_COUNT_WORDS', 1);
DEFINE ('PROBLEMSTATEMENT_COUNT_LETTERS', 2);

/**
 * Given an object containing all the necessary data, 
 * (defined by the form in mod.html) this function 
 * will create a new instance and return the id number 
 * of the new instance.
 *
 * @param object $instance An object from the form in mod.html
 * @return int The id of the newly inserted problemstatement record
 **/
function problemstatement_add_instance($problemstatement) {
    global $CFG;
    require_once("$CFG->dirroot/mod/problemstatement/problemstatement.class.php");
    $pclass = "problemstatement";
    $pinstance = new $pclass();
    return $pinstance->add_instance($problemstatement);
/*    $problemstatement->timemodified = time();

    # May have to add extra stuff in here #
    
    return insert_record("problemstatement", $problemstatement);*/
}

/**
 * Given an object containing all the necessary data, 
 * (defined by the form in mod.html) this function 
 * will update an existing instance with new data.
 *
 * @param object $instance An object from the form in mod.html
 * @return boolean Success/Fail
 **/
function problemstatement_update_instance($problemstatement) {
    global $CFG;
    require_once("$CFG->dirroot/mod/problemstatement/problemstatement.class.php");
    $pclass = "problemstatement";
    $pinstance = new $pclass();
    return $pinstance->update_instance($problemstatement);
/*    $problemstatement->timemodified = time();
    $problemstatement->id = $problemstatement->instance;

    # May have to add extra stuff in here #

    return update_record("problemstatement", $problemstatement);*/
}

/**
 * Given an ID of an instance of this module, 
 * this function will permanently delete the instance 
 * and any data that depends on it. 
 *
 * @param int $id Id of the module instance
 * @return boolean Success/Failure
 **/
function problemstatement_delete_instance($id) {
    global $CFG;
    if (! $problemstatement = get_record('problemstatement', 'id', $id)) {
        return false;
    }
    require_once("$CFG->dirroot/mod/problemstatement/problemstatement.class.php");
    $pclass = "problemstatement";
    $pinstance = new $pclass();
    return $pinstance->delete_instance($problemstatement);
    /*if (! $problemstatement = get_record("problemstatement", "id", "$id")) {
        return false;
    }

    $result = true;

    # Delete any dependent records here #

    if (! delete_records("problemstatement", "id", "$problemstatement->id")) {
        $result = false;
    }

    return $result;*/
}

/**
 * Return a small object with summary information about what a 
 * user has done with a given particular instance of this module
 * Used for user activity reports.
 * $return->time = the time they did it
 * $return->info = a short text description
 *
 * @return null
 * @todo Finish documenting this function
 **/
function problemstatement_user_outline($course, $user, $mod, $problemstatement) {
    global $CFG;

    require_once("$CFG->dirroot/mod/problemstatenment/problemstatement.class.php");
    $pclass = "problemstatement";
    $pinstance = new $pclass($mod->id, $problemstatement, $mod, $course);
    return $pinstance->user_outline($user);
}

/**
 * Print a detailed representation of what a user has done with 
 * a given particular instance of this module, for user activity reports.
 *
 * @return boolean
 * @todo Finish documenting this function
 **/
function problemstatement_user_complete($course, $user, $mod, $problemstatement) {
    global $CFG;

    require_once("$CFG->dirroot/mod/problemstatement/problemstatement.class.php");
    $pclass = "problemstatement";
    $pinstance = new $problemstatementclass($mod->id, $problemstatement, $mod, $course);
    return $pinstance->user_complete($user);
}

/**
 * Given a course and a time, this module should find recent activity 
 * that has occurred in problemstatement activities and print it out. 
 * Return true if there was output, or false is there was none. 
 *
 * @uses $CFG
 * @return boolean
 * @todo Finish documenting this function
 **/
function problemstatement_print_recent_activity($course, $isteacher, $timestart) {
    global $CFG;

    return false;  //  True if anything was printed, otherwise false 
}

/**
 * Function to be run periodically according to the moodle cron
 * This function searches for things that need to be done, such 
 * as sending out mail, toggling flags etc ... 
 *
 * @uses $CFG
 * @return boolean
 * @todo Finish documenting this function
 **/
function problemstatement_cron () {
    global $CFG;
/*
    global $CFG, $USER;

    /// first execute all crons in plugins
    if ($plugins = get_list_of_plugins('mod/assignment/type')) {
        foreach ($plugins as $plugin) {
            require_once("$CFG->dirroot/mod/assignment/type/$plugin/assignment.class.php");
            $assignmentclass = "assignment_$plugin";
            $ass = new $assignmentclass();
            $ass->cron();
        }
    }

    /// Notices older than 1 day will not be mailed.  This is to avoid the problem where
    /// cron has not been running for a long time, and then suddenly people are flooded
    /// with mail from the past few weeks or months

    $timenow   = time();
    $endtime   = $timenow - $CFG->maxeditingtime;
    $starttime = $endtime - 24 * 3600;   /// One day earlier

    if ($submissions = assignment_get_unmailed_submissions($starttime, $endtime)) {

        $realuser = clone($USER);

        foreach ($submissions as $key => $submission) {
            if (! set_field("assignment_submissions", "mailed", "1", "id", "$submission->id")) {
                echo "Could not update the mailed field for id $submission->id.  Not mailed.\n";
                unset($submissions[$key]);
            }
        }

        $timenow = time();

        foreach ($submissions as $submission) {

            echo "Processing assignment submission $submission->id\n";

            if (! $user = get_record("user", "id", "$submission->userid")) {
                echo "Could not find user $post->userid\n";
                continue;
            }

            if (! $course = get_record("course", "id", "$submission->course")) {
                echo "Could not find course $submission->course\n";
                continue;
            }

            /// Override the language and timezone of the "current" user, so that
            /// mail is customised for the receiver.
            $USER = $user;
            course_setup($course);

            if (!has_capability('moodle/course:view', get_context_instance(CONTEXT_COURSE, $submission->course), $user->id)) {
                echo fullname($user)." not an active participant in " . format_string($course->shortname) . "\n";
                continue;
            }

            if (! $teacher = get_record("user", "id", "$submission->teacher")) {
                echo "Could not find teacher $submission->teacher\n";
                continue;
            }

            if (! $mod = get_coursemodule_from_instance("assignment", $submission->assignment, $course->id)) {
                echo "Could not find course module for assignment id $submission->assignment\n";
                continue;
            }

            if (! $mod->visible) {    /// Hold mail notification for hidden assignments until later
                continue;
            }

            $strassignments = get_string("modulenameplural", "assignment");
            $strassignment  = get_string("modulename", "assignment");

            $assignmentinfo = new object();
            $assignmentinfo->teacher = fullname($teacher);
            $assignmentinfo->assignment = format_string($submission->name,true);
            $assignmentinfo->url = "$CFG->wwwroot/mod/assignment/view.php?id=$mod->id";

            $postsubject = "$course->shortname: $strassignments: ".format_string($submission->name,true);
            $posttext  = "$course->shortname -> $strassignments -> ".format_string($submission->name,true)."\n";
            $posttext .= "---------------------------------------------------------------------\n";
            $posttext .= get_string("assignmentmail", "assignment", $assignmentinfo)."\n";
            $posttext .= "---------------------------------------------------------------------\n";

            if ($user->mailformat == 1) {  // HTML
                $posthtml = "<p><font face=\"sans-serif\">".
                "<a href=\"$CFG->wwwroot/course/view.php?id=$course->id\">$course->shortname</a> ->".
                "<a href=\"$CFG->wwwroot/mod/assignment/index.php?id=$course->id\">$strassignments</a> ->".
                "<a href=\"$CFG->wwwroot/mod/assignment/view.php?id=$mod->id\">".format_string($submission->name,true)."</a></font></p>";
                $posthtml .= "<hr /><font face=\"sans-serif\">";
                $posthtml .= "<p>".get_string("assignmentmailhtml", "assignment", $assignmentinfo)."</p>";
                $posthtml .= "</font><hr />";
            } else {
                $posthtml = "";
            }

            if (! email_to_user($user, $teacher, $postsubject, $posttext, $posthtml)) {
                echo "Error: assignment cron: Could not send out mail for id $submission->id to user $user->id ($user->email)\n";
            }
        }

        $USER = $realuser;
        course_setup(SITEID); // reset cron user language, theme and timezone settings

    }

    return true;*/
    return true;
}

/**
 * Must return an array of grades for a given instance of this module, 
 * indexed by user.  It also returns a maximum allowed grade.
 * 
 * Example:
 *    $return->grades = array of grades;
 *    $return->maxgrade = maximum allowed grade;
 *
 *    return $return;
 *
 * @param int $problemstatementid ID of an instance of this module
 * @return mixed Null or object with an array of grades and with the maximum grade
 **/
function problemstatement_grades($problemstatementid) {
   return NULL;
}

/**
 * Must return an array of user records (all data) who are participants
 * for a given instance of problemstatement. Must include every user involved
 * in the instance, independient of his role (student, teacher, admin...)
 * See other modules as example.
 *
 * @param int $problemstatementid ID of an instance of this module
 * @return mixed boolean/array of students
 **/
function problemstatement_get_participants($problemstatementid) {
    //Get students
    $students = get_records_sql("SELECT DISTINCT u.id, u.id
                                 FROM {$CFG->prefix}user u,
                                      {$CFG->prefix}problemstatement_submissions p
                                 WHERE p.problemstatement = '$problemstatementid' and
                                       u.id = p.userid");
    //Get teachers
    $teachers = get_records_sql("SELECT DISTINCT u.id, u.id
                                 FROM {$CFG->prefix}user u,
                                      {$CFG->prefix}problemstatement_submissions p
                                 WHERE p.problemstatement = '$problemstatementid' and
                                       u.id = p.userid");

    //Add teachers to students
    if ($teachers) {
        foreach ($teachers as $teacher) {
            $students[$teacher->id] = $teacher;
        }
    }
    //Return students array (it contains an array of unique users)
    return ($students);
}

/**
 * This function returns if a scale is being used by one problemstatement
 * it it has support for grading and scales. Commented code should be
 * modified if necessary. See forum, glossary or journal modules
 * as reference.
 *
 * @param int $problemstatementid ID of an instance of this module
 * @return mixed
 * @todo Finish documenting this function
 **/
function problemstatement_scale_used ($problemstatementid,$scaleid) {
    $return = false;

    //$rec = get_record("problemstatement","id","$problemstatementid","scale","-$scaleid");
    //
    //if (!empty($rec)  && !empty($scaleid)) {
    //    $return = true;
    //}
   
    return $return;
}

//////////////////////////////////////////////////////////////////////////////////////
/// Any other problemstatement functions go here.  Each of them must have a name that 
/// starts with problemstatement_

/**
 * Return grade for given user or all users.
 *
 * @param int $assignmentid id of assignment
 * @param int $userid optional user id, 0 means all users
 * @return array array of grades, false if none
 */
/*function assignment_get_user_grades($assignment, $userid=0) {
    global $CFG;

    $user = $userid ? "AND u.id = $userid" : "";

    $sql = "SELECT u.id, u.id AS userid, s.grade AS rawgrade, s.submissioncomment AS feedback, s.format AS feedbackformat,
                   s.teacher AS usermodified, s.timemarked AS dategraded, s.timemodified AS datesubmitted
              FROM {$CFG->prefix}user u, {$CFG->prefix}assignment_submissions s
             WHERE u.id = s.userid AND s.assignment = $assignment->id
                   $user";

    return get_records_sql($sql);
}*/

/**
 * Update grades by firing grade_updated event
 *
 * @param object $problemstatement null means all problemstatements
 * @param int $userid specific user only, 0 mean all
 */
function problemstatement_update_grades($problemstatement=null, $userid=0, $nullifnone=true) {
    global $CFG;
    if (!function_exists('grade_update')) { //workaround for buggy PHP versions
        require_once($CFG->libdir.'/gradelib.php');
    }

    if ($problemstatement != null) {
        if ($grades = problemstatement_get_user_grades($problemstatement, $userid)) {
            foreach($grades as $k=>$v) {
                if ($v->rawgrade == -1) {
                    $grades[$k]->rawgrade = null;
                }
            }
            problemstatement_grade_item_update($problemstatement, $grades);
        } else {
            problemstatement_grade_item_update($problemstatement);
        }

    } else {
        $sql = "SELECT a.*, cm.idnumber as cmidnumber, a.course as courseid
                  FROM {$CFG->prefix}problemstatement a, {$CFG->prefix}course_modules cm, {$CFG->prefix}modules m
                 WHERE m.name='problemstatement' AND m.id=cm.module AND cm.instance=a.id";
        if ($rs = get_recordset_sql($sql)) {
            while ($problemstatement = rs_fetch_next_record($rs)) {
                if ($problemstatement->grade != 0) {
                    problemstatement_update_grades($problemstatement);
                } else {
                    problemstatement_grade_item_update($problemstatement);
                }
            }
            rs_close($rs);
        }
    }
}

/**
 * Create grade item for given assignment
 *
 * @param object $assignment object with extra cmidnumber
 * @param mixed optional array/object of grade(s); 'reset' means reset grades in gradebook
 * @return int 0 if ok, error code otherwise
 */
/*function assignment_grade_item_update($assignment, $grades=NULL) {
    global $CFG;
    if (!function_exists('grade_update')) { //workaround for buggy PHP versions
        require_once($CFG->libdir.'/gradelib.php');
    }

    if (!isset($assignment->courseid)) {
        $assignment->courseid = $assignment->course;
    }

    $params = array('itemname'=>$assignment->name, 'idnumber'=>$assignment->cmidnumber);

    if ($assignment->grade > 0) {
        $params['gradetype'] = GRADE_TYPE_VALUE;
        $params['grademax']  = $assignment->grade;
        $params['grademin']  = 0;

    } else if ($assignment->grade < 0) {
        $params['gradetype'] = GRADE_TYPE_SCALE;
        $params['scaleid']   = -$assignment->grade;

    } else {
        $params['gradetype'] = GRADE_TYPE_TEXT; // allow text comments only
    }

    if ($grades  === 'reset') {
        $params['reset'] = true;
        $grades = NULL;
    }

    return grade_update('mod/assignment', $assignment->courseid, 'mod', 'assignment', $assignment->id, 0, $grades, $params);
}*/

/**
 * Delete grade item for given assignment
 *
 * @param object $assignment object
 * @return object assignment
 */
/*function assignment_grade_item_delete($assignment) {
    global $CFG;
    require_once($CFG->libdir.'/gradelib.php');

    if (!isset($assignment->courseid)) {
        $assignment->courseid = $assignment->course;
    }

    return grade_update('mod/assignment', $assignment->courseid, 'mod', 'assignment', $assignment->id, 0, NULL, array('deleted'=>1));
}*/


/**
 * Checks if a scale is being used by an assignment
 *
 * This is used by the backup code to decide whether to back up a scale
 * @param $assignmentid int
 * @param $scaleid int
 * @return boolean True if the scale is used by the assignment
 */
/*function assignment_scale_used($assignmentid, $scaleid) {

    $return = false;

    $rec = get_record('assignment','id',$assignmentid,'grade',-$scaleid);

    if (!empty($rec) && !empty($scaleid)) {
        $return = true;
    }

    return $return;
}*/

/**
 * Checks if scale is being used by any instance of assignment
 *
 * This is used to find out if scale used anywhere
 * @param $scaleid int
 * @return boolean True if the scale is used by any assignment
 */
/*function assignment_scale_used_anywhere($scaleid) {
    if ($scaleid and record_exists('assignment', 'grade', -$scaleid)) {
        return true;
    } else {
        return false;
    }
}*/

/**
 * Make sure up-to-date events are created for all assignment instances
 *
 * This standard function will check all instances of this module
 * and make sure there are up-to-date events created for each of them.
 * If courseid = 0, then every assignment event in the site is checked, else
 * only assignment events belonging to the course specified are checked.
 * This function is used, in its new format, by restore_refresh_events()
 *
 * @param $courseid int optional If zero then all assignments for all courses are covered
 * @return boolean Always returns true
 */
/*function assignment_refresh_events($courseid = 0) {

    if ($courseid == 0) {
        if (! $assignments = get_records("assignment")) {
            return true;
        }
    } else {
        if (! $assignments = get_records("assignment", "course", $courseid)) {
            return true;
        }
    }
    $moduleid = get_field('modules', 'id', 'name', 'assignment');

    foreach ($assignments as $assignment) {
        $event = NULL;
        $event->name        = addslashes($assignment->name);
        $event->description = addslashes($assignment->description);
        $event->timestart   = $assignment->timedue;

        if ($event->id = get_field('event', 'id', 'modulename', 'assignment', 'instance', $assignment->id)) {
            update_event($event);

        } else {
            $event->courseid    = $assignment->course;
            $event->groupid     = 0;
            $event->userid      = 0;
            $event->modulename  = 'assignment';
            $event->instance    = $assignment->id;
            $event->eventtype   = 'due';
            $event->timeduration = 0;
            $event->visible     = get_field('course_modules', 'visible', 'module', $moduleid, 'instance', $assignment->id);
            add_event($event);
        }

    }
    return true;
}*/

/**
 * Print recent activity from all assignments in a given course
 *
 * This is used by the recent activity block
 */
/*function assignment_print_recent_activity($course, $viewfullnames, $timestart) {
    global $CFG, $USER;

    // do not use log table if possible, it may be huge

    if (!$submissions = get_records_sql("SELECT asb.id, asb.timemodified, cm.id AS cmid, asb.userid,
                                                u.firstname, u.lastname, u.email, u.picture
                                           FROM {$CFG->prefix}assignment_submissions asb
                                                JOIN {$CFG->prefix}assignment a      ON a.id = asb.assignment
                                                JOIN {$CFG->prefix}course_modules cm ON cm.instance = a.id
                                                JOIN {$CFG->prefix}modules md        ON md.id = cm.module
                                                JOIN {$CFG->prefix}user u            ON u.id = asb.userid
                                          WHERE asb.timemodified > $timestart AND
                                                a.course = {$course->id} AND
                                                md.name = 'assignment'
                                       ORDER BY asb.timemodified ASC")) {
         return false;
    }

    $modinfo =& get_fast_modinfo($course); // reference needed because we might load the groups
    $show    = array();
    $grader  = array();

    foreach($submissions as $submission) {
        if (!array_key_exists($submission->cmid, $modinfo->cms)) {
            continue;
        }
        $cm = $modinfo->cms[$submission->cmid];
        if (!$cm->uservisible) {
            continue;
        }
        if ($submission->userid == $USER->id) {
            $show[] = $submission;
            continue;
        }

        // the act of sumitting of assignemnt may be considered private - only graders will see it if specified
        if (empty($CFG->assignment_showrecentsubmissions)) {
            if (!array_key_exists($cm->id, $grader)) {
                $grader[$cm->id] = has_capability('moodle/grade:viewall', get_context_instance(CONTEXT_MODULE, $cm->id));
            }
            if (!$grader[$cm->id]) {
                continue;
            }
        }

        $groupmode = groups_get_activity_groupmode($cm, $course);

        if ($groupmode == SEPARATEGROUPS and !has_capability('moodle/site:accessallgroups', get_context_instance(CONTEXT_MODULE, $cm->id))) {
            if (isguestuser()) {
                // shortcut - guest user does not belong into any group
                continue;
            }

            if (is_null($modinfo->groups)) {
                $modinfo->groups = groups_get_user_groups($course->id); // load all my groups and cache it in modinfo
            }

            // this will be slow - show only users that share group with me in this cm
            if (empty($modinfo->groups[$cm->id])) {
                continue;
            }
            $usersgroups =  groups_get_all_groups($course->id, $cm->userid, $cm->groupingid);
            if (is_array($usersgroups)) {
                $usersgroups = array_keys($usersgroups);
                $interset = array_intersect($usersgroups, $modinfo->groups[$cm->id]);
                if (empty($intersect)) {
                    continue;
                }
            }
        }
        $show[] = $submission;
    }

    if (empty($show)) {
        return false;
    }

    print_headline(get_string('newsubmissions', 'assignment').':');

    foreach ($show as $submission) {
        $cm = $modinfo->cms[$submission->cmid];
        $link = $CFG->wwwroot.'/mod/assignment/view.php?id='.$cm->id;
        print_recent_activity_note($submission->timemodified, $submission, $cm->name, $link, false, $viewfullnames);
    }

    return true;
}*/


/**
 * Returns all assignments since a given time in specified forum.
 */
/*function assignment_get_recent_mod_activity(&$activities, &$index, $timestart, $courseid, $cmid, $userid=0, $groupid=0)  {

    global $CFG, $COURSE, $USER;

    if ($COURSE->id == $courseid) {
        $course = $COURSE;
    } else {
        $course = get_record('course', 'id', $courseid);
    }

    $modinfo =& get_fast_modinfo($course);

    $cm = $modinfo->cms[$cmid];

    if ($userid) {
        $userselect = "AND u.id = $userid";
    } else {
        $userselect = "";
    }

    if ($groupid) {
        $groupselect = "AND gm.groupid = $groupid";
        $groupjoin   = "JOIN {$CFG->prefix}groups_members gm ON  gm.userid=u.id";
    } else {
        $groupselect = "";
        $groupjoin   = "";
    }

    if (!$submissions = get_records_sql("SELECT asb.id, asb.timemodified, asb.userid,
                                                u.firstname, u.lastname, u.email, u.picture
                                           FROM {$CFG->prefix}assignment_submissions asb
                                                JOIN {$CFG->prefix}assignment a      ON a.id = asb.assignment
                                                JOIN {$CFG->prefix}user u            ON u.id = asb.userid
                                                $groupjoin
                                          WHERE asb.timemodified > $timestart AND a.id = $cm->instance
                                                $userselect $groupselect
                                       ORDER BY asb.timemodified ASC")) {
         return;
    }

    $groupmode       = groups_get_activity_groupmode($cm, $course);
    $cm_context      = get_context_instance(CONTEXT_MODULE, $cm->id);
    $grader          = has_capability('moodle/grade:viewall', $cm_context);
    $accessallgroups = has_capability('moodle/site:accessallgroups', $cm_context);
    $viewfullnames   = has_capability('moodle/site:viewfullnames', $cm_context);

    if (is_null($modinfo->groups)) {
        $modinfo->groups = groups_get_user_groups($course->id); // load all my groups and cache it in modinfo
    }

    $show = array();

    foreach($submissions as $submission) {
        if ($submission->userid == $USER->id) {
            $show[] = $submission;
            continue;
        }

        // the act of sumitting of assignemnt may be considered private - only graders will see it if specified
        if (!empty($CFG->assignment_showrecentsubmissions)) {
            if (!$grader) {
                continue;
            }
        }

        if ($groupmode == SEPARATEGROUPS and !$accessallgroups) {
            if (isguestuser()) {
                // shortcut - guest user does not belong into any group
                continue;
            }

            // this will be slow - show only users that share group with me in this cm
            if (empty($modinfo->groups[$cm->id])) {
                continue;
            }
            $usersgroups = groups_get_all_groups($course->id, $cm->userid, $cm->groupingid);
            if (is_array($usersgroups)) {
                $usersgroups = array_keys($usersgroups);
                $interset = array_intersect($usersgroups, $modinfo->groups[$cm->id]);
                if (empty($intersect)) {
                    continue;
                }
            }
        }
        $show[] = $submission;
    }

    if (empty($show)) {
        return;
    }

    if ($grader) {
        require_once($CFG->libdir.'/gradelib.php');
        $userids = array();
        foreach ($show as $id=>$submission) {
            $userids[] = $submission->userid;

        }
        $grades = grade_get_grades($courseid, 'mod', 'assignment', $cm->instance, $userids);
    }

    $aname = format_string($cm->name,true);
    foreach ($show as $submission) {
        $tmpactivity = new object();

        $tmpactivity->type         = 'assignment';
        $tmpactivity->cmid         = $cm->id;
        $tmpactivity->name         = $aname;
        $tmpactivity->sectionnum   = $cm->sectionnum;
        $tmpactivity->timestamp    = $submission->timemodified;

        if ($grader) {
            $tmpactivity->grade = $grades->items[0]->grades[$submission->userid]->str_long_grade;
        }

        $tmpactivity->user->userid   = $submission->userid;
        $tmpactivity->user->fullname = fullname($submission, $viewfullnames);
        $tmpactivity->user->picture  = $submission->picture;

        $activities[$index++] = $tmpactivity;
    }

    return;
}*/

/**
 * Print recent activity from all assignments in a given course
 *
 * This is used by course/recent.php
 */
/*function assignment_print_recent_mod_activity($activity, $courseid, $detail, $modnames)  {
    global $CFG;

    echo '<table border="0" cellpadding="3" cellspacing="0" class="assignment-recent">';

    echo "<tr><td class=\"userpicture\" valign=\"top\">";
    print_user_picture($activity->user->userid, $courseid, $activity->user->picture);
    echo "</td><td>";

    if ($detail) {
        $modname = $modnames[$activity->type];
        echo '<div class="title">';
        echo "<img src=\"$CFG->modpixpath/assignment/icon.gif\" ".
             "class=\"icon\" alt=\"$modname\">";
        echo "<a href=\"$CFG->wwwroot/mod/assignment/view.php?id={$activity->cmid}\">{$activity->name}</a>";
        echo '</div>';
    }

    if (isset($activity->grade)) {
        echo '<div class="grade">';
        echo get_string('grade').': ';
        echo $activity->grade;
        echo '</div>';
    }

    echo '<div class="user">';
    echo "<a href=\"$CFG->wwwroot/user/view.php?id={$activity->user->userid}&amp;course=$courseid\">"
         ."{$activity->user->fullname}</a>  - ".userdate($activity->timestamp);
    echo '</div>';

    echo "</td></tr></table>";
}*/

/// GENERIC SQL FUNCTIONS

/**
 * Fetch info from logs
 *
 * @param $log object with properties ->info (the assignment id) and ->userid
 * @return array with assignment name and user firstname and lastname
 */
/*function assignment_log_info($log) {
    global $CFG;
    return get_record_sql("SELECT a.name, u.firstname, u.lastname
                             FROM {$CFG->prefix}assignment a,
                                  {$CFG->prefix}user u
                            WHERE a.id = '$log->info'
                              AND u.id = '$log->userid'");
}*/

/**
 * Return list of marked submissions that have not been mailed out for currently enrolled students
 *
 * @return array
 */
/*function assignment_get_unmailed_submissions($starttime, $endtime) {

    global $CFG;

    return get_records_sql("SELECT s.*, a.course, a.name
                              FROM {$CFG->prefix}assignment_submissions s,
                                   {$CFG->prefix}assignment a
                             WHERE s.mailed = 0
                               AND s.timemarked <= $endtime
                               AND s.timemarked >= $starttime
                               AND s.assignment = a.id");
*/
    /* return get_records_sql("SELECT s.*, a.course, a.name
                              FROM {$CFG->prefix}assignment_submissions s,
                                   {$CFG->prefix}assignment a,
                                   {$CFG->prefix}user_students us
                             WHERE s.mailed = 0
                               AND s.timemarked <= $endtime
                               AND s.timemarked >= $starttime
                               AND s.assignment = a.id
                               AND s.userid = us.userid
                               AND a.course = us.course");
    */
/*}*/

/**
 * Counts all real problemstatement submissions by ENROLLED students (not empty ones)
 *
 * @param $groupid int optional If nonzero then count is restricted to this group
 * @return int The number of submissions
 */
function problemstatement_count_real_submissions($cm, $groupid=0) {/*
    global $CFG;

    $context = get_context_instance(CONTEXT_MODULE, $cm->id);

    // this is all the users with this capability set, in this context or higher
    if ($users = get_users_by_capability($context, 'mod/problemstatement:submit', 'u.id', '', '', '', $groupid, '', false)) {
        $users = array_keys($users);
    }

    // if groupmembersonly used, remove users who are not in any group
    if ($users and !empty($CFG->enablegroupings) and $cm->groupmembersonly) {
        if ($groupingusers = groups_get_grouping_members($cm->groupingid, 'u.id', 'u.id')) {
            $users = array_intersect($users, array_keys($groupingusers));
        }
    }

    if (empty($users)) {
        return 0;
    }

    $userlists = implode(',', $users);

    return count_records_sql("SELECT COUNT('x')
                                FROM {$CFG->prefix}problemstatement_submissions
                               WHERE problemstatement = $cm->instance AND
                                     timemodified > 0 AND
                                     userid IN ($userlists)");*/
}


/**
 * Return all assignment submissions by ENROLLED students (even empty)
 *
 * There are also assignment type methods get_submissions() wich in the default
 * implementation simply call this function.
 * @param $sort string optional field names for the ORDER BY in the sql query
 * @param $dir string optional specifying the sort direction, defaults to DESC
 * @return array The submission objects indexed by id
 */
/*function assignment_get_all_submissions($assignment, $sort="", $dir="DESC") {
/// Return all assignment submissions by ENROLLED students (even empty)
    global $CFG;

    if ($sort == "lastname" or $sort == "firstname") {
        $sort = "u.$sort $dir";
    } else if (empty($sort)) {
        $sort = "a.timemodified DESC";
    } else {
        $sort = "a.$sort $dir";
    }
*/
    /* not sure this is needed at all since assignmenet already has a course define, so this join?
    $select = "s.course = '$assignment->course' AND";
    if ($assignment->course == SITEID) {
        $select = '';
    }*/
/*
    return get_records_sql("SELECT a.*
                              FROM {$CFG->prefix}assignment_submissions a,
                                   {$CFG->prefix}user u
                             WHERE u.id = a.userid
                               AND a.assignment = '$assignment->id'
                          ORDER BY $sort");
*/
    /* return get_records_sql("SELECT a.*
                              FROM {$CFG->prefix}assignment_submissions a,
                                   {$CFG->prefix}user_students s,
                                   {$CFG->prefix}user u
                             WHERE a.userid = s.userid
                               AND u.id = a.userid
                               AND $select a.assignment = '$assignment->id'
                          ORDER BY $sort");
    */
/*}*/

/**
 * Add a get_coursemodule_info function in case any assignment type wants to add 'extra' information
 * for the course (see resource).
 *
 * Given a course_module object, this function returns any "extra" information that may be needed
 * when printing this activity in a course listing.  See get_array_of_activities() in course/lib.php.
 *
 * @param $coursemodule object The coursemodule object (record).
 * @return object An object on information that the coures will know about (most noticeably, an icon).
 *
 */
/*function assignment_get_coursemodule_info($coursemodule) {
    global $CFG;

    if (! $assignment = get_record('assignment', 'id', $coursemodule->instance, '', '', '', '', 'id, assignmenttype, name')) {
        return false;
    }

    $libfile = "$CFG->dirroot/mod/assignment/type/$assignment->assignmenttype/assignment.class.php";

    if (file_exists($libfile)) {
        require_once($libfile);
        $assignmentclass = "assignment_$assignment->assignmenttype";
        $ass = new $assignmentclass('staticonly');
        if ($result = $ass->get_coursemodule_info($coursemodule)) {
            return $result;
        } else {
            $info = new object();
            $info->name = $assignment->name;
            return $info;
        }

    } else {
        debugging('Incorrect assignment type: '.$assignment->assignmenttype);
        return false;
    }
}*/



/// OTHER GENERAL FUNCTIONS FOR ASSIGNMENTS  ///////////////////////////////////////

/**
 * Executes upgrade scripts for assignment types when necessary
 */
/*function assignment_upgrade_submodules() {

    global $CFG;

/// Install/upgrade assignment types (it uses, simply, the standard plugin architecture)
    upgrade_plugins('assignment_type', 'mod/assignment/type', "$CFG->wwwroot/$CFG->admin/index.php");

}*/

/*function assignment_print_overview($courses, &$htmlarray) {

    global $USER, $CFG;

    if (empty($courses) || !is_array($courses) || count($courses) == 0) {
        return array();
    }

    if (!$assignments = get_all_instances_in_courses('assignment',$courses)) {
        return;
    }

    // Do assignment_base::isopen() here without loading the whole thing for speed
    foreach ($assignments as $key => $assignment) {
        $time = time();
        if ($assignment->timedue) {
            if ($assignment->preventlate) {
                $isopen = ($assignment->timeavailable <= $time && $time <= $assignment->timedue);
            } else {
                $isopen = ($assignment->timeavailable <= $time);
            }
        }
        if (empty($isopen) || empty($assignment->timedue)) {
            unset($assignments[$key]);
        }
    }

    $strduedate = get_string('duedate', 'assignment');
    $strduedateno = get_string('duedateno', 'assignment');
    $strgraded = get_string('graded', 'assignment');
    $strnotgradedyet = get_string('notgradedyet', 'assignment');
    $strnotsubmittedyet = get_string('notsubmittedyet', 'assignment');
    $strsubmitted = get_string('submitted', 'assignment');
    $strassignment = get_string('modulename', 'assignment');
    $strreviewed = get_string('reviewed','assignment');

    foreach ($assignments as $assignment) {
        $str = '<div class="assignment overview"><div class="name">'.$strassignment. ': '.
               '<a '.($assignment->visible ? '':' class="dimmed"').
               'title="'.$strassignment.'" href="'.$CFG->wwwroot.
               '/mod/assignment/view.php?id='.$assignment->coursemodule.'">'.
               $assignment->name.'</a></div>';
        if ($assignment->timedue) {
            $str .= '<div class="info">'.$strduedate.': '.userdate($assignment->timedue).'</div>';
        } else {
            $str .= '<div class="info">'.$strduedateno.'</div>';
        }
        $context = get_context_instance(CONTEXT_MODULE, $assignment->coursemodule);
        if (has_capability('mod/assignment:grade', $context)) {

            // count how many people can submit
            $submissions = 0; // init
            if ($students = get_users_by_capability($context, 'mod/assignment:submit', '', '', '', '', 0, '', false)) {
                 foreach ($students as $student) {
                    if (record_exists_sql("SELECT id FROM {$CFG->prefix}assignment_submissions
                                           WHERE assignment = $assignment->id AND
                                               userid = $student->id AND
                                               teacher = 0 AND
                                               timemarked = 0")) {
                        $submissions++;
                    }
                }
            }

            if ($submissions) {
                $str .= get_string('submissionsnotgraded', 'assignment', $submissions);
            }
        } else {
            $sql = "SELECT *
                      FROM {$CFG->prefix}assignment_submissions
                     WHERE userid = '$USER->id'
                       AND assignment = '{$assignment->id}'";
            if ($submission = get_record_sql($sql)) {
                if ($submission->teacher == 0 && $submission->timemarked == 0) {
                    $str .= $strsubmitted . ', ' . $strnotgradedyet;
                } else if ($submission->grade <= 0) {
                    $str .= $strsubmitted . ', ' . $strreviewed;
                } else {
                    $str .= $strsubmitted . ', ' . $strgraded;
                }
            } else {
                $str .= $strnotsubmittedyet . ' ' . assignment_display_lateness(time(), $assignment->timedue);
            }
        }
        $str .= '</div>';
        if (empty($htmlarray[$assignment->course]['assignment'])) {
            $htmlarray[$assignment->course]['assignment'] = $str;
        } else {
            $htmlarray[$assignment->course]['assignment'] .= $str;
        }
    }
}*/

/*function assignment_display_lateness($timesubmitted, $timedue) {
    if (!$timedue) {
        return '';
    }
    $time = $timedue - $timesubmitted;
    if ($time < 0) {
        $timetext = get_string('late', 'assignment', format_time($time));
        return ' (<span class="late">'.$timetext.'</span>)';
    } else {
        $timetext = get_string('early', 'assignment', format_time($time));
        return ' (<span class="early">'.$timetext.'</span>)';
    }
}*/

/*function assignment_get_view_actions() {
    return array('view');
}*/

/*function assignment_get_post_actions() {
    return array('upload');
}*/


/**
 * Removes all grades from gradebook
 * @param int $courseid
 * @param string optional type
 */
/*function assignment_reset_gradebook($courseid, $type='') {
    global $CFG;

    $type = $type ? "AND a.assignmenttype='$type'" : '';

    $sql = "SELECT a.*, cm.idnumber as cmidnumber, a.course as courseid
              FROM {$CFG->prefix}assignment a, {$CFG->prefix}course_modules cm, {$CFG->prefix}modules m
             WHERE m.name='assignment' AND m.id=cm.module AND cm.instance=a.id AND a.course=$courseid $type";

    if ($assignments = get_records_sql($sql)) {
        foreach ($assignments as $assignment) {
            assignment_grade_item_update($assignment, 'reset');
        }
    }
}*/

/**
 * This function is used by the reset_course_userdata function in moodlelib.
 * This function will remove all posts from the specified assignment
 * and clean up any related data.
 * @param $data the data submitted from the reset course.
 * @return array status array
 */
/*function assignment_reset_userdata($data) {
    global $CFG;

    $status = array();

    foreach (get_list_of_plugins('mod/assignment/type') as $type) {
        require_once("$CFG->dirroot/mod/assignment/type/$type/assignment.class.php");
        $assignmentclass = "assignment_$type";
        $ass = new $assignmentclass();
        $status = array_merge($status, $ass->reset_userdata($data));
    }

    return $status;
}*/

/**
 * Implementation of the function for printing the form elements that control
 * whether the course reset functionality affects the assignment.
 * @param $mform form passed by reference
 */
/*function assignment_reset_course_form_definition(&$mform) {
    $mform->addElement('header', 'assignmentheader', get_string('modulenameplural', 'assignment'));
    $mform->addElement('advcheckbox', 'reset_assignment_submissions', get_string('deleteallsubmissions','assignment'));
}*/

/**
 * Course reset form defaults.
 */
/*function assignment_reset_course_form_defaults($course) {
    return array('reset_assignment_submissions'=>1);
}*/

/**
 * Returns all other caps used in module
 */
/*function assignment_get_extra_capabilities() {
    return array('moodle/site:accessallgroups', 'moodle/site:viewfullnames');
}*/

?>
