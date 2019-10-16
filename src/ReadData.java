/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.io.*;
import java.util.ArrayList;


public class ReadData {

    //returns an arraylist containing the data of the file
    public ArrayList<String> read(String file) {
        ArrayList<String> data = new ArrayList<String>();
        File f = null;
        BufferedReader reader = null;
        String line;
        try {
            f = new File(file);
        } catch (NullPointerException e) {
            System.err.println("File not found.");
        }
        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file!");
        }
        try {
            line = reader.readLine();
            boolean first = true;
            int numOfAtr = -1;
            int i = 0;
            while (line != null) {
                i++;
                if (first) {
                    numOfAtr = line.split(",").length;
                    first = false;
                }
                if (line.split(",").length == numOfAtr) {
                    data.add(line);
                } else {
                    System.out.println("Error in data. Line skipped : " + i);
                }
                line = reader.readLine();
            }
            return data;

        } catch (IOException e) {
            System.out.println("Error reading line ");
        }
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing file.");
        }
        return null;
    }


}
