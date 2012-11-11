package aufgabe2.interfaces;

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
     * @return DataWrapper mit angegebenen Daten
     */
    public DataWrapper createOuputChannel();

    // 6.11.2012:
    //Sowas kommt in die JavaDocs!

    /**
     * 
     * @return DataWrapper, der die Daten auf dem linken band beinhaltet (vorsortiert!) 
     */
    public DataWrapper readLeftChannel();


    /**
     * 
     * @return DataWrapper, der die Daten auf dem rechten band beinhaltet (vorsortiert!) 
     */
    public DataWrapper readRightChannel();

    /**
     * Prädikat für die existenz einer kommenden Folge auf dem Channel
     *
     * @return true if the Channel has a sequence left to give, else false
     */
    public boolean leftChannelHasNext();

    /**
     * Prädikat für die existenz einer kommenden Folge auf dem Channel
     *
     * @return true if the Channel has a sequence left to give, else false
     */
    public boolean rightChannelHasNext();

    /**
     *  Loescht alle Dateien bis auf die, die dieselbe Groeße hat wie die Ausgangsdatei.
     *  Diese wird umbenannt.(Umbenennen, dann rest löschen)
     *  
     *  @return der vollständige Pfad der sortierten Datei
     */
    public String signSortedFile();

    /**
     * Schließt alle offen FileChannels.
     */
    public void closeAllChannelsIfOpen();
}
