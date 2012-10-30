package aufgabe2.interfaces;

import aufgabe2.data.DataWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 30.10.12
 * Time: 11:32
 */
public interface DataManager {

    /*
    <Name>:<Text>;<\n>
    Anmerkungen und Ideen:
    Alex: Momentan alles Vorschläge, die Namensgebung DataWrapper klingt vllt. etwas allgemein;

     */

    /**
     * Zum lesen gewünschter unsortierter Blöcke an Datensätzen, um diese per
     * InsertSort für das MergeSort vorzubereiten.
     * Beispiel; Datensätze d = {2,6,8,34,74,23,63,234,45,267}, Blockgröße b = 4
     *           DataManager.readBlock(b) -> {2,6,8,34}
     *           DataManager.readBlock(b) -> {74,23,63,234}
     *           DataManager.readBlock(b) -> {45,267}
     *
     * @param blockSize größe, bzw. Anzahl der zu lesenden Datensätze
     * @return einen DataWrapper inkl. unsortiertem Block
     */
    public DataWrapper readBlock(int blockSize);

    /**
     * Liest zwei, bereits mit InsertSort sortierte, DataWrapper.
     *
     * @return DataWrapper Array mit größe 2, [0] linker, [1] rechter Datawrapper
     */
    public DataWrapper[] read();

    /**
     * Schreibt Blöcke von Datensätzen abwechselnd in zwei Dateien.
     *
     * @param dataWrapper beinhaltet einen Array mit sortierten Datensätzen
     */
    public void write(DataWrapper dataWrapper);

}
