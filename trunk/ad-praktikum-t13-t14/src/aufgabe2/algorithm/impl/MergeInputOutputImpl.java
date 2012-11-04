package aufgabe2.algorithm.impl;


import aufgabe2.interfaces.DataWrapper;
import aufgabe2.interfaces.MergeInputOutput;

/**
 * Created with IntelliJ IDEA.
 * User: chrisch
 * Date: 04.11.12
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public class MergeInputOutputImpl implements MergeInputOutput {

    /* MergeInputOutput ist nur Verpackung für das, was die methode read() aus DataManager liefert,
     nämlich ein Dreielementiges Array  mit den beiden input-arrays und dem Output-array */


    private DataWrapper input1;

    private DataWrapper input2;

    private DataWrapper output;  //



    private MergeInputOutputImpl(DataWrapper[] in1In2AndOut) {
        //
          // inhalt von in1In2AndOut: [input1,input2,output]
        input1 = in1In2AndOut[0];
        input2 = in1In2AndOut[1];
        output = in1In2AndOut[2];
       }







    @Override
    public DataWrapper GetOutputData() {
       return output;
    }

    @Override
    public InputStream GetNotCompleteMergedStream() {
        return null; //wie soll ich das Implementieren? Eigentlich kann das doch nur der DataManager wissen, oder?
    }

    @Override
    public DataWrapper GetInput1() {
       return input1;
    }

    @Override
    public DataWrapper GetInput2() {
        return input2;
    }

    @Override
    public boolean isBlockComplete() {
        return false;  //wie soll ich das implementieren?
    }
}
