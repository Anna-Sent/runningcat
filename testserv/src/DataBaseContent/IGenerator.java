package DataBaseContent;

import DataBaseContent.Generic.DataElement;

/**
 * Класс "Генератор входа" (input generator).
 *
 * @author partizanka
 */
public class IGenerator extends DataElement {

    private int language_id;
    private String source;

    /**
     * Конструктор класса.
     *
     * @param id код генератора входа
     * @param source исходный код программы-генератора
     * @param language_id код языка программирования, на котором написана
     * программа-генератор
     */
    public IGenerator(int id, String source, int language_id) {
        this.id = id;
        this.source = source;
        this.language_id = language_id;
    }
}
