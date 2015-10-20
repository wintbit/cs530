package synergy.cs530.ccsu.mobileauthentication;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ejwint on 9/28/15.
 */
public class AppConstants {

    private final String TAG = this.getClass().getName();


    public static final int MAX_SEQUENCE_LIMIT = 3;
    public static final int MAX_TAP_LIMIT = 15;


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
                                          HashMap<Integer, ArrayList<TapModel>> sequences) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        boolean result = false;
        File mediaStorageDir = getExternalStorageDirectory(context);

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath()
                + File.separator + timeStamp + ".csv");
        try {
            FileWriter writer = new FileWriter(mediaFile);

            for (Map.Entry<Integer, ArrayList<TapModel>> entry : sequences.entrySet()) {
                Integer key = entry.getKey();
                ArrayList<TapModel> value = entry.getValue();
                for (TapModel tapModel : value) {
                    writer.append(key.toString());
                    writer.append(",");
                    writer.append(tapModel.toString());
                    writer.append("\r\n");
                }
            }
            //generate whatever data you want
            writer.flush();
            writer.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        //
        return result;
    }
}
