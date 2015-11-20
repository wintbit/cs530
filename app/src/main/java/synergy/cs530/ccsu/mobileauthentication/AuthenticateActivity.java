package synergy.cs530.ccsu.mobileauthentication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.utils.Algorithm;


public class AuthenticateActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnTouchListener {

    private int counter = 0;
    private DatabaseManager mDatabaseManager;
    private ArrayList<Double> sequenceTouchDown = new ArrayList<>();
    private ArrayList<Double> sequenceTouchup = new ArrayList<>();
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenicate);
        mDatabaseManager = DatabaseManager.getInstance(getApplicationContext());
        //Get login button
        Button mLoginButton = (Button) findViewById(R.id.activity_authenticate_login_button);
        //Add event listener for login button
        mLoginButton.setOnClickListener(this);
        //Get the tap surface area
        LinearLayout mLinearLayout = (LinearLayout) findViewById(
                R.id.activity_authenticate_linearLayout);
        //Add event listener for tap surface area
        mLinearLayout.setOnTouchListener(this);
        infoTextView = (TextView) findViewById(R.id.activity_authenticate_info_textView);
    }

    private void incrementTap() {
        counter++;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                long touchDownTime = System.currentTimeMillis();
                sequenceTouchDown.add((double) touchDownTime);
                return true;
            case MotionEvent.ACTION_UP:
                long touchUpTime = System.currentTimeMillis();
                sequenceTouchup.add((double) touchUpTime);
                incrementTap();
                updateTapInfoView();
                return true;
        }
        return false;
    }

    private void updateTapInfoView() {
        infoTextView.setText(Integer.toString(counter));
    }

    @Override
    public void onClick(View v) {
        /*Alogrithm used to compute the accuracy of user entered taps.*/
        Algorithm algorithm = new Algorithm(mDatabaseManager.getSequenceSetTouchDown());
        /*Comparing the user touch down*/
        Double resultTouchDown = algorithm.compute(sequenceTouchDown);
        algorithm = new Algorithm(mDatabaseManager.getSequenceSetTouchUp());
        /*Comparing the user touch up*/
        Double resultTouchUp = algorithm.compute(sequenceTouchup);
        //Reset counter
        counter = 0;
        infoTextView.setText(null);
        //Reset containers
        sequenceTouchDown.clear();
        sequenceTouchup.clear();
        //Display message to user
        Toast.makeText(getApplicationContext(),
                "Down: " + resultTouchDown + ",\n" +
                        " Up: " + resultTouchUp,
                Toast.LENGTH_SHORT).show();
    }
}
