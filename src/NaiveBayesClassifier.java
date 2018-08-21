import javax.swing.*;
import java.awt.*;
import static java.lang.Math.max;

/**
 * Class that implements Naive Bayes Classifier
 */
public class NaiveBayesClassifier {

    private double[][] training_input;
    private int[] training_label;

    private double[][] testing_input;
    private int[] testing_label;

    private double probabilityOfSpam;
    private double probabilityOfNonSpam;

    private double[] mean_spam = new double[Constants.NUMBER_OF_FEATURES];
    private double[] standardDeviation_spam = new double[Constants.NUMBER_OF_FEATURES];

    private double[] mean_nonSpam = new double[Constants.NUMBER_OF_FEATURES];
    private double[] standardDeviation_nonSpam = new double[Constants.NUMBER_OF_FEATURES];

    private int[] test_output = new int[Constants.NUMBER_OF_TESTING_INSTANCES];

    private int numberOfTruePositives;
    private int numberOfFalsePositives;

    private int numberOfTrueNegatives;
    private int numberOfFalseNegatives;

    private double accuracy;
    private double precision;
    private double recall;

    /**
     * Getter method for Accuracy
     */
    public double getAccuracy(){
        return accuracy;
    }

    /**
     * Getter method for Precision
     */
    public double getPrecision(){
        return precision;
    }

    /**
     * Getter method for Recall
     */
    public double getRecall(){
        return recall;
    }

    /**
     * Naive Bayes Classifier constructor that calls the appropriate methods for the classification in order.
     */
    public NaiveBayesClassifier() {
        /*Read input for test and training data*/
        LabelInput trainingLabelInput = new ReadCSV("spambase/Training.csv", true).readCSV();
        LabelInput testingLabelInput = new ReadCSV("spambase/Testing.csv", false).readCSV();

        training_input = trainingLabelInput.getInput();
        training_label = trainingLabelInput.getLabel();

        testing_input = testingLabelInput.getInput();
        testing_label = testingLabelInput.getLabel();

        calculatePriorProbabilities();
        calculateMeanAndStandardDeviation();

        for (int i = 0; i < Constants.NUMBER_OF_TESTING_INSTANCES; i++) {
            test_output[i] = determineTheClass(testing_input[i]);
        }

        calculate_TP_FP_TN_FN();

        accuracy = calculateAccuracy();
        precision = calculatePrecision();
        recall = calculateRecall();

        showConfusionMatrix();
    }

    /**
     * Calculate the prior probabilities for the class spam and non-spam from the training data
     */
    private void calculatePriorProbabilities() {
        int spamCount = 0;
        int nonSpamCount = 0;

        for (int i = 0; i < training_label.length; i++) {
            if (training_label[i] == Spam_NonSpam.NON_SPAM.ordinal()) {
                nonSpamCount++;
            } else {
                spamCount++;
            }
        }

        probabilityOfSpam = (double) spamCount / Constants.NUMBER_OF_TRAINING_INSTANCES;
        probabilityOfNonSpam = (double) nonSpamCount / Constants.NUMBER_OF_TRAINING_INSTANCES;
    }

    /**
     * Calculate the mean and standard deviation for each feature and each class for the training data
     */
    private void calculateMeanAndStandardDeviation() {
        for (int f = 0; f < Constants.NUMBER_OF_FEATURES; f++) {
            double spam_sum = 0;
            int spam_count = 0;

            double nonSpam_sum = 0;
            int nonSpam_count = 0;

            for (int i = 0; i < Constants.NUMBER_OF_TRAINING_INSTANCES; i++) {
                if (training_label[i] == Spam_NonSpam.SPAM.ordinal()) {
                    spam_sum += training_input[i][f];
                    spam_count++;
                } else {
                    nonSpam_sum += training_input[i][f];
                    nonSpam_count++;
                }
            }
            mean_spam[f] = spam_sum / spam_count;
            mean_nonSpam[f] = nonSpam_sum / nonSpam_count;
        }

        for (int f = 0; f < Constants.NUMBER_OF_FEATURES; f++) {
            double spam_sum = 0;
            int spam_count = 0;

            double nonSpam_sum = 0;
            int nonSpam_count = 0;

            for (int i = 0; i < Constants.NUMBER_OF_TRAINING_INSTANCES; i++) {
                if (training_label[i] == Spam_NonSpam.SPAM.ordinal()) {
                    spam_sum += Math.pow((training_input[i][f] - mean_spam[f]), 2);
                    spam_count++;
                } else {
                    nonSpam_sum += Math.pow((training_input[i][f] - mean_nonSpam[f]), 2);
                    nonSpam_count++;
                }
            }
            standardDeviation_spam[f] = max((spam_sum / spam_count), 0.0001);
            standardDeviation_nonSpam[f] = max((nonSpam_sum / nonSpam_count), 0.0001);
        }
    }

