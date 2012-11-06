package aufgabe2.interfaces;

import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 30.10.12
 * Time: 11:32
 */
public interface DataManager {

    /**
     * Zum lesen unsortierter Blöcke an Datensätzen, um diese per
     * InsertSort für das MergeSort vorzubereiten.
     * Beispiel; Datensätze d = {2,6,8,34,74,23,63,234,45,267}, Blockgröße b = 4
     *           DataManager.readBlock() -> {2,6,8,34}
     *           DataManager.readBlock() -> {74,23,63,234}
     *           DataManager.readBlock() -> {45,267}
     * @return einen DataWrapper inkl. unsortiertem Block
     */
    public DataWrapper readBlock();


    /**
     * Schreibt Blöcke von Datensätzen abwechselnd in zwei Dateien.
     *
     *
     * @param dataWrapper beinhaltet einen Array mit sortierten Datensätzen
     */
    public void write(DataWrapper dataWrapper);

    /**
     *
     * Erstellt einen neuen DataWrapper, anstelle der Klasse.
     *
     * @param data Array von Datensätzen
     * @param size of Array von Datensätzen
     * @return DataWrapper mit angegebenen Daten
     */
    public DataWrapper createDataWrapper();
    
    // 6.11.2012: 
    /*statt read() sollen jetzt die einzelnen b�nder gelesen werden, also das linke und das rechte,
     * Hier die "getter": */ 
    /**
     * 
     * @return DataWrapper, der die Daten auf dem linken band beinhaltet (vorsortiert!) 
     */
    DataWrapper readLeftChannel(); 
    
    
    /**
     * 
     * @return DataWrapper, der die Daten auf dem rechten band beinhaltet (vorsortiert!) 
     */
    DataWrapper readRightChannel(); 

}
