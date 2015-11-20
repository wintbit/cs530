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


public class DashBoardActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private final static int RESULT_SET_REQUEST = 33;

    private DatabaseManager databaseManager;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        databaseManager = DatabaseManager.getInstance(getApplicationContext());

        Button modifyButton = (Button) findViewById(
                R.id.fragment_dash_board_modify_Button);
        modifyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this,
                        TapCodeActivity.class);
                startActivityForResult(intent, RESULT_SET_REQUEST);
            }
        });

        testButton = (Button) findViewById(
                R.id.fragment_dash_board_test_Button);
        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AuthenticateActivity.class);
                startActivity(intent);
            }
        });

        int count = databaseManager.getRowCount(TapSequenceTableEnum.KEY_ROW_ID);
        testButton.setEnabled((count > 0));


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
//        int id = item.getItemId();
//        switch (id) {}
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SET_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                int count = databaseManager.getRowCount(TapSequenceTableEnum.KEY_ROW_ID);
                testButton.setEnabled((count > 0));
            }
        }
    }
}