    /**
     * Determines the class by calculating the probabilities of an input to belong to each class using Bayes rule
     * and assigns the class that has higher probability
     * @param input
     * @return The class that it belongs to.
     */
    private int determineTheClass(double[] input) {
        double sumOfProbabilities_Spam = Math.log(probabilityOfSpam);
        double sumOfProbabilities_NonSpam = Math.log(probabilityOfNonSpam);

        for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
            sumOfProbabilities_Spam += calculateLogOfConditionalProbability(input[i], mean_spam[i], standardDeviation_spam[i]);
            sumOfProbabilities_NonSpam += calculateLogOfConditionalProbability(input[i], mean_nonSpam[i], standardDeviation_nonSpam[i]);
        }

        return sumOfProbabilities_NonSpam > sumOfProbabilities_Spam ? Spam_NonSpam.NON_SPAM.ordinal() : Spam_NonSpam.SPAM.ordinal();
    }

    /**
     * Calculate the log of conditional probability using Gaussian formula
     * @param x
     * @param mean
     * @param standardDeviation
     * @return Log of conditional probability
     */
    private double calculateLogOfConditionalProbability(double x, double mean, double standardDeviation) {
        double exponent = Math.pow(x - mean, 2) / (2 * Math.pow(standardDeviation, 2));
        double multiplicand = 1 / (Math.sqrt(2 * Math.PI) * standardDeviation);
        double product = multiplicand * Math.pow(Math.E, -1 * exponent);
        return Math.log(product);
    }

    /**
     * Calculate Accuracy = (TN+TP) / (TN+TP+FN+FP)
     * @return Accuracy
     */
    private double calculateAccuracy() {
        return (double) (numberOfTruePositives + numberOfTrueNegatives) / Constants.NUMBER_OF_TESTING_INSTANCES;
    }

    /**
     * Calculate Precision = TP / (TP+FP)
     * @return Precision
     */
    private double calculatePrecision() {
        return (double) numberOfTruePositives / (numberOfTruePositives + numberOfFalsePositives);
    }

    /**
     * Calculate Recall = TP / (TP+FN
     * @return Recall
     */
    private double calculateRecall() {
        return (double) numberOfTruePositives / (numberOfTruePositives + numberOfFalseNegatives);
    }

    /**
     * Calculate number of true positives, true negatives, false positives and false negatives
     */
    private void calculate_TP_FP_TN_FN() {
        for (int i = 0; i < Constants.NUMBER_OF_TESTING_INSTANCES; i++) {
            if (testing_label[i] == Spam_NonSpam.SPAM.ordinal()) {
                if (testing_label[i] == test_output[i])
                    numberOfTruePositives++;
                else
                    numberOfFalseNegatives++;
            } else {
                if (testing_label[i] == test_output[i])
                    numberOfTrueNegatives++;
                else
                    numberOfFalsePositives++;
            }
        }
    }

    /**
     * Show confusion matrix
     */
    private void showConfusionMatrix(){
        JTable table = new JTable(3,3);
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);

        table.setValueAt("Predicted_Spam",0,1);
        table.setValueAt("Predicted_NonSpam", 0,2);

        table.setValueAt("Actual_Spam",1,0);
        table.setValueAt("Actual_NonSpam", 2,0);

        table.setValueAt(numberOfTruePositives,1,1);
        table.setValueAt(numberOfFalseNegatives,1,2);
        table.setValueAt(numberOfFalsePositives,2,1);
        table.setValueAt(numberOfTrueNegatives,2,2);

        JFrame frame = new JFrame("Confusion Matrix");
        frame.getContentPane().add(table);

        frame.pack();
        frame.setVisible(true);
    }
}
