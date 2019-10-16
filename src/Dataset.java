/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class Dataset {
    private ReadData reader = new ReadData();
    public static int numOfAtr;
    private ArrayList<String> data;
    private static final String DELIM = ",";

    public Dataset(String file) {
        data = reader.read(file);
    }


    public Dataset(ArrayList<String> data) {
        this.data = data;
    }

    public Dataset() {
        this.data = new ArrayList<String>();
    }

    public int size() {
        return data.size();
    }

    public void add(String line) {
        data.add(line);
    }

    public String get(int i) {
        return data.get(i);
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> d) {
        data = d;
    }

    //returns the number of attributes of the dataset
    public int getNumOfAtr() {
        if (!data.isEmpty()) {
            return data.get(0).split(DELIM).length - 1;
        }
        return -1;
    }

    //returns the entropy of the dataset
    public double getEntropy() {
        Hashtable<String, Integer> conclusion = getCategories();
        Enumeration<String> categories = conclusion.keys();
        double entropy = 0;
        while (categories.hasMoreElements()) {
            String category = categories.nextElement();
            double c_chance = (float) conclusion.get(category) / data.size();
            entropy += -(c_chance * Util.log2(c_chance));
        }
        return entropy;
    }

    //returns a sum that is the entropy of the dataset if we know the value of a specific attribute multiplied by the chance of that attribute occuring
    public double getFeatureEntropy(int feature_i) {
        Hashtable<String, ArrayList<Hashtable<String, Integer>>> f_categories = getFeaturesPerCategory();
        Hashtable<String, Integer> features = getFeatures().get(feature_i);

        Enumeration<String> feature = features.keys();
        double entropy = 0;

        while (feature.hasMoreElements()) {//while the feature has more possible values
            String feat = feature.nextElement();
            Enumeration<String> categories = f_categories.keys();
            double entropy_i = 0;

            while (categories.hasMoreElements()) {//for each category
                double chance = 0;
                String category = categories.nextElement();

                if (f_categories.get(category).get(feature_i).containsKey(feat)) {//if the category contains the value of the feature

                    chance = (float) f_categories.get(category).get(feature_i).get(feat) / features.get(feat);
                }

                if (chance != 0) {
                    entropy_i += -((float) chance * Util.log2(chance));
                }

            }
            entropy_i *= (float) features.get(feat) / data.size();
            entropy += (float) entropy_i;
        }


        return entropy;
    }

    //returns the information gain of a specific attribute
    public double InfoGain(int feature_i) {
        return getEntropy() - getFeatureEntropy(feature_i);
    }

    public Hashtable<String, Integer> getCategories() {
        Hashtable<String, Integer> count = new Hashtable<String, Integer>();
        for (int i = 0; i < data.size(); i++) {
            String category = getCategory(data.get(i));
            if (!count.containsKey(category)) {
                count.put(category, 1);
            } else {
                count.put(category, count.get(category) + 1);
            }
        }
        return count;
    }

    public ArrayList<Hashtable<String, Integer>> getFeatures() {
        ArrayList<Hashtable<String, Integer>> count = new ArrayList<Hashtable<String, Integer>>();
        for (int j = 0; j < data.size(); j++) {
            String[] feature = getFeatures(data.get(j));//get the features of the data
            for (int i = 0; i < feature.length; i++) {
                if (count.size() != feature.length) {//if its the first loop
                    Hashtable<String, Integer> feat = new Hashtable<String, Integer>();//create hashtable for each feature
                    feat.put(feature[i], 1);
                    count.add(feat);
                } else {
                    if (!count.get(i).containsKey(feature[i])) {

                        count.get(i).put(feature[i], 1);
                    } else {
                        count.get(i).put(feature[i], count.get(i).get(feature[i]) + 1);

                    }
                }
            }
        }
        return count;
    }

    public Hashtable<String, ArrayList<Hashtable<String, Integer>>> getFeaturesPerCategory() {
        Hashtable<String, ArrayList<Hashtable<String, Integer>>> count = new Hashtable<String, ArrayList<Hashtable<String, Integer>>>();

        for (int j = 0; j < data.size(); j++) {

            String category = getCategory(data.get(j));
            String[] feature = getFeatures(data.get(j));

            if (!count.containsKey(category)) {//if no such category yet
                count.put(category, new ArrayList<Hashtable<String, Integer>>());//add the category to the hashtable and initialize the
                //arraylist which contains a hashtable for each feature
                ArrayList<Hashtable<String, Integer>> categoryFeatures = count.get(category);//get the arraylist
                for (int i = 0; i < feature.length; i++) {
                    Hashtable<String, Integer> feat = new Hashtable<String, Integer>();//create a hashtable for each spot of the arraylist
                    feat.put(feature[i], 1);//add each feature to the correspoing
                    categoryFeatures.add(feat);//spot in the arraylist
                }
            } else {
                for (int i = 0; i < feature.length; i++) {//if category has been created already
                    Hashtable<String, Integer> feat = count.get(category).get(i);//get the hashtable at the i-th index of the arraylist
                    if (feat.containsKey(feature[i])) {//if the specific feature is contained in the hashtable increase its number of occurences
                        feat.put(feature[i], feat.get(feature[i]) + 1);
                    } else {//if the feature is not contained add it to the hastable
                        feat.put(feature[i], 1);
                    }
                }
            }
        }
        return count;

    }

    //returns the category that occurs the most in the dataset
    public String getMaxCategory() {
        Hashtable<String, Integer> most = getCategories();
        int max = -1;
        String max_category = "-1";
        for (String value : most.keySet()) {
            if (most.get(value) > max) {
                max = most.get(value);
                max_category = value;
            }
        }
        return max_category;
    }

    //returns a double showing if all data are the same category (if all same returns 0 else a decimal showing % that are same)
    public double allSame() {
        String category = getCategory(data.get(0));
        int count = 0;
        for (int i = 1; i < data.size(); i++) {
            if (!category.equals(getCategory(data.get(i)))) {
                count++;
            }
        }
        return (float) count / data.size();
    }


    protected String[] getFeatures(String line) {
        String[] features = line.split(DELIM);
        features = Arrays.copyOfRange(features, 0, features.length - 1);
        return features;
    }

    protected String getCategory(String line) {
        String[] features = line.split(DELIM);
        return features[features.length - 1];

    }

}
