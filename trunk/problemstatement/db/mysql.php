<?php // $Id: mysql.php,v 1.3 2006/08/28 16:41:20 mark-nielsen Exp $
/**
 * Upgrade procedures for problemstatement.
 *
 * @author Sentyakova Anna
 * @version $Id: mysql.php,v 1.3 2006/08/28 16:41:20 mark-nielsen Exp $
 * @package problemstatement
 **/

/**
 * This function does anything necessary to upgrade 
 * older versions to match current functionality 
 *
 * @uses $CFG
 * @param int $oldversion The prior version number
 * @return boolean Success/Failure
 **/
function problemstatement_upgrade($oldversion) {
    global $CFG;

    if ($oldversion < 2008092700) {

       # Do something ...

    }

    return true;
}

?>
