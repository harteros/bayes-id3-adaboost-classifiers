/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

public class Bayes extends Classifier {

    private Hashtable<String, ArrayList<Hashtable<String, Integer>>> FeaturesPerCategory;//hashtable containing for each category an arraylist
    //where each spot of the arraylist contains the possible values of the specific attribute and how many times they occur in that category
    private Hashtable<String, Integer> CategoryNumber;//hashtable containing for each category the number of times it occurs

    public Bayes() {

        FeaturesPerCategory = new Hashtable<String, ArrayList<Hashtable<String, Integer>>>();
        CategoryNumber = new Hashtable<String, Integer>();
    }

    public void learn(Dataset data) {

        super.learn(data);
        for (int i = 0; i < data.size(); i++) {
            learn(data.get(i));
        }
    }

    private void learn(String line) {

        String[] features = getFeatures(line);
        if (features == null) return;
        String category = getCategory(line);
        IncreaseCategory(category);
        IncreaseFeaturesPerCategory(features, category);

    }

    protected String predict(String line) {

        String[] features = getFeatures(line);
        Enumeration<String> category_iter = FeaturesPerCategory.keys();// enumerates the categories of the dataset
        double max = -1;
        String max_category = "-1";

        while (category_iter.hasMoreElements()) {//for each category

            double chance = 1;
            String category_i = category_iter.nextElement();
            ArrayList<Hashtable<String, Integer>> categoryFeatures = FeaturesPerCategory.get(category_i);    //get the attributes of the category

            for (int i = 0; i < numOfAtr; i++) {//for each attribute
                Hashtable<String, Integer> feature_i = categoryFeatures.get(i);//get the possible values of the attribute
                int count;
                if (feature_i.get(features[i]) == null) {//if the feature doesnt exist in the category
                    count = 0;
                } else {
                    count = feature_i.get(features[i]);
                }
                chance *= ((float) (count + 1) / (float) (CategoryNumber.get(category_i) + feature_i.size()));//chance=Π(p(xi=features[i])|(c=category_i)) with laplace
            }

            chance *= ((float) CategoryNumber.get(category_i) / (float) total_features);//chance=chance*p(c=category_i)

            if (chance > max) {
                max = chance;
                max_category = category_i;
            }
        }

        return max_category;
    }

    private void IncreaseCategory(String category) {

        if (!CategoryNumber.containsKey(category)) {
            CategoryNumber.put(category, 1);
        } else {
            CategoryNumber.put(category, CategoryNumber.get(category) + 1);
        }
    }

    private void IncreaseFeaturesPerCategory(String[] features, String category) {

        if (!FeaturesPerCategory.containsKey(category)) {//if no such category yet
            FeaturesPerCategory.put(category, new ArrayList<Hashtable<String, Integer>>());//add the category to the hashtable and initialize the
            //arraylist which contains a hashtable for each feature
            ArrayList<Hashtable<String, Integer>> categoryFeatures = FeaturesPerCategory.get(category);//get the arraylist
            for (int i = 0; i < numOfAtr; i++) {
                Hashtable<String, Integer> feat = new Hashtable<String, Integer>();//create a hashtable for each spot of the arraylist
                feat.put(features[i], 1);//add each feature to the correspoing
                categoryFeatures.add(feat);//spot in the arraylist
            }
        } else {
            for (int i = 0; i < numOfAtr; i++) {//if ccategory has been created already
                Hashtable<String, Integer> feat = FeaturesPerCategory.get(category).get(i);//get the hashtable at the i-th index of the arraylist
                if (feat.containsKey(features[i])) {//if the specific feature is contained in the hashtable increase its number of occurences
                    feat.put(features[i], feat.get(features[i]) + 1);
                } else {//if the feature is not contained add it to the hastable
                    feat.put(features[i], 1);
                }
            }
        }
    }

}
