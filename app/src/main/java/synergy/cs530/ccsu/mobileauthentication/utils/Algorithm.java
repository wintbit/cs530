package synergy.cs530.ccsu.mobileauthentication.utils;

import java.util.ArrayList;
import java.util.HashMap;

import synergy.cs530.ccsu.mobileauthentication.TapModel;

/**
 * Created by ejwint on 11/17/15.
 */
public class Algorithm {

    private HashMap<Integer, ArrayList<TapModel>> templateModels;

    private long[] tVectors;

    public Algorithm(HashMap<Integer, ArrayList<TapModel>> models) {
        this.templateModels = models;

        int size = models.get(0).size();

        tVectors = new long[models.get(0).size()];

        for (ArrayList<TapModel> values : models.values()) {

            int len = values.size();
            for (int i = 0; i < len; i++) {
                tVectors[i] += values.get(i).getTimeDown();
            }
        }
        for (int i = 0; i < size; i++) {
            tVectors[i] = (tVectors[i] / size);
        }

    }

    public double compute(ArrayList<TapModel> userInput) {
        double result = 0.0;
        int size = userInput.size();

        double deviation = getStandardDeviation(userInput);

        for (int i = 0; i < size; i++) {
            TapModel a = userInput.get(i);
            result += (a.getTimeDown() - tVectors[i]) / deviation;
        }

        return result;
    }


    public double getMean(ArrayList<TapModel> data) {
        double sum = 0.0;
        for (TapModel a : data) {
            sum += a.getTimeDown();
        }
        return sum / data.size();
    }

    public double getVariance(ArrayList<TapModel> data) {
        double mean = getMean(data);
        double temp = 0;
        for (TapModel a : data) {
            temp += (mean - a.getTimeDown()) * (mean - a.getTimeDown());
        }
        return temp / data.size();
    }

    public double getStandardDeviation(ArrayList<TapModel> data) {
        return Math.sqrt(getVariance(data));
    }


}
