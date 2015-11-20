package synergy.cs530.ccsu.mobileauthentication.utils;

import java.util.ArrayList;

/**
 * Created by ejwint on 11/17/15.
 */
public class Algorithm {


    private double[][] sequenceSet;
    private long[] templateAverages;
    private double[] deviations;
    private double[][] set;
    private int sequenceSize;


    public Algorithm(double[][] sequenceSet) {
        this.sequenceSet = sequenceSet;
        if (sequenceSet != null && sequenceSet.length > 0) {

            int sequenceSetSize = sequenceSet.length;
            sequenceSize = sequenceSet[0].length;

            set = new double[sequenceSize][sequenceSetSize];
            templateAverages = new long[sequenceSize];
            deviations = new double[sequenceSize];

            for (int i = 0; i < sequenceSize; i++) {
                double average = 0.0;
                for (int x = 0; x < sequenceSetSize; x++) {
                    double value = sequenceSet[x][i];

                    double last = sequenceSet[x][sequenceSize - 1];

                    value = (value / last);

                    average += value;
                    set[i][x] = value;
                }
                /* Compute the average " T[ T(i) ] " averages */
                average = (average / sequenceSetSize);

                /* Compute the standard deviation  of " T[ T(i) ] " */
                deviations[i] = standardDeviation(set[i], average);
            }
        }
    }

    public double compute(ArrayList<Double> userInput) {
        double result = 0.0;
        if (null != userInput && userInput.size() == sequenceSize) {
            int size = userInput.size();
            for (int i = 0; i < size; i++) {
                double X = userInput.get(i);
                double T = templateAverages[i];
                double deviation = deviations[i];
                double minus = (X - T);
                result += Math.abs((minus / deviation));
            }
        }
        return result;
    }


    public static double standardDeviation(double[] data, double average) {
        double result = 0;
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
