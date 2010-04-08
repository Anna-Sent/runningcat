package DataBaseContent;

import DataBaseContent.Generic.DataElement;

/**
 *
 * @author partizanka
 */
public class ParseFormat extends DataElement {

    private String comment, read_function, write_function;
    private int data_type_id;

    /**
     *
     * @param id
     * @param comment
     * @param data_type_id
     * @param read_function
     * @param write_function
     */
    public ParseFormat(int id, String comment, int data_type_id,
            String read_function, String write_function) {
        this.id = id;
        this.comment = comment;
        this.data_type_id = data_type_id;
        this.read_function = read_function;
        this.write_function = write_function;
    }

    /**
     * 
     * @return
     */
    public DataType getDataType() {
        return DataTypes.getInstance().getDataTypeById(data_type_id);
    }
}
