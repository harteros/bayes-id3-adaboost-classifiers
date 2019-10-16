
/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

public class TreeLeaf extends Tree {

    private String conclusion;

    public TreeLeaf(String value, Dataset data) {
        super.data = data;
        this.conclusion = value;
    }

    public String predict(String e) {
        return conclusion;
    }

    public String toString(int depth, StringBuffer buf) {

        buf.append("category -> " + conclusion + "\n");
        buf.append("****end decision***\n");

        return buf.toString();
    }


}

