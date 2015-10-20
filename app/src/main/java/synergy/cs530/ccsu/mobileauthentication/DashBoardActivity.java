package synergy.cs530.ccsu.mobileauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;


public class DashBoardActivity extends AppCompatActivity implements OnClickListener {

    private final String TAG = this.getClass().getName();
    private Button mConfigureButton;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        databaseManager = DatabaseManager.getInstance(getApplicationContext());

        mConfigureButton = (Button) findViewById(
                R.id.fragment_dash_board_toggleButton);
        mConfigureButton.setOnClickListener(this);

        if (databaseManager.getRowCount(TapSequenceTableEnum.KEY_ROW_ID) > 0) {
            mConfigureButton.setText(getResources().getText(R.string.action_reset));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_export_sequence:


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_dash_board_toggleButton) {
            Intent i = new Intent(this, TapCodeActivity.class);
            startActivity(i);
        }

    }
}
