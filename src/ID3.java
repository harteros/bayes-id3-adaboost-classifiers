/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ID3 extends Classifier {

    private Tree decisiontree;

    public ID3() {
    }

    public ID3(Tree tree) {
        this.decisiontree = tree;
    }

    public void learn(Dataset data) {

        super.learn(data);
        List<Integer> a = new ArrayList<Integer>();
        String[] features = data.getFeatures(data.get(0));
        for (int i = 0; i < features.length; i++) {//initialize available features
            a.add(i);
        }
        decisiontree = learnTree(data, a, new TreeLeaf("root", data));
    }

    //returns a decision tree model based on the dataset
    private Tree learnTree(Dataset data, List<Integer> atr, TreeLeaf category) {

        if (data.size() == 0) return category;
        if (data.allSame() == 0) return new TreeLeaf(data.getCategory(data.get(0)), data);
        else if (data.allSame() > 0.9) return new TreeLeaf(data.getMaxCategory(), data);
        if (atr.isEmpty()) return new TreeLeaf(data.getMaxCategory(), data);

        int best = best_feature(data, atr);
        Tree root = new Tree(Integer.toString(best), data);
        TreeLeaf m = new TreeLeaf(data.getMaxCategory(), data);
        Set<String> values = data.getFeatures().get(best).keySet();
        //double[] bins=Util.getBins(values,data.getNumOfAtr());
        //if(bins==null){
        for (String value : values) {

            Dataset sub = new Dataset();

            for (int i = 0; i < data.size(); i++) {
                if (data.getFeatures(data.get(i))[best].equals(value)) {
                    sub.add(data.get(i));
                }
            }

            List<Integer> new_atr = remove(atr, best);
            Tree subtree = learnTree(sub, new_atr, m);
            root.addNode(value, sub, subtree);
        }
		/*}else{
			for(int j=0; j<bins.length; j++){
				Dataset sub=new Dataset();
				for(int i=0; i<data.size(); i++){
					if(j==0){
						if(Double.parseDouble(data.getFeatures(data.get(i))[best])<=bins[j]){
							sub.add(data.get(i));
						}
					}else{
						if((Double.parseDouble(data.getFeatures(data.get(i))[best])>bins[j-1]) && (Double.parseDouble(data.getFeatures(data.get(i))[best])<=bins[j])){
							sub.add(data.get(i));
						}
					}
				}
				List<Integer> new_atr=remove(atr,best);
				Tree subtree=learnTree(sub,new_atr,m);
				root.addNode(String.valueOf(bins[j]),sub, subtree);
			}	
		}*/
        return root;
    }

    protected String predict(String line) {

        return decisiontree.predict(line);
    }

    public Tree getTree() {

        return decisiontree;
    }

    //removes a feature from the available features
    private static List<Integer> remove(List<Integer> atr, Object value) {

        List<Integer> newList = new ArrayList<Integer>(atr);
        newList.remove(value);
        return newList;
    }

    //returns the feature with the highest info gain
    private int best_feature(Dataset data, List<Integer> table) {

        double max = -1;
        int category = -1;
        for (int i = 0; i < data.getFeatures().size(); i++) {

            if (table.contains(i)) {
                double value = data.InfoGain(i);

                if (value > max && table.contains(i)) {
                    max = value;
                    category = i;
                }
            }
        }
        return category;
    }

}
