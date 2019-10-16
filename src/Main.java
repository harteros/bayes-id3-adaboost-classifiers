
/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

public class Main {

    public static void main(String[] args) {

        Classifier classifier = null;
        int i = 1;
        if (i == 1) {
            classifier = new Bayes();
        } else if (i == 2) {
            classifier = new ID3();
        } else if (i == 3) {
            classifier = new AdaBoost();
        }
        String train_data = "vote.data.txt";
        String test_data = "vote.test.txt";
        Dataset train = new Dataset("data/" + train_data);
        Dataset test = new Dataset("data/" + test_data);
        if (classifier != null) {
            classifier.learn(train);
            classifier.classify(test);
        } else {
            System.out.println("Wrong choice");
        }
    }
}
