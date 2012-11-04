package aufgabe2.interfaces;

@Deprecated
public interface MergeOutput {

    /**
     * Gibt die beinhalteten Datensätze zurück ,eventuell mit Nullen am Ende
     *
     * @return Verpackter Array von sortierten Integer
     */
     DataWrapper GetOutputData();
	
    /**
     * Gibt Anzahl der Elemente zurück, welche noch nicht gemerged werden konnten
     * Brauchen wir nicht mehr, da die Arrays in DataWrappern verpackt werden.
     * @return Integer >=0
     */
    @Deprecated
    int GetNotMergedCount();
    
    /**
     * Gibt zurück, welcher der Input-Stream nicht vollständig gemerged werden konnte (sofern GetNotMergedCount>0)
     *
     * @return InputStream-Enum
     */
    InputStream GetNotCompleteMergedStream();
    
    public enum InputStream {
        Input1, Input2
        // Eigentlich sollten Enums komplett in Großbuchstaben geschrieben werden, weil sie Konstanten sind.
    }
    
}
