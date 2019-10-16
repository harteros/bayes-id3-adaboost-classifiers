/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.util.Arrays;

public abstract class Classifier {

    protected static final String DELIM = ",";//prefix delim for splitting datasset attributes

    protected static int total_features;//total examples of the dataset

    protected static int numOfAtr; //total attributes of the dataset

    //method called to teach the algorithm the dataset
    public void learn(Dataset data) {

        //uncomment to test accuracy of algorithms based on the size of the sub data
		/*Collections.shuffle(data.getData());
		/data.setData(new ArrayList<String>(data.getData().subList(0, 200)));
		*/

        total_features = data.size();
        numOfAtr = data.getNumOfAtr();
    }

    //method called to test how well our algorithm predicts the examples
    public void classify(Dataset data) {

        int sum = 0;
        for (int i = 0; i < data.size(); i++) {
            sum += classify(data.get(i));
        }
        System.out.println("Accuracy: " + (float) sum / data.size());
    }

    //method to predict one data
    public int classify(String line) {

        String prediction = predict(line);
        String category = getCategory(line);
        System.out.println("Answered: " + prediction + "  Correct: " + category);
        if (prediction.equals(category)) return 1;
        else return 0;
    }

    //method that returns the prediction of the aglorithm for a specific data
    protected abstract String predict(String line);

    //method that returns the features of one line of data
    protected String[] getFeatures(String line) {

        String[] features = line.split(DELIM);
        features = Arrays.copyOfRange(features, 0, numOfAtr);
        return features;
    }

    //method that returns the category of the data
    protected String getCategory(String line) {
        return line.split(DELIM)[numOfAtr];
    }

}
