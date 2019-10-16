/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.util.ArrayList;
import java.util.Set;

public class AdaBoost extends Classifier {

    private ArrayList<Tree> stumps;
    private ArrayList<Double> weights;
    private Set<String> categories;

    public AdaBoost() {

        this.stumps = new ArrayList<Tree>();
        this.weights = new ArrayList<Double>();
    }

    public void learn(Dataset data) {

        super.learn(data);
        categories = data.getCategories().keySet();
        double[] w = initializeWeights(data.size());
        double error;

        Tree min = null;
        double min_err = 10;
        ArrayList<Integer> features = new ArrayList<Integer>();
        for (int i = 0; i < numOfAtr; i++) {//for a total of the number of the attributes

            double max_gain = -1;
            int feat = -1;
            for (int j = 0; j < numOfAtr; j++) {//find the feature with highest info gain
                double gain = data.InfoGain(j);
                if (gain > max_gain && !features.contains(j)) {
                    max_gain = gain;
                    feat = j;
                }
            }
            features.add(feat);//add the feature the inferred ones
            error = calculateError(w, data, Tree.getStump(data).get(feat));// calculate the error for the feature using the correspoinding decision stump
            min_err = error;
            min = Tree.getStump(data).get(feat);
			
			/* *******tropos 2**************
			for(Tree s: Tree.getStump(data)){
				error=calculateError(w,data,s);

				if(error<min_err){
					min_err=error;
					min=s;

				}
			}
			System.out.println(min.feature + "  "+min_err);
			********telos tropos 2*********** */

            stumps.add(min);//add stump to the array with the weak learners
            w = adjustWeights(w, min_err, data, min);//adjust the weights of the data
            w = normalize(w);//normalize weight to that it sums to 1
            weights.add(Math.log((1 - min_err) / min_err));//add the weight of the weak learner to the weights


        }

    }

    protected String predict(String e) {
        return weightedMajority(e);
    }

    private String weightedMajority(String e) {

        double max = -1;
        String max_category = "";
        for (String category : categories) {

            double count = 0;
            for (int i = 0; i < stumps.size(); i++) {//for each stump check which category do the stumps categorize the data
                if (category.equals(stumps.get(i).predict(e))) {
                    count += weights.get(i);
                }
            }

            if (count > max) {//the category with the highest weights for the stumps is the one that is returned
                max = count;
                max_category = category;
            }
        }

        return max_category;
    }

    //returns an array with the data size with each value being 1/size initially
    private double[] initializeWeights(int size) {

        double[] weights = new double[size];
        for (int i = 0; i < size; i++) {
            weights[i] = (float) 1 / size;
        }
        return weights;
    }

    //returns the error of the stump
    private double calculateError(double[] weight, Dataset data, Tree stump) {

        double error = 0.0;
        for (int i = 0; i < weight.length; i++) {//for each data
            String e = data.get(i);
            if (!(stump.predict(e).equals(data.getCategory(e)))) {//if the stump does not categorize correctly the data
                error = error + weight[i];//increase the error by the weight of the data
            }
        }
        return error;
    }

    //returns an array with the new weights of the data
    private double[] adjustWeights(double[] weight, double error, Dataset data, Tree stump) {

        double err = error / (1.0 - error);
        for (int j = 0; j < data.size(); j++) {//for each data
            String e = data.get(j);
            if (stump.predict(e).equals(data.getCategory(e))) {//if the stump does categorize correctly the data
                weight[j] = weight[j] * err;//reduce the weight of the data

            }
        }
        return weight;
    }

    //returns an array where the sum of all the values goes to 1
    private double[] normalize(double[] weights) {

        int size = weights.length;
        double total = 0.0;

        for (double weight : weights) {//sums all weights to find the total of all the data
            total = total + weight;
        }

        double[] normalized = new double[size];
        if (total != 0) {
            for (int i = 0; i < size; i++) {//for each data divide its weight with the total weight to reduce it to the range of 0-1
                normalized[i] = weights[i] / total;
            }
        }
        return normalized;
    }

}
