package aufgabe2.interfaces;

/**
 * AD-Praktikum
 * Team: 13
 * Date: 30.10.12
 * Time: 23:06
 */
public interface DataWrapper {

    /**
     * Gibt die beinhalteten Datensätze zurück.
     *
     * @return array von unsortierten integer
     */
    int[] getData();

    /**
     * Gibt die Anzahl der Datensätze zurück, die sich im Array des DataWrappers befinden.
     *
     * @return Anzahl der Datensätze
     */
    int getSize();

    /**
     * <b>Nicht von nöten aufgrund von constructor!</b>
     * <p>
     * Speichert das angegbene Array in den DataWrapper, size sollte ebenfals gesetzt werden.
     *
     * @param data Array von Datensätzen
     */
    void setData(int[] data);

    /**
     * <b>Nicht von nöten aufgrund von constructor!</b>
     * <p>
     * Setzt die Anzahl der Datensätze für einfachere Abfrage.
     *
     * @param size of Array von Datensätzen
     */
    void setSize(int size);
}
