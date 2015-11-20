/*
 * Copyright (c) 2015. Ernel J. Wint
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package synergy.cs530.ccsu.mobileauthentication;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import synergy.cs530.ccsu.mobileauthentication.dao.DatabaseManager;
import synergy.cs530.ccsu.mobileauthentication.dao.models.DataModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeveloperDatabaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeveloperDatabaseFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {

    private static final String DEVELOPER_TABLE_NAME_SQL = "SELECT name FROM sqlite_master" +
            " WHERE type='table' and  name NOT IN ( 'android_metadata', " +
            " 'sqlite_sequence' ) ORDER BY name ASC ";
    private final String TAG = this.getClass().getName();
    private DatabaseManager mDatabaseManager;
    private Spinner mSpinner;
    private TableLayout mTableLayout;
    private String[] mTableNames;
    private TableRow headerTableRow;

    public DeveloperDatabaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeveloperDatabaseFragment.
     */
    public static DeveloperDatabaseFragment newInstance() {
        DeveloperDatabaseFragment fragment = new DeveloperDatabaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_developer_database, container, false);
        this.mDatabaseManager = DatabaseManager.getInstance(getActivity());
        if (BuildConfig.DEBUG) {

            mTableLayout = (TableLayout) view.findViewById(
                    R.id.fragment_developer_database_tableLayout);
            headerTableRow = (TableRow) view.findViewById(
                    R.id.fragment_developer_database_tableLayout_header_tableRow);
            mSpinner = (Spinner) view.findViewById(R.id.fragment_developer_database_spinner);

            WorkerTask workerTask = new WorkerTask(getActivity().getApplicationContext());
            try {
                workerTask.execute();
            } catch (IllegalStateException ex) {
                Log.e(TAG, ex.getMessage());
            }
            //Event Listener
            mSpinner.setOnItemSelectedListener(this);
            //ONLY FOR DEVELOPER's

        }
        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Display useful information for developer related to database.
        LoadDatabaseTableTask task = new LoadDatabaseTableTask();
        task.execute(mTableNames[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class WorkerTask extends AsyncTask<Void, Void, String[]> {

        private final Context context;

        public WorkerTask(Context context) {
            this.context = context;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            return (null != mDatabaseManager) ? mDatabaseManager.getColumnValues
                    (DEVELOPER_TABLE_NAME_SQL, null) : null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            mTableNames = strings;
            //Create adapter and set seconds.
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                    R.layout.spinner_item, strings);
            // Apply the adapter to the mSpinner
            mSpinner.setAdapter(arrayAdapter);

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
            mTableLayout.removeAllViews();
            if (null != dataModels && !dataModels.isEmpty()) {
                int rows = dataModels.size();
                int cols = dataModels.get(0).size();
                //Create header row
                headerTableRow.removeAllViews();
                DataModel headerDataModel = dataModels.get(0);
                for (String key : headerDataModel.getMap().keySet()) {
                    TextView columnTextView = new TextView(getActivity());
                    columnTextView.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    columnTextView.setGravity(Gravity.CENTER);
                    columnTextView.setTextSize(18);
                    columnTextView.setPadding(10, 5, 10, 5);
                    columnTextView.setText(key);

                    columnTextView.setBackground(getResources().getDrawable(
                            R.drawable.table_row_header_border,
                            getActivity().getApplicationContext().getTheme()));
                    headerTableRow.addView(columnTextView);
                }

                mTableLayout.addView(headerTableRow);
                // outer for loop
                for (int i = 0; i < rows; i++) {
                    TableRow tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tableRow.setPadding(0, 0, 0, 1);
                    // inner for loop
                    for (int j = 0; j < cols; j++) {
                        TextView columnTextView = new TextView(getActivity());
                        //Set text
                        columnTextView.setText(dataModels.get(i).getValueAtIndex(j));

                        columnTextView.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        columnTextView.setGravity(Gravity.CENTER);
                        columnTextView.setTextSize(18);
                        columnTextView.setPadding(10, 0, 10, 0);
                        columnTextView.setBackground(
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ?
                                        getResources().getDrawable(R.drawable.table_row_header_border,
                                                getActivity().getTheme()) :
                                        getResources().getDrawable(R.drawable.table_row_header_border)
                        );
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
