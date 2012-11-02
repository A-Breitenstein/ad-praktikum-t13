package aufgabe2.interfaces;

public interface MergeOutput {

    /**
     * Gibt die beinhalteten Datensätze zurück mit GetNotMergedCount() Nullen am Ende 
     *
     * @return Array von sortierten Integer, am Ende stehen GetNotMergedCount() Nullen
     */
    int[] GetOutputData();
	
    /**
     * Gibt Anzahl der Elemente zurück, welche noch nicht gemerged werden konnten
     *
     * @return Integer >=0
     */
    int GetNotMergedCount();
    
    /**
     * Gibt zurück, welcher der Input-Stream nicht vollständig gemerged werden konnte (sofern GetNotMergedCount>0)
     *
     * @return InputStream-Enum
     */
    InputStream GetNotCompleteMergedStream();
    
    public enum InputStream {
        Input1, Input2
    }
    
}
