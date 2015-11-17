package synergy.cs530.ccsu.mobileauthentication.utils;

import java.util.ArrayList;

import synergy.cs530.ccsu.mobileauthentication.TapModel;

/**
 * Created by ejwint on 11/17/15.
 */
public class Algorithm {


    private ArrayList<TapModel> models;

    public Algorithm(ArrayList<TapModel> models) {
        this.models = models;
    }

    public long[] compute() {
        long[] result = new long[models.size()];
        long len = models.size();
        for (int i = 0; i < len; i++) {
            result[i] = models.get(i).getTimeDown() - average(i);
//            / divided by ?
        }
        return result;
    }

    public long average(int index) {
        long result = 0;
        for (int i = 0; i <= index; i++) {
            result += models.get(i).getTimeDown();
        }
        result = (result / (index + 1));
        return result;
    }

}
