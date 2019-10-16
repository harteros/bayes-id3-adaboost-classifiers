/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class Tree {

    protected String feature;
    protected Dataset data;
    protected Hashtable<String, Tree> node;

    public Tree() {
    }

    public Tree(String feature, Dataset data) {
        this.feature = feature;
        this.data = data;
        this.node = new Hashtable<String, Tree>();
    }

    public String predict(String e) {


        String[] features = e.split(",");

        if (node.containsKey(features[Integer.parseInt(feature)])) {//if the node contains the specific value of the feature
            return node.get(features[Integer.parseInt(feature)]).predict(e);

        } else if (Util.isNumeric(features[Integer.parseInt(feature)])) {//if not but the value is numeric

            double closest = 0;
            String chosen = "";
            boolean first = true;
            for (String k : node.keySet()) {//for each key of the node check which one is closer to the one we need to predict
                if (Util.isNumeric(k)) {
                    if (first) {
                        closest = Math.abs(Double.parseDouble(k) - Double.parseDouble(features[Integer.parseInt(feature)]));
                        chosen = k;
                        first = false;
                    } else {
                        double min = Math.abs(Double.parseDouble(k) - Double.parseDouble(features[Integer.parseInt(feature)]));
                        if (min < closest) {
                            closest = min;
                            chosen = k;
                        }
                    }
                }
            }
            return node.get(chosen).predict(e);
        } else {//if the data was never seen during training return the category that occurs the most in the data of the node
            return data.getMaxCategory();

        }
    }

    //predicts based on bins for numeric datasets mainly (need to uncomment in ID3 code)
    public String predictNumeric(String e) {

        String[] features = e.split(",");

        if (node.containsKey(features[Integer.parseInt(feature)])) {
            return node.get(features[Integer.parseInt(feature)]).predict(e);

        } else if (Util.isNumeric(features[Integer.parseInt(feature)])) {
            String chosen = "";
            double[] bins = Util.getNumericArray(node.keySet().toArray(new String[node.keySet().size()]));
            Arrays.sort(bins);
            for (int i = 0; i < bins.length; i++) {
                if (Double.parseDouble(features[Integer.parseInt(feature)]) <= bins[i]) {

                    chosen = String.format("%.2f", bins[i]);
                    break;
                }
            }
            if (chosen.equals("")) chosen = String.valueOf(bins[bins.length - 1]);
            if (!node.containsKey(chosen)) chosen = String.format("%.1f", Double.parseDouble(chosen));

            return node.get(chosen).predict(e);
        } else {
            return data.getMaxCategory();
        }
    }

    public void addNode(String feature, Dataset data, Tree node) {
        this.node.put(feature, node);
    }

    public void addLeaf(String feature, String conclusion, Dataset data) {
        this.node.put(feature, new TreeLeaf(conclusion, data));
    }

    //returns an arraylist with decision stumps for each feature
    public static ArrayList<Tree> getStump(Dataset data) {

        int[] features = new int[data.getFeatures(data.get(0)).length];
        for (int i = 0; i < features.length; i++) {
            features[i] = i;
        }
        ArrayList<Tree> stumps = new ArrayList<Tree>();

        for (int i = 0; i < features.length; i++) {//for each feature

            Tree stump = new Tree(Integer.toString(i), data);

            for (String feature_name : data.getFeatures().get(i).keySet()) {//for each feature possible value
                Dataset sub = new Dataset();
                for (int k = 0; k < data.size(); k++) {
                    if (data.get(k).split(",")[i].equals(feature_name)) {
                        sub.add(data.get(k));
                    }
                }

                if (sub.size() != 0) {

                    stump.addLeaf(sub.get(0).split(",")[i], sub.getMaxCategory(), sub);
                }
            }
            stumps.add(stump);
        }
        return stumps;
    }

    public String toString() {
        return toString(1, new StringBuffer());
    }

    public String toString(int depth, StringBuffer buf) {

        if (feature != null) {


            buf.append(feature + " \n");

            for (String attributeValue : node.keySet()) {

                Tree child = node.get(attributeValue);
                buf.append("\n");

                buf.append("akmi-->" + attributeValue);
                buf.append("\n");
                buf.append("	|	");
                buf.append("\n");
                buf.append("	v	");
                buf.append("\n");
                buf.append("Neo bathos : " + depth);
                buf.append("\n");
                buf.append("komvos megethous : " + child.data.size());
                buf.append("\n");

                buf.append(child.toString(depth + 1, new StringBuffer()));
            }
        }

        return buf.toString();
    }

}
