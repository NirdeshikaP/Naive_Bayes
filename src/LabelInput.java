/**
 * Data structure to hold label and input of an example
 */
public class LabelInput {
    private int[] label;
    private double[][] input;

    public LabelInput(int[] label, double[][] input){
        this.input = input;
        this.label = label;
    }

    public int[] getLabel(){
        return label;
    }

    public double[][] getInput(){
        return input;
    }
}
