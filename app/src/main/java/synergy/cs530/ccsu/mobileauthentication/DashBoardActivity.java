package synergy.cs530.ccsu.mobileauthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ToggleButton;
import android.view.View;
import android.view.View.OnClickListener;


public class DashBoardActivity extends AppCompatActivity implements OnClickListener {
    private Button mConfigureToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        mConfigureToggleButton = (Button) findViewById(
                R.id.fragment_dash_board_toggleButton);
        mConfigureToggleButton.setOnClickListener(this);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
