<?php
error_reporting(E_ALL); 
require_once ($CFG->dirroot.'/course/moodleform_mod.php');

class mod_problemstatement_mod_form extends moodleform_mod {

    function definition() {
        global $CFG;

        $mform =& $this->_form;
		$mform->addElement('html','
<script type="text/javascript" src="'.$CFG->httpswwwroot.'/mod/problemstatement/jquery-1.3.2.min.js"></script>
<script type="text/javascript" defer="defer">
//<![CDATA[
$(document).ready(
    function()
    {
$(\'#id_problem_id\').change(
    function() 
    {	    $.getJSON(\'http://meinekleine.homelinux.org/moodle/mod/problemstatement/get_problem_data_by_id.php?id=\'+$(this).val(),
	    function(data) { 
		    $(\'#problem_description_id\').html(data.description)
		    $(\'#problem_restrictions_id\').html(data.restrictions)
		    $(\'#problem_samples_id\').html(data.samples)
		    
		    
} 
)
}
)
});
//]]>
</script>
');
//-------------------------------------------------------------------------------
        $mform->addElement('header', 'general', get_string('general', 'form'));

        $mform->addElement('text', 'name', get_string('problemstatementname', 'problemstatement'), array('size'=>'48'));
        $mform->setType('name', PARAM_CLEAN);
        $mform->addRule('name', null, 'required', null, 'client');

/*        $mform->addElement('text', 'testsdir', get_string('testsdirectory', 'problemstatement'), array('size'=>'48'));
        $mform->setType('testsdir', PARAM_CLEAN);
        $mform->addRule('testsdir', null, 'required', null, 'client');

        $mform->addElement('htmleditor', 'description', get_string('problemdescription', 'problemstatement'));
        $mform->setType('description', PARAM_RAW);
        $mform->setHelpButton('description', array('writing', 'questions', 'richtext'), false, 'editorhelpbutton');
        $mform->addRule('description', get_string('required'), 'required', null, 'client');
		
        $mform->addElement('htmleditor', 'restrictions', get_string('restrictions', 'problemstatement'));
        $mform->setType('restrictions', PARAM_RAW);
        $mform->setHelpButton('restrictions', array('writing', 'questions', 'richtext'), false, 'editorhelpbutton');
        //$mform->addRule('restrictions', get_string('required'), 'required', null, 'client');

        $mform->addElement('htmleditor', 'samples', get_string('samples', 'problemstatement'));
        $mform->setType('samples', PARAM_RAW);
        $mform->setHelpButton('samples', array('writing', 'questions', 'richtext'), false, 'editorhelpbutton');
        //$mform->addRule('samples', get_string('required'), 'required', null, 'client');
*/
//=============================================
	$problem_id = -1;
	if (!empty($this->_instance)) {
		if ($pr = get_record('problemstatement', 'id', (int)$this->_instance)) {
			$problem_id = $pr->problem_id;
		} else {
			error('incorrect problemstatement');
		}
	}
	
	$query = 'SELECT id, name, description, restrictions, samples FROM '.$CFG->prefix.'problemstatement_problem';
	if (($problems = get_records_sql($query, 0, 0)) !== false) {
		$strings = array();
		$strings[null]=get_string('selectproblem','problemstatement');
		$index = null;
		$problem_description = '';
		$problem_restrictions = '';
		$problem_samples = '';
		$problem_testsdir = '';//var_dump($problems);
		foreach($problems as $key=>$value) {
			$strings[$key] = $value->name;
			if ($value->id == $problem_id) {
				$index = $key;
				$problem_description = $value->description;
				$problem_restrictions = $value->restrictions;
				$problem_samples = $value->samples;
			}
		}
		$mform->addElement('select', 'problem_id', get_string('problemstatements', 'problemstatement'), $strings);
		$mform->setDefault('problem_id', $index);
        $mform->addRule('problem_id', get_string('required'), 'required', null, 'client');

//		$mform->addElement('html', '<div id=\'problemstatement-description\'></div>');

		//var_dump($problem_description);
		$mform->addElement('html',
		'
		<div class="fitem"><div class="fitemtitle"><div class="fstaticlabel"><label for="id_problem_description"><strong>Постановка задачи</strong> </label></div></div><div class="felement fstatic"> <font size="3">
		<div id="problem_description_id">'
		.$problem_description.
		'</div>
		</font><br/> </div></div>
		'
		);

		$mform->addElement('html',
		'
		<div class="fitem"><div class="fitemtitle"><div class="fstaticlabel"><label for="id_problem_description"><strong>Ограничения</strong> </label></div></div><div class="felement fstatic"> <font size="3">
		<div id="problem_restrictions_id">'
		.$problem_restrictions.
		'</div>
		</font><br/> </div></div>
		'
		);
		
		$mform->addElement('html',
		'
		<div class="fitem"><div class="fitemtitle"><div class="fstaticlabel"><label for="id_problem_description"><strong>Примеры</strong> </label></div></div><div class="felement fstatic"> <font size="3">
		<div id="problem_samples_id">'
		.$problem_samples.
		'</div>
		</font><br/> </div></div>
		'
		);


//$mform->addElement('static','problem_description','<STRONG>Постановка задачи</STRONG>',$problem_description);
//		$mform->addElement('static','problem_restrictions','<STRONG>Ограничения</STRONG>',$problem_restrictions);
//		$mform->addElement('static','problem_samples','<STRONG>Примеры</STRONG>',$problem_samples);
    /*$id = optional_param('id', 0, PARAM_INT); // Course Module ID, or
    $a  = optional_param('a', 0, PARAM_INT);  // problemstatement ID
	echo $id;
	echo $a;
	        if (! $problemstatement = get_record("problemstatement", "id", $cm->instance)) {
            error("Problemstatement ID is incorrect");
        }*/
	
	}
//=============================================

		$mform->addElement('modgrade', 'grade', get_string('grade'));
        $mform->setDefault('grade', 100);

        $mform->addElement('date_time_selector', 'timeavailable', get_string('availabledate', 'problemstatement'), array('optional'=>true));
        $mform->setDefault('timeavailable', time());
        $mform->addElement('date_time_selector', 'timedue', get_string('duedate', 'problemstatement'), array('optional'=>true));
        $mform->setDefault('timedue', time()+7*24*3600);

        $ynoptions = array( 0 => get_string('no'), 1 => get_string('yes'));

        $mform->addElement('select', 'preventlate', get_string('preventlate', 'problemstatement'), $ynoptions);
        $mform->setDefault('preventale', 0);

        $mform->addElement('select', 'allowresubmit', get_string('allowresubmit', 'problemstatement'), $ynoptions);
        $mform->setDefault('allowresubmit', 1);

        $features = new stdClass;
        $features->groups = true;
        $features->groupings = true;
        $features->groupmembersonly = true;
        $this->standard_coursemodule_elements($features);

        $this->add_action_buttons();
    }
}
?>
