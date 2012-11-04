package aufgabe2.interfaces;

@Deprecated
public interface MergeInput {

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
    boolean GetBlockComplete();
    
}
