package synergy.cs530.ccsu.mobileauthentication.utils;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ejwint on 11/17/15.
 */
public class Algorithm {


    private double[] templateAverages;
    private double[] deviations;

    private int columnCount;


    /**
     * Create a instace of Algorithm with given parameters
     *
     * @param sequenceSet a double array that contains collections of test sequences
     */
    public Algorithm(double[][] sequenceSet) {
        /*
        *  sequenceSet [x][i]
        *  x = the current index OF a sequence
         * i = the current position WITH-IN a sequence
        * */

        if (sequenceSet != null && sequenceSet.length > 0) {

            int rowCount = sequenceSet.length;
            columnCount = sequenceSet[0].length;
            double[][] set = new double[columnCount][rowCount];
            templateAverages = new double[columnCount];
            deviations = new double[columnCount];

            for (int column = 0; column < columnCount; column++) {
                double average = 0.0;
                for (int row = 0; row < rowCount; row++) {
                    double value = sequenceSet[row][column];
                    //Get the last value from the
                    double last = sequenceSet[row][columnCount - 1];
                    /*Normalize the data*/
                    value = (value / last);
                    average += value;
                    set[column][row] = value;
                }
                /* Compute the average " T[ T(column) ] " averages */
                average = (average / rowCount);
                templateAverages[column] = average;
                /* Compute the standard deviation  of " T[ T(column) ] " */
                deviations[column] = standardDeviation(set[column], average);
            }
        }
    }

    public double compute(ArrayList<Double> userInput) {
        double result = -1.0;
        if (null != userInput && userInput.size() == columnCount) {
            int size = userInput.size();
            StringBuffer sb = new StringBuffer();
            double last = userInput.get(size - 1);
            for (int column = 0; column < size; column++) {
                double value = 0.0;
                double deviation = deviations[column];
                /*Can't divide by ZERO*/
                if (deviation > 0.0) {
                    /*Need to normalize the input from the user as well.
                     If everything has been normalized.*/
                    double xNormalized = (userInput.get(column) / last);
                    double T = templateAverages[column];
                    double minus = (xNormalized - T);
                    value = Math.abs((minus / deviation));
                }
                sb.append(String.format("[%s]: %s | ", column, value));
                result += value;
            }
            Log.d("Algorithm", sb.toString());
        }
        return result;

    }


    public static double standardDeviation(double[] data, double average) {
        double result = 0.0;
        int size = data.length;
        for (double item : data) {
            /*Summation  sqrt( (x-X)^2 ) */
            result += Math.pow((item - average), 2);
        }
        /*Divide by size of element in collection*/
        result = (result / size);
        /* Bessel correction */
        return Math.sqrt(result);
    }


    public static void main(String[] args) {
        double[] data = {2, 4, 4, 4, 5, 5, 7, 9};
        long average = 0;
        for (double i : data) {
            average += i;
        }
        average = average / data.length;

        double result = Algorithm.standardDeviation(data, average);
        System.out.println("Result " + result);


    }
}
