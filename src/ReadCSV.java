import java.io.*;

/**
 * Class to read csv files to read data
 */
public class ReadCSV {
    String fileName;

    double[][] input;
    int[] label;

    /**
     * Constructor
     * @param fileName Name of the csv file to be read
     * @param isTrainingDataSet Boolean to check if it is training or test dataset
     */
    public ReadCSV(String fileName, boolean isTrainingDataSet) {
        this.fileName = fileName;
        if (isTrainingDataSet) {
            input = new double[Constants.NUMBER_OF_TRAINING_INSTANCES][Constants.NUMBER_OF_FEATURES];
            label = new int[Constants.NUMBER_OF_TRAINING_INSTANCES];
        } else {
            input = new double[Constants.NUMBER_OF_TESTING_INSTANCES][Constants.NUMBER_OF_FEATURES];
            label = new int[Constants.NUMBER_OF_TESTING_INSTANCES];
        }
    }

    /**
     * Reads csv file, parses and returns the inputs and corresponding labels
     */
    public LabelInput readCSV() {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            int i = 0;

            while ((line = bufferedReader.readLine()) != null) {
                String[] features = line.split(",");
                int f;
                for (f = 0; f < features.length - 1; f++) {
                    input[i][f] = Double.parseDouble(features[f]);
                }

                label[i] = Integer.parseInt(features[f]);
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " is not found");
            System.exit(10);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(10);
        }

        return new LabelInput(label,input);
    }
}
