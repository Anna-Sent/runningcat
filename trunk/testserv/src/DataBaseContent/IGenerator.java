/*
 */
package DataBaseContent;

import DataBaseContent.Generic.DataElement;

/**
 *
 * @author partizanka
 */
public class IGenerator extends DataElement {

    private int language_id;
    private String source;

    public IGenerator(int id, String source, int language_id) {
        this.id = id;
        this.source = source;
        this.language_id = language_id;
    }
}
