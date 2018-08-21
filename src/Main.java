/**
 * The main class which calls Naive Bayes Classifier
 */
public class Main {
    public static void main(String[] args) {
        NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
        System.out.println("Accuracy = " + naiveBayesClassifier.getAccuracy());
        System.out.println("Precision = " + naiveBayesClassifier.getPrecision());
        System.out.println("Recall = " + naiveBayesClassifier.getRecall());
    }
}
