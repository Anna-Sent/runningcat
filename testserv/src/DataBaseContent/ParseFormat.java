package DataBaseContent;

import DataBaseContent.Generic.DataElement;

/**
 * Класс "Формат разбора".
 *
 * @author partizanka
 */
public class ParseFormat extends DataElement {

    private String comment, read_function, write_function;
    private int data_type_id;

    /**
     * Конструктор класса.
     *
     * @param id код формата
     * @param comment наименование формата
     * @param data_type_id форматируемый тип данных
     * @param read_function функция на чтение в заданном формате
     * @param write_function функция на запись в заданном формате
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
     * Возвращает тип данных {@link DataType}, для которого применяется
     * заданный формат.
     *
     * @return экземпляр класса {@code DataType}, соответствующий формату
     */
    public DataType getDataType() {
        return DataTypes.getInstance().getDataTypeById(data_type_id);
    }
}
