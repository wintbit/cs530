package synergy.cs530.ccsu.mobileauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.models.Criterion;
import synergy.cs530.ccsu.mobileauthentication.dao.models.DataModel;

public class TapCodeActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private final String TAG = this.getClass().getName();

    private DatabaseManager databaseManager;
    private HashMap<Integer, ArrayList<TapModel>> map;
    private int position = 0;
    private ArrayList<TapModel> currentSequence;
    private LinearLayout linearLayout;
    private TextView infoTextView;
    private Button mStartStopButton;
    private TextView mTimerinfoView;
    //private TextView txtCount;
  /*  private Button btnCount;
    int count = 0;

    boolean[] timerProcessing = {false};

    boolean[] timerStarts = {false};

    //private MyCount timer;
    */
    private RadioGroup sequenceRadioGroup;
    /**
     * represents the current position within the entered sequence
     */
    private int currentTap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_code);
        map = new HashMap<>();
        databaseManager = DatabaseManager.getInstance(getApplicationContext());

        sequenceRadioGroup = (RadioGroup) findViewById(R.id.activity_tap_code_RadioGroup);
//        sequenceRadioGroup
        mStartStopButton = (Button) findViewById(R.id.activity_tap_code_stopwatch_togglcontent);
        mStartStopButton.setOnClickListener(this);
        // clears the existing map if not empty and adds new items
        // Also adds new items to database if does not exist
        for (int i = 0; i < AppConstants.MAX_SEQUENCE_LIMIT; i++) {
            map.put(i, new ArrayList<TapModel>());
        }

        Button mRetry = (Button) findViewById(R.id.activity_tap_code_retry_button);
        Button mConfirm = (Button) findViewById(R.id.activity_tap_code_confirm_button);
        mTimerinfoView = (TextView) findViewById(R.id.activity_tap_code_info_timer);

        mConfirm.setOnClickListener(this);


        mRetry.setOnClickListener(this);

        //Set the initial sequence container using the position
        currentSequence = map.get(position);
        //Get the view object that registers the touch events
        linearLayout = (LinearLayout) findViewById(R.id.activity_tap_code_linearLayout);
        //Set the touch event listener
        linearLayout.setOnTouchListener(this);

        infoTextView = (TextView) findViewById(R.id.activity_tap_code_info_textView);
        //for timer
        //timer = new MyCount(5000, 1);

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
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_developer:
                if (BuildConfig.DEBUG) {
                    Intent intent = new Intent(this,
                            DeveloperDatabaseActivity.class);
                    startActivity(intent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.activity_tap_code_retry_button):
                if (position < AppConstants.MAX_SEQUENCE_LIMIT) {
                    currentSequence.clear();
                    currentTap = 0;
                    int result = databaseManager.deleteDataModel(
                            TapSequenceTableEnum.KEY_SEQUENCE_ID,
                            new Criterion(TapSequenceTableEnum.KEY_SEQUENCE_ID,
                                    position));
                    Log.d(TAG, "Seq. Del. #: " + result);
                }

                infoTextView.setText(Integer.toString(currentTap));
                break;
            case (R.id.activity_tap_code_confirm_button):
                if (position < AppConstants.MAX_SEQUENCE_LIMIT) {


                    position++;
                    //currentTap=0;
                   /* if(position == 1) {
                        currentTap=0;
                    }*/
                    currentSequence = map.get(position);


//                    //getting the number of taps of previous sequence
                    if (position > 1) {

                        int previousCount = databaseManager.getRowCount(TapSequenceTableEnum.KEY_SEQUENCE_ID, position - 1);

                        int currentCount = currentTap;

                        if (previousCount != currentCount) {
                            //tap patterns did not matcg// message to renter the sequence correctly

                            Toast.makeText(getApplicationContext(),

                                    "Tap Did not match-startover", Toast.LENGTH_SHORT).show();
                            // delate the datamodel of the two sequence
                            position = 0;

                            for (int i = 0; i < AppConstants.MAX_SEQUENCE_LIMIT; i++) {
                                map.get(i).clear();
                                databaseManager.deleteDataModel(
                                        TapSequenceTableEnum.KEY_ROW_ID,
                                        new Criterion(TapSequenceTableEnum.KEY_SEQUENCE_ID, i));

                            }

                            infoTextView.setText(Integer.toString(currentTap));
                        } else {
                            currentTap = 0;
                        }
                    }
                    infoTextView.setText(Integer.toString(currentTap));
                    //TODO: Need to compare and evaluate that
                    // each sequence is similar, special case of 0
                    currentTap = 0;
                } else if (position == AppConstants.MAX_SEQUENCE_LIMIT) {
                    //confirm finished all taps
                    Toast.makeText(getApplicationContext(),
                            "Done", Toast.LENGTH_SHORT).show();
                    //TODO: Need to compare and evaluate that each sequence is similar
                    currentTap = 0;
                }
                infoTextView.setText(Integer.toString(currentTap));
                break;

            case (R.id.activity_tap_code_stopwatch_togglcontent):
//start the countdown timer for 10 seconds
                //After 10 seconds there should be amessage telling the user to stop tapping
                // and save the record in the array
                //if the user presses the stop button before the time is up jus add the entered patter
              /*  mStartStopButton.setText("stop");
                if (!timerStarts[0]) {
                    timer.start();
                    timerStarts[0] = true;
                    timerProcessing[0] = true;
                }

                if (timerProcessing[0]) {
                    count++;
                    mTimerinfoView.setText(String.valueOf(count));
                }


                break;
      */  }

    }

    int tapRowId = -1;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        event.getSize();
        long time = System.currentTimeMillis();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currentTap < AppConstants.MAX_TAP_LIMIT
                        && position < AppConstants.MAX_SEQUENCE_LIMIT) {


                    TapModel tapmodel = new TapModel(x,
                            y, time);
                    currentSequence.add(tapmodel);

                    DataModel dataModel = new DataModel(TapSequenceTableEnum.KEY_ROW_ID);
                    dataModel.put(TapSequenceTableEnum.KEY_SEQUENCE_ID, position);
                    dataModel.put(TapSequenceTableEnum.KEY_X_AXIS, x);
                    dataModel.put(TapSequenceTableEnum.KEY_Y_AXIS, y);
                    dataModel.put(TapSequenceTableEnum.KEY_TOUCHDOWN, time);

                    tapRowId = databaseManager.addDataModel(dataModel);

                    Log.d(TAG, "Seq. Add TouchDown #: " + tapRowId);


                    infoTextView.setText(Integer.toString(currentTap));
                } else {
                    Toast.makeText(getApplicationContext(), "limit reached", Toast.LENGTH_SHORT).show();
                }
                return true;

            case MotionEvent.ACTION_UP:
                currentSequence.get(currentTap).setTimeUp(time);
               int result = databaseManager.updateDataModel(TapSequenceTableEnum.KEY_ROW_ID
                        , new Criterion(TapSequenceTableEnum.Key_TOUCHUP, time), new Criterion(TapSequenceTableEnum.KEY_ROW_ID, tapRowId));

                tapRowId = -1;

                Log.d(TAG, "Seq. Add TouchUp #: " + result);

                currentTap++;

                break;
        }
        return false;
    }

 /*  public class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onFinish() {
            mTimerinfoView.setText("Time is up");
            timerStarts[0] = false;
            mStartStopButton.setText("Start");

        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTimerinfoView.setText("" + millisUntilFinished / 1000 + ":"
                    + millisUntilFinished % 1000);

        }

    }
    */
    private void setCheckedPosition(int position) {

    }
}



