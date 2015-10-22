package synergy.cs530.ccsu.mobileauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import synergy.cs530.ccsu.mobileauthentication.enums.NotificationEnum;

public class TapCodeActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnTouchListener {

    private final String TAG = this.getClass().getName();

    private HashMap<Integer, ArrayList<TapModel>> mSequenceMap
            = new HashMap<>();
    private int SEQ_INDEX = 0;
    private ArrayList<TapModel> currentSequence;
    private LinearLayout mLinearLayout;
    private TextView mInfoTextView;
    /**
     * represents the current SEQ_INDEX within the entered sequence
     */
    private int currentTapCount = 0;

    private RadioButton mFirstRadioButton;
    private RadioButton mSecondRadioButton;
    private RadioButton mThirdRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_code);

        //Get view objects
        Button retryButton = (Button) findViewById(R.id.activity_tap_code_retry_button);
        retryButton.setOnClickListener(this);

        Button confirmButton = (Button) findViewById(R.id.activity_tap_code_confirm_button);
        confirmButton.setOnClickListener(this);

        //Get the view object that registers the touch events
        mLinearLayout = (LinearLayout) findViewById(R.id.activity_tap_code_linearLayout);
        //Set the touch event listener
        mLinearLayout.setOnTouchListener(this);
        //Get the TextView in which we will display the current tap count.
        mInfoTextView = (TextView) findViewById(R.id.activity_tap_code_info_textView);

        //GET the sequence radio buttons
        mFirstRadioButton = (RadioButton) findViewById(R.id.activity_tap_code_seq_1_RadioButton);
        mSecondRadioButton = (RadioButton) findViewById(R.id.activity_tap_code_seq_2_RadioButton);
        mThirdRadioButton = (RadioButton) findViewById(R.id.activity_tap_code_seq_3_RadioButton);

        //Create the sequence map.
        for (int i = 0; i < AppConstants.MAX_SEQUENCE_LIMIT; i++) {
            mSequenceMap.put(i, new ArrayList<TapModel>());
        }
        //Set the initial sequence container using the SEQ_INDEX
        currentSequence = mSequenceMap.get(SEQ_INDEX);

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
        //Check first if your use has touch/entered a touch event first

        switch (v.getId()) {
            case (R.id.activity_tap_code_retry_button):
                //Check if not empty first.
                if (!currentSequence.isEmpty()) {
                    resetSequencePatterns();
                    displayNotification(NotificationEnum.RESETTING_SEQUENCES);
                } else {
                    displayNotification(NotificationEnum.NO_ENTRY);
                }
                break;
            case (R.id.activity_tap_code_confirm_button):

                if (currentTapCount > 0) {
                    if (SEQ_INDEX == 0) {
                        updateSequenceRadioButtonView(SEQ_INDEX, currentTapCount);
                        SEQ_INDEX++;
                        currentSequence = mSequenceMap.get(SEQ_INDEX);
                        currentTapCount = 0;

                    } else if (SEQ_INDEX == 1) {
                        int previousTapCount = mSequenceMap.get(SEQ_INDEX - 1).size();
                        //If previous and current tap's match
                        if (previousTapCount == currentTapCount) {
                            updateSequenceRadioButtonView(SEQ_INDEX, currentTapCount);
                            SEQ_INDEX++;
                            currentSequence = mSequenceMap.get(SEQ_INDEX);
                            currentTapCount = 0;
                        } else {
                            //If previous and current tap's DON'T match
                            //RESET current sequence can try again.
                            resetCurrentSequence();
                        }
                    } else if (SEQ_INDEX == 2) {

                        int previousTapCount = mSequenceMap.get(SEQ_INDEX - 1).size();
                        //If previous and current tap's match
                        if (previousTapCount == currentTapCount) {
                            updateSequenceRadioButtonView(SEQ_INDEX, currentTapCount);
                            Toast.makeText(getApplicationContext(),
                                    "Tap-Sequence Recorded", Toast.LENGTH_SHORT).show();

                            if (BuildConfig.DEBUG) {
                                //Feature is only available for developers ONLY.
                                boolean exported = AppConstants.generateCSVFile(this, mSequenceMap);
                                if (exported) {
                                    displayNotification(NotificationEnum.EXPORT_SEQUENCE_SUCCESS);
                                    resetSequencePatterns();
                                    displayNotification(NotificationEnum.RESETTING_SEQUENCES);
                                } else {
                                    displayNotification(NotificationEnum.EXPORT_SEQUENCE_FAIL);
                                }
                            }
                        } else {
                            //If previous and current tap's DON'T match
                            //RESET current sequence can try again.
                            resetCurrentSequence();
                        }
                    }
                } else {
                    displayNotification(NotificationEnum.NO_ENTRY);
                }
                mInfoTextView.setText(Integer.toString(currentTapCount));
                break;
        }
    }

    private void resetCurrentSequence() {
        //If previous and current tap's DON'T match
        //RESET current sequence can try again.
        currentSequence.clear();
        currentTapCount = 0;
        displayNotification(NotificationEnum.MIS_MATCH);
        displayNotification(NotificationEnum.TRY_AGAIN);

    }


    private long touchUpTime;
    private long touchDowTime;
    private int xAxis;
    private int yAxis;
    private TapModel tapModel = new TapModel();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //check to see if the user in is the correct range first.
        if (currentTapCount < AppConstants.MAX_TAP_LIMIT
                && SEQ_INDEX <= AppConstants.MAX_SEQUENCE_LIMIT) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Get the initial tap info from the touch down
                    touchDowTime = System.currentTimeMillis();
                    xAxis = (int) event.getX();
                    yAxis = (int) event.getY();

                    return true;
                case MotionEvent.ACTION_UP:

                    touchUpTime = System.currentTimeMillis();
                    tapModel.setTimeDown(touchDowTime);
                    tapModel.setTimeUp(touchUpTime);
                    tapModel.setX(xAxis);
                    tapModel.setTimeUp(yAxis);
                    currentSequence.add(tapModel);

                    //Increment counter
                    incrementTap();
                    updateTapInfoView();

                    return true;
            }
        } else {
           displayNotification(NotificationEnum.LIMIT_REACHED);
        }
        return false;
    }

    private void resetTap() {
        currentTapCount = 0;
    }

    private void incrementTap() {
        currentTapCount++;
    }

    private void updateTapInfoView() {
        mInfoTextView.setText(Integer.toString(currentTapCount));
    }

    private void displayNotification(NotificationEnum notificationEnums) {
        Toast.makeText(getApplicationContext(),
                notificationEnums.getValue(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Resets the entire tap sequence setup and all related components
     */
    private void resetSequencePatterns() {
        resetSequenceMap();
        SEQ_INDEX = 0;
        currentTapCount = 0;
        currentSequence = mSequenceMap.get(SEQ_INDEX);
        mInfoTextView.setText(null);
        updateSequenceRadioButtonView(-1, -1);
    }

    /**
     * Clears a tap patterns current stored in the all sequences
     */
    private void resetSequenceMap() {
        for (int i = 0; i < AppConstants.MAX_SEQUENCE_LIMIT; i++) {
            ArrayList<TapModel> list = mSequenceMap.get(i);
            if (list != null && !list.isEmpty()) {
                list.clear();
            }
        }
    }

    /**
     * Sets the current number fo taps associated with sequence that has been configured
     *
     * @param position zero bac index, -1 = reset all to blank.
     * @param value    a valid integer value.
     */
    private void updateSequenceRadioButtonView(int position, int value) {
        boolean state = false;
        switch (position) {
            case -1:
                mFirstRadioButton.setText(null);
                mSecondRadioButton.setText(null);
                mThirdRadioButton.setText(null);
                mFirstRadioButton.setChecked(state);
                mSecondRadioButton.setChecked(state);
                mThirdRadioButton.setChecked(state);
                break;
            case 0:
                mFirstRadioButton.setChecked(!state);
                mFirstRadioButton.setText(Integer.toString(value));
                break;
            case 1:
                mSecondRadioButton.setChecked(!state);
                mSecondRadioButton.setText(Integer.toString(value));
                break;
            case 2:
                mThirdRadioButton.setChecked(!state);
                mThirdRadioButton.setText(Integer.toString(value));
                break;
        }
    }

}
