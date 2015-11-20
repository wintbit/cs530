package synergy.cs530.ccsu.mobileauthentication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.models.Criterion;
import synergy.cs530.ccsu.mobileauthentication.utils.FileTransfer;

public class DeveloperDatabaseActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_database);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_developer_database, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_export:
                ExportDatabaseTask workerTask = new ExportDatabaseTask();
                try {
                    workerTask.execute();
                } catch (IllegalStateException ex) {
                    Log.e(TAG, ex.getLocalizedMessage());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {


        public ExportDatabaseTask() {
        }

        // Decode image in background.
        @Override
        protected Boolean doInBackground(Void... params) {
            FileTransfer fileTransfer = new FileTransfer();
            File dbFile = getDatabasePath(DatabaseManager.DATABASE_NAME);
            File distFile = AppConstants.getExternalStorageDirectory(getApplicationContext());
            return fileTransfer.transferFile(dbFile, distFile);
        }


        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Boolean status) {
            Toast.makeText(getApplicationContext(), "Exported: " +
                    status, Toast.LENGTH_LONG).show();
        }
    }


}
