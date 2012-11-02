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

    /**
     * Gibt die Anzahl der zugrundeliegenden Folgen, bei zusammengesetzten Folgen, zurück.
     * Bei Teilfolgen, also Stücke einer ganzen Folge ist eine 0 zuu setzen.
     * <p>
     * Bei einem Stück einer Folge = 0;<br>
     *             Bei einer Folge = 1;<br>
     *                Bei n Folgen = n;<br>
     *
     * @return
     */
    int getFolgeAnzahl();

    /**
     * Wenn der Buffer kleiner ist als die in der Datei zugrundeligende Folge, muss beim letzten Stück,
     * welches als DataWrapper übergeben wird das isFolgeKomplett auf True gesetzt werden, in anderen Fällen auf False
     *
     * @return
     */
    boolean isFolgeKomplett();
}
