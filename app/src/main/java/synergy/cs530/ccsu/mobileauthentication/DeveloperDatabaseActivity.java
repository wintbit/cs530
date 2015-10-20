package synergy.cs530.ccsu.mobileauthentication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.models.Criterion;
import synergy.cs530.ccsu.mobileauthentication.dao.models.DataModel;
import synergy.cs530.ccsu.mobileauthentication.utils.FileTransfer;

public class DeveloperDatabaseActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private final String TAG = this.getClass().getName();

    private DatabaseManager mDatabaseManager;

    private TableLayout mTableLayout;
    private TableRow headerTableRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_database);
        this.mDatabaseManager = DatabaseManager.getInstance(getApplicationContext());
        if (BuildConfig.DEBUG) {
            mTableLayout = (TableLayout) findViewById(
                    R.id.fragment_developer_database_tableLayout);
            headerTableRow = (TableRow) findViewById(
                    R.id.fragment_developer_database_tableLayout_header_tableRow);
            LoadDatabaseTableTask tableTask = new LoadDatabaseTableTask();
            try {
                tableTask.execute(TapSequenceTableEnum.KEY_ROW_ID.getTableName());
            } catch (IllegalStateException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
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
            case R.id.action_reset:
                int result = mDatabaseManager.deleteDataModel(
                        TapSequenceTableEnum.KEY_ROW_ID, (Criterion) null);
                if (result > 0) {
                    LoadDatabaseTableTask tableTask = new LoadDatabaseTableTask();
                    try {
                        tableTask.execute(TapSequenceTableEnum.KEY_ROW_ID.getTableName());
                    } catch (IllegalStateException ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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


    private class LoadDatabaseTableTask extends AsyncTask<String, Void, ArrayList<DataModel>> {


        @Override
        protected ArrayList<DataModel> doInBackground(String... params) {
            String query = String.format("SELECT * FROM %s", params[0]);
            return mDatabaseManager.getDataModels(query);
        }

        @Override
        protected void onPostExecute(ArrayList<DataModel> dataModels) {
            int count = mTableLayout.getChildCount();
            if (count > 0) {
                mTableLayout.removeAllViews();
            }
            if (null != dataModels && !dataModels.isEmpty()) {
                int rows = dataModels.size();
                int cols = dataModels.get(0).size();
                //Create header row
                headerTableRow.removeAllViews();
                DataModel headerDataModel = dataModels.get(0);
                for (String key : headerDataModel.getMap().keySet()) {
                    TextView columnTextView = new TextView(getApplicationContext());
                    columnTextView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    columnTextView.setGravity(Gravity.CENTER);
                    columnTextView.setTextSize(18);
                    columnTextView.setPadding(10, 5, 10, 5);
                    columnTextView.setText(key);

                    headerTableRow.addView(columnTextView);
                }

                mTableLayout.addView(headerTableRow);
                // outer for loop
                for (int i = 0; i < rows; i++) {
                    TableRow tableRow = new TableRow(getApplicationContext());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tableRow.setPadding(0, 0, 0, 1);
                    // inner for loop
                    for (int j = 0; j < cols; j++) {
                        TextView columnTextView = new TextView(getApplicationContext());
                        //Set text
                        columnTextView.setText(dataModels.get(i).getValueAtIndex(j));

                        columnTextView.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        columnTextView.setGravity(Gravity.CENTER);
                        columnTextView.setTextSize(18);
                        columnTextView.setPadding(10, 0, 10, 0);
//                        columnTextView.setBackground(
//                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ?
//                                        getResources().getDrawable(R.drawable.table_row_header_border,
//                                                getApplicationContext().getTheme()) :
//                                        getResources().getDrawable(R.drawable.table_row_header_border)
//                        );
                        //Add to table k
                        tableRow.addView(columnTextView);
                    }
                    mTableLayout.addView(tableRow);
                }
            }
            super.onPostExecute(dataModels);
        }
    }

}
