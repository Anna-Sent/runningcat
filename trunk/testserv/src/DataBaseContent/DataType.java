package DataBaseContent;

import DataBaseContent.Generic.DataElement;

/**
 * Класс "Тип данных".
 *
 * @author partizanka
 */
public class DataType extends DataElement {

    private String data_type_name_en;

    /**
     * Конструктор класса.
     *
     * @param id код типа данных
     * @param data_type_name_en наименование типа данных
     */
    public DataType(int id, String data_type_name_en) {
        this.id = id;
        this.data_type_name_en = data_type_name_en;
    }
}
