package aufgabe2.interfaces;

import java.nio.ByteBuffer;

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
    public ByteBuffer readBlock();


    /**
     * Schreibt Blöcke von Datensätzen abwechselnd in zwei Dateien.
     *
     *
     * @param dataWrapper beinhaltet einen Array mit sortierten Datensätzen
     */
    public void writeBlock(ByteBuffer dataWrapper);

    /**
     *
     * Stellt einen Kanal zum schreiben der gemergeten Zahlen bereit
     *
     * @return OutputBuffer
     */
    public OutputBuffer createOuputBuffer();

    // 6.11.2012:
    //Sowas kommt in die JavaDocs!

    /**
     * 
     * @return InputBuffer, der die Daten auf dem linken band beinhaltet (vorsortiert!) 
     */
    public InputBuffer readLeftChannel();


    /**
     * 
     * @return InputBuffer, der die Daten auf dem rechten band beinhaltet (vorsortiert!) 
     */
    public InputBuffer readRightChannel();


    /**
     * Schließt alle offen FileChannels und gibt den Pfad der sortierten Datei zurück.
     */
    public String completeSort();
}
