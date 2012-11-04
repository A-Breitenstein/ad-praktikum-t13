package aufgabe2.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: chrisch
 * Date: 04.11.12
 * Time: 20:39
 */
public interface MergeInputOutput {

   //kombiniertes interface aus MergeInput und MergeOutput.
    /**
     * Gibt die beinhalteten Datensätze zurück ,eventuell mit Nullen am Ende
     *
     * @return Verpackter Array von sortierten Integer
     */
    DataWrapper GetOutputData();



    /**
     * Gibt zurück, welcher der Input-Stream nicht vollständig gemerged werden konnte (sofern GetNotMergedCount>0)
     *
     * @return InputStream-Enum
     */
    InputStream GetNotCompleteMergedStream();

    public enum InputStream {
        INPUT1, INPUT2
        // Eigentlich sollten Enums komplett in Großbuchstaben geschrieben werden, weil sie Konstanten sind.
    }

    /**
     * Gibt die Elemente vom 1. Input zurück.
     *
     * @return Verpackter Array von sortierten Integer
     */
    DataWrapper GetInput1();

    /**
     * Gibt die Elemente vom 2. Input zurück.
     *
     * @return Verpackter Array von sortierten Integer
     */
    DataWrapper GetInput2();

    /**
     * Ruft ab, Input1 und Input2 die letzten Elemente einer sortierten Blockgröße enthalten, sodass der Mergevorgang komplett erfolgen kann
     *
     * @return Boolean, ob keine weiteren Elemente des selben Blocks folgen
     */
    boolean isBlockComplete();



}
