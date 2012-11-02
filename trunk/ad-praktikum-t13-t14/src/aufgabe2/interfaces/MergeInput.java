package aufgabe2.interfaces;

public interface MergeInput {

    /**
     * Gibt die Elemente vom 1. Input zurück.
     *
     * @return Array von sortierten Integer
     */
    int[] GetInput1();
	
    /**
     * Gibt die Elemente vom 2. Input zurück.
     *
     * @return Array von sortierten Integer
     */
    int[] GetInput2();
    
    /**
     * Ruft ab, Input1 und Input2 die letzten Elemente einer sortierten Blockgröße enthalten, sodass der Mergevorgang komplett erfolgen kann 
     *
     * @return Boolean, ob keine weiteren Elemente des selben Blocks folgen
     */
    boolean GetBlockComplete();
    
}
