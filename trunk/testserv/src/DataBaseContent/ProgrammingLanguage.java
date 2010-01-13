/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataBaseContent;

import DataBaseContent.Generic.DataElement;

/**
 *
 * @author partizanka
 */
public class ProgrammingLanguage extends DataElement {
    public String language_name/*, suffix*/;
    public ProgrammingLanguage(int id, String language_name/*, String suffix*/) {
        this.id=id;
        this.language_name=language_name;
        //this.suffix=suffix;
    }
}
