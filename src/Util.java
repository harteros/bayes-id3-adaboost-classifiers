/*
 *  Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class Util {

    //returns the log2 of a number
    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    //checks if a string is numeric
    public static boolean isNumeric(String str) {
        boolean dot = false;
        int i = 0;
        boolean wait = false;


        for (char c : str.toCharArray()) {
            if (i == 0) {
                if (Character.isDigit(c) && Character.toString(c).equals("0")) {
                    wait = true;

                }

            }
            if (wait && i == 1) {
                if (!Character.toString(c).equals(".")) return false;
            }
            if (!Character.isDigit(c)) {
                if (Character.toString(c).equals(".") && !dot && i != 0) {
                    dot = true;
                } else {
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    //returns an array with bins for a set with numeric values
    public static double[] getBins(Set<String> values, int numberOfBins) {

        String[] values_arr = values.toArray(new String[values.size()]);
        double min = 0;
        double max = 0;

        for (int i = 0; i < values_arr.length; i++) {
            if (isNumeric(values_arr[i])) {
                if (i == 0) {
                    min = Double.parseDouble(values_arr[i]);
                    max = Double.parseDouble(values_arr[i]);
                }
                double v = Double.parseDouble(values_arr[i]);
                if (v < min) {
                    min = v;
                } else if (v > max) {
                    max = v;
                }

            } else {
                return null;
            }
        }
        double range = max - min;
        double interval = round((float) range / numberOfBins, 2);
        double[] bins = new double[numberOfBins];
        for (int i = 0; i < bins.length; i++) {
            if (i == 0) {
                bins[i] = round(min + interval, 2);
            } else {
                bins[i] = round(bins[i - 1] + interval, 2);
            }
        }
        return bins;

    }

    //returns a double rounded to the specified number of decimals
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(places, RoundingMode.HALF_UP);
        return decimal.doubleValue();
    }

    //turns an array of string to an array of double if the string values are numeric
    //if values that are not numeric are found in the string array they are ignored
    public static double[] getNumericArray(String[] array) {
        double[] double_array = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            if (isNumeric(array[i])) {
                double_array[i] = round(Double.parseDouble(array[i]), 2);
            }
        }
        return double_array;
    }

}
