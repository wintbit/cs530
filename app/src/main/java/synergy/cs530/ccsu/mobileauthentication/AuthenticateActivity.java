package synergy.cs530.ccsu.mobileauthentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.models.Criterion;
import synergy.cs530.ccsu.mobileauthentication.dao.models.DataModel;
import synergy.cs530.ccsu.mobileauthentication.enums.NotificationEnum;
import synergy.cs530.ccsu.mobileauthentication.utils.Algorithm;


public class AuthenticateActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnTouchListener {

    private LinearLayout mLinearLayout;
    private int currentTapCount = 0;
    private DatabaseManager mDatabaseManager;
    private HashMap<Integer, ArrayList<DataModel>> userTemplateMap
            = new HashMap<>();
    private ArrayList dataModel;
    private ArrayList<Double> currentSequenceTouchDown= new ArrayList<>();
    private ArrayList<Double> currentSequenceTouchup= new ArrayList<>();
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenicate);
        Button mLoginButton = (Button) findViewById(R.id.activity_authenticate_login_button);
        mDatabaseManager = DatabaseManager.getInstance(getApplicationContext());
        mLoginButton.setOnClickListener(this);
        mLinearLayout = (LinearLayout) findViewById(R.id.activity_authenticate_linearLayout);
        mLinearLayout.setOnTouchListener(this);
        infoTextView=(TextView)findViewById(R.id.activity_authenticate_info_textView);

    }

    private void incrementTap() {
        currentTapCount++;
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    long touchDownTime = System.currentTimeMillis();
                    currentSequenceTouchDown.add((double) touchDownTime);
                    return true;
                case MotionEvent.ACTION_UP:
                    long touchUpTime = System.currentTimeMillis();
                    currentSequenceTouchup.add((double) touchUpTime);

                    incrementTap();
                    updateTapInfoView();
                    return true;


            }
        return false;
    }
    private void updateTapInfoView() {
        infoTextView.setText(Integer.toString(currentTapCount));
    }

    @Override
    public void onClick(View v) {

          Algorithm algorithm= new Algorithm(mDatabaseManager.getSequenceSetTouchDown());

        Double resultTouchDown=  algorithm.compute(currentSequenceTouchDown);

        algorithm= new Algorithm(mDatabaseManager.getSequenceSetTouchUp());

        Double resultTouchUp=algorithm.compute(currentSequenceTouchup);
        currentSequenceTouchDown.clear();
        currentSequenceTouchup.clear();
        Toast.makeText(getApplicationContext(),
                "Down:"+ resultTouchDown+", Up: "+
                resultTouchUp,
                Toast.LENGTH_SHORT).show();
    }
}
