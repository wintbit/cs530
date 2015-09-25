package synergy.cs530.ccsu.mobileauthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class TapCodeActivity extends AppCompatActivity implements View.OnClickListener {


    private HashMap<Integer, ArrayList<TapModel>> map = new HashMap<Integer,ArrayList<TapModel>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_code);
      Button  mRetry=(Button)findViewById(R.id.activity_Tap_Code_Retry_button);
        Button   mConfirm=(Button)findViewById(R.id.activity_Tap_Code_Confirm_button);
        mConfirm.setOnClickListener(this);
        mRetry.setOnClickListener(this);

        SurfaceView mSurfaceView=(SurfaceView)findViewById(R.id.activity_Tap_code_surfaceView);
        mSurfaceView.setOnClickListener(this);
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
        switch(v.getId()){
            case (R.id.activity_Tap_Code_Retry_button):
                break;
            case(R.id.activity_Tap_Code_Confirm_button):
                break;
            case (R.id.activity_Tap_code_surfaceView):



                break;
        }
    }
}
