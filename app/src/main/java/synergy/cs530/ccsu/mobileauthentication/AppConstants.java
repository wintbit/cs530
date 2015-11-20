package synergy.cs530.ccsu.mobileauthentication;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ejwint on 9/28/15.
 * Provides a application level utilities
 */
public class AppConstants {

    private static String TAG = "AppConstants";


    public static final int MAX_SEQUENCE_LIMIT = 3;
    public static final int MAX_TAP_LIMIT = 15;

    private static final String CSV_HEADER[] = {

            "seq_idx", "x-axis", "y-axis", "time-down", "time-up"

    };

    public static File getExternalStorageDirectory(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                context.getResources().getString(R.string.app_name));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    public static boolean generateCSVFile(Context context,
                                          ArrayList<TapModel>[] sequences) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        boolean result = false;

        if (BuildConfig.DEBUG) {
            //Feature is only available for developers ONLY.
            try {
                File mediaStorageDir = getExternalStorageDirectory(context);
                // Create a media file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File mediaFile;
                mediaFile = new File(mediaStorageDir.getPath()
                        + File.separator + timeStamp + ".csv");
                try {
                    FileWriter writer = new FileWriter(mediaFile);

                    //write header
                    int len = CSV_HEADER.length;
                    for (int i = 0; i < len; i++) {
                        writer.append(CSV_HEADER[i]);
                        if (i < (len - 1)) {
                            writer.append(", ");
                        } else {
                            writer.append("\r\n");
                        }
                    }


                    int size = sequences.length;
                    for (int i = 0; i < size; i++) {
                        ArrayList<TapModel> values = sequences[i];
                        for (TapModel tapModel : values) {
                            writer.append(Integer.toString(i));
                            writer.append(", ");
                            writer.append(tapModel.toString());
                            writer.append("\r\n");
                        }
                    }


                    //generate whatever data you want
                    writer.flush();
                    writer.close();
                    result = true;
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }

            } catch (NullPointerException ex) {
                Log.e(TAG, ex.getMessage());
            } catch (SecurityException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
        //
        return result;
    }
}
