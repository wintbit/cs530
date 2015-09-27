package synergy.cs530.ccsu.mobileauthentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.SequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.models.Criterion;
import synergy.cs530.ccsu.mobileauthentication.dao.models.DataModel;

public class TapCodeActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private final String TAG = this.getClass().getName();

    private DatabaseManager databaseManager;
    private HashMap<Integer, ArrayList<TapModel>> map = new HashMap<Integer, ArrayList<TapModel>>();
    private int position = 0;
    private ArrayList<TapModel> currentSequence;
    private LinearLayout linearLayout;
    private static final int MAX_SEQUENCE = 3;
    private static final int MAX_TAP_LIMIT = 15;
    private String[] dbSequence = new String[MAX_SEQUENCE];
    private int currentTap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_code);
        databaseManager = DatabaseManager.getInstance(getApplicationContext());
        dbSequence = new String[MAX_SEQUENCE];
        //Check if sequence already exist;
        int count = databaseManager.getRowCount(SequenceTableEnum.KEY_ROWID);
        if (count > 0) {
            dbSequence = databaseManager.getColumnValues(SequenceTableEnum.KEY_ROWID, null);
        }
        // clears the existing map if not empty and adds new items
        // Also adds new items to database if does not exist

        for (int i = 0; i < MAX_SEQUENCE; i++) {
            if (count == 0) {
                DataModel dataModel = new DataModel(SequenceTableEnum.KEY_ROWID);
                dataModel.put(SequenceTableEnum.KEY_CREATED, System.currentTimeMillis());
                //Sets the row if of the database insert
                int result = databaseManager.addDataModel(dataModel);
                dbSequence[i] = Integer.toString(result);
                Log.d(TAG, "Add #: " + result);
            }
            map.put(i, new ArrayList<TapModel>());
        }


        Button mRetry = (Button) findViewById(R.id.activity_tap_code_retry_button);
        Button mConfirm = (Button) findViewById(R.id.activity_tap_code_confirm_button);
        mConfirm.setOnClickListener(this);
        mRetry.setOnClickListener(this);

        currentSequence = map.get(position);
        linearLayout = (LinearLayout) findViewById(R.id.activity_tap_code_surfaceView);
        linearLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tap_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.activity_tap_code_retry_button):
                if (position < MAX_SEQUENCE) {
                    currentSequence.clear();
                    currentTap = 0;
                    int result = databaseManager.deleteDataModel(TapSequenceTableEnum.KEY_SEQUENCE_ID,
                            new Criterion(TapSequenceTableEnum.KEY_SEQUENCE_ID,
                                    dbSequence[position]));
                    Log.d(TAG, "Delete #: " + result);
                }
                break;
            case (R.id.activity_tap_code_confirm_button):
                if (position < MAX_SEQUENCE) {
                    position++;
                    currentSequence = map.get(position);
                } else if (position == MAX_SEQUENCE) {
                    //confirm finished all taps
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }
                currentTap = 0;
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (currentTap < MAX_TAP_LIMIT) {
            float x = event.getX();
            float y = event.getY();
            TapModel tapmodel = new TapModel((int) x,
                    (int) y, System.currentTimeMillis());
            currentSequence.add(tapmodel);
            String foreignKey = dbSequence[position];
            DataModel dataModel = new DataModel(TapSequenceTableEnum.KEY_ROWID);
            dataModel.put(TapSequenceTableEnum.KEY_SEQUENCE_ID, foreignKey);
            dataModel.put(TapSequenceTableEnum.KEY_X_AXIS, (int) x);
            dataModel.put(TapSequenceTableEnum.KEY_Y_AXIS, (int) y);
            dataModel.put(TapSequenceTableEnum.KEY_TIME, System.currentTimeMillis());
            int result = databaseManager.addDataModel(dataModel);
            Log.d(TAG, "Add #: " + result);
            currentTap++;
        } else {
            Toast.makeText(getApplicationContext(), "limit reached", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
