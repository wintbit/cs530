/*
 * Copyright (c) 2015.  Ernel J. Wint
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package synergy.cs530.ccsu.mobileauthentication.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import synergy.cs530.ccsu.mobileauthentication.dao.enums.TapSequenceTableEnum;
import synergy.cs530.ccsu.mobileauthentication.dao.interfaces.TableFieldInterface;
import synergy.cs530.ccsu.mobileauthentication.dao.models.Criterion;
import synergy.cs530.ccsu.mobileauthentication.dao.models.DataModel;
import synergy.cs530.ccsu.mobileauthentication.dao.models.OrderByEnum;


/**
 * DatabaseManager class, responsible for managing the database connection and
 * creating, request, updating and deleting data.
 */
public class DatabaseManager {

    public static final String DATABASE_NAME = "mobile_authentication.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseManager";
    private static final String CREATE_TABLE_KEY = " CREATE TABLE IF NOT EXISTS %s ";
    private static final String DROP_TABLE_KEY = " DROP TABLE IF EXISTS %s; ";

    private static DatabaseManager singletonInstance;
    private final Context mContext;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqliteDatabase;

    private HashMap<String, TableFieldInterface[]> map = new HashMap<>();

    private DatabaseManager(Context context) {
        mContext = context;
        setDefaultValue();
        open();
    }

    /**
     * Create a Singleton instance of the DatabaseManager
     *
     * @param context a valid Context Object, to use to open or create the database
     * @return a already existing DatabaseManager singleton or  a newly created singleton.
     */
    public static DatabaseManager getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (singletonInstance == null) {
            singletonInstance = new DatabaseManager(
                    context.getApplicationContext());
        }
        return singletonInstance;
    }


    /**
     * Initializes the DatabaseManager with entails opening a database connection
     *
     * @param context a valid Context Object, to use to open or create the database
     */
    public static synchronized void initialize(Context context) {
        if (singletonInstance == null) {
            singletonInstance = new DatabaseManager(context);
        }
    }

    /**
     * Returns the DatabaseManager singleton
     *
     * @return a DatabaseManager if it was iniitialized
     * @throws IllegalStateException, if the singleton was not initialized first
     */
    public static synchronized DatabaseManager getInstance() throws IllegalStateException {
        if (singletonInstance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call getInstance(..) or initialize(..) method first.");
        }
        return singletonInstance;
    }


    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    private synchronized DatabaseManager open() throws SQLException,
            NullPointerException {
        databaseHelper = new DatabaseHelper(mContext);
        sqliteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }


    private void setDefaultValue() {

        map.put(TapSequenceTableEnum.KEY_ROW_ID
                .getTableName(), TapSequenceTableEnum.values());
    }

    /**
     * Closes the database.
     */
    public synchronized void close() throws NullPointerException {

        databaseHelper.close();
    }


    private synchronized String createTableSQLite(String tableName,
                                                  TableFieldInterface[] tableFields) {
        String result = "";
        if (null != tableName && tableName.length() > 0 && null != tableFields
                && tableFields.length > 0) {
            StringBuilder sb = new StringBuilder(createTableKey(tableName));
            sb.append(" ( ");
            int size = tableFields.length;
            for (int idx = 0; idx < size; idx++) {
                TableFieldInterface field = tableFields[idx];
                sb.append(generateField(1, field));
                if (idx < (size - 1)) {
                    sb.append(", ");
                }
            }
            result = sb.append(" );").toString();
        }
        return result;
    }

    private synchronized String generateField(int type, TableFieldInterface field) {
        String result = "";
        if (type == 0) {
            result = String.format("%s %s", field.getColumnName(),
                    field.getMySQLFieldType());
        } else if (type == 1) {
            result = String.format("%s %s", field.getColumnName(),
                    field.getSQLiteFieldType());
        }
        return result;
    }

    private synchronized String dropTableSQL(String tableName) {
        String result = "";
        if (null != tableName && tableName.length() > 0) {
            result = dropTableKey(tableName);
        }
        return result;
    }

    private synchronized String createTableKey(String name) {
        return (null != name && name.length() > 0) ? String.format(
                CREATE_TABLE_KEY, name) : " ";
    }

    private synchronized String dropTableKey(String name) {
        return (null != name && name.length() > 0) ? String.format(
                DROP_TABLE_KEY, name) : "";
    }



	/*
     * #########################################################################
	 */


    /**
     * Returns the number of rows of data associated with a TableFieldInterface
     *
     * @param tableField a valid TableFieldInterface, use the name of the table
     * @return a valid number of rows within a table or -1 if no rows are found.
     */
    public synchronized int getRowCount(TableFieldInterface tableField) {
        return getRowCount(tableField, null);
    }

    /**
     * Returns the number of rows of data associated with a TableFieldInterface
     *
     * @param tableField a valid TableFieldInterface, use the name of the table and field name
     * @param input      a associated value with the field name
     * @return a valid number of rows within a table or 0 if no rows are found.
     */
    public synchronized int getRowCount(TableFieldInterface tableField, int input) {
        int rowCount = -1;
        try {
            if (null != tableField && tableField.getTableName().length() > 0
                    && null != sqliteDatabase) {
                Cursor cursor = sqliteDatabase.rawQuery("SELECT  count(*) FROM "
                        + tableField.getTableName() + " WHERE " +
                        tableField.getColumnName() + " = " + input, null);
                cursor.moveToFirst();
                rowCount = cursor.getInt(0);
                cursor.close();
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        // return row count
        return rowCount;
    }

    /**
     * Returns the number of rows of data associated with a TableFieldInterface
     *
     * @param tableField a valid TableFieldInterface, use the name of the table and field name
     * @param input      a associated value with the field name
     * @return a valid number of rows within a table or 0 if no rows are found.
     */
    public synchronized int getRowCount(TableFieldInterface tableField, String input) {
        int rowCount = -1;
        try {
            if (null != tableField && tableField.getTableName().length() > 0
                    && null != sqliteDatabase) {
                StringBuilder sb = new StringBuilder("SELECT  count(*) FROM "
                        + tableField.getTableName());
                if (null != input && input.length() > 0) {
                    sb.append(" WHERE ").append(tableField.getColumnName()).append(" = ")
                            .append("'").append(input).append("'");
                }
                Cursor cursor = sqliteDatabase.rawQuery(sb.toString(), null);
                cursor.moveToFirst();
                rowCount = cursor.getInt(0);
                cursor.close();
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        // return row count
        return rowCount;
    }

    /**
     * Returns the number of rows of data associated with a TableFieldInterface
     *
     * @param tableField a valid TableFieldInterface, use the name of the table and field name
     * @param criterions a collection of where criterias in with a query can be decided by.
     * @return a valid number of rows within a table or 0 if no rows are found.
     */
    public synchronized int getCount(TableFieldInterface tableField,
                                     ArrayList<Criterion> criterions) {
        int rowCount = -1;
        try {
            if (null != tableField && tableField.getTableName().length() > 0
                    && null != sqliteDatabase) {
                StringBuilder sb = new StringBuilder("SELECT  count(*) FROM "
                        + tableField.getTableName());
                if (!criterions.isEmpty()) {
                    sb.append(" WHERE ");
                    int size = criterions.size();
                    for (int i = 0; i < size; i++) {
                        sb.append(criterions.get(i).getClause());
                        if (i < (size - 1)) {
                            sb.append(" AND ");
                        }
                    }
                }
                Log.d(TAG, sb.toString());
                Cursor cursor = sqliteDatabase.rawQuery(sb.toString(), null);
                cursor.moveToFirst();
                rowCount = cursor.getInt(0);
                cursor.close();
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        // return row count
        return rowCount;
    }


    /**
     * Inserts a valid DataModel attributes as a new row the database.
     *
     * @param dataModel has attributes and values a of table and row
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public synchronized int addDataModel(DataModel dataModel) {
        int id = -1;
        try {
            if (null != dataModel && dataModel.getTableName() != null) {
                TableFieldInterface[] fields = null;
                String tableName = dataModel.getTableName();
                /**
                 * Doing this so you can the array of fields from the specific table.
                 */
                for (Map.Entry<String, TableFieldInterface[]> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (tableName.equalsIgnoreCase(key)) {
                        fields = entry.getValue();
                        break;
                    }
                }

                if (null != fields && fields.length > 0) {
                    ContentValues values = new ContentValues();
                    for (TableFieldInterface field : fields) {
                        String val = dataModel.get(field);
                        values.put(field.getColumnName(), val);
                    }
                    // Inserting Row
                    id = (int) sqliteDatabase.insert(tableName, null, values);
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return id;
    }


    /**
     * Returns a DataModel given a table, column name and associated value
     *
     * @param tableField used to identify the name of the table
     * @param criterion  a standards by with a query is filtered
     * @return a valid DataModel if row exist else Null.
     */
    public DataModel getDataModel(TableFieldInterface tableField, Criterion criterion) {
        DataModel model = null;
        try {
            if (null != tableField && criterion != null && null != sqliteDatabase) {

                String table = tableField.getTableName();
                String field = tableField.getColumnName();
                Cursor cursor = sqliteDatabase.query(true, table,
                        null, criterion.getClause(), null,
                        null, null, null, null);
                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    model = new DataModel(table);
                    do {
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        break;
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return model;
    }

    /**
     * Returns a DataModel given a table, column name and associated value
     *
     * @param tableField used to identify the name of the table
     * @param criterions a collection of standards by with a query is filtered
     * @return a valid DataModel if row exist else Null.
     */
    public synchronized DataModel getDataModel(TableFieldInterface tableField,
                                               Criterion[] criterions) {
        DataModel model = null;
        try {
            if (null != tableField && criterions != null && criterions.length > 0
                    && null != sqliteDatabase) {

                String table = tableField.getTableName();

                int len = criterions.length;
                StringBuilder selection = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    Criterion criterion = criterions[i];
                    String clause = criterion.getClause();
                    if (clause.length() > 0) {
                        selection.append(clause);
                        if (i < (len - 1)) {
                            selection.append(" AND ");
                        }
                    }
                }

                Cursor cursor = sqliteDatabase.query(table, null, selection.toString(),
                        null, null, null, null);
                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    model = new DataModel(table);
                    do {
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        break;
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return model;
    }


    /**
     * Returns a DataModel given a table, column name and associated value
     *
     * @param tableField used to identify the name of the table
     * @param criterions a collection of standards by with a query is filtered
     * @return a valid DataModel if row exist else Null.
     */
    public synchronized DataModel getDataModel(TableFieldInterface tableField,
                                               ArrayList<Criterion> criterions) {
        return getDataModel(tableField, criterions.toArray(new Criterion[criterions.size()]));
    }


    /**
     * Returns a DataModel given a table, column name and associated value
     *
     * @param tableField used to identify the name of the table and the specific column name to query on.
     * @param index      a specific database row index
     * @return a valid DataModel Object or null
     */
    public DataModel getNthDataModel(TableFieldInterface tableField, int index) {
        DataModel result = null;
        try {
            if (null != tableField
                    && null != sqliteDatabase) {
                String table = tableField.getTableName();
                String sql = String.format("SELECT * FROM %s LIMIT 1 OFFSET %s ", table, index);
                Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    result = new DataModel(table);
                    do {
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            result.put(colName, colVal);
                        }
                        break;
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }


    /**
     * Returns a column of values from a specific table column given a collection
     * of standards by with a query is filtered
     *
     * @param tableField used to identify the name of the table name and column
     * @param criterions a collection of standards by with a query is filtered
     * @return Returns a column of values from a specific table column
     */
    public synchronized String[] getColumnValues(TableFieldInterface tableField,
                                                 ArrayList<Criterion> criterions) {
        String[] result = null;
        try {
            if (null != tableField && null != criterions && !criterions.isEmpty()) {
                StringBuilder selectClause = new StringBuilder();
                int idx = 0;
                int size = criterions.size();
                for (Criterion entry : criterions) {

                    String clause = entry.getClause();
                    if (clause.length() > 0) {
                        selectClause.append(clause);
                        if (idx < (size - 1)) {
                            selectClause.append(" AND ");
                        }
                    }
                    idx++;
                }

                Cursor cursor = sqliteDatabase.query(tableField.getTableName(),
                        new String[]{tableField.getColumnName()},
                        selectClause.toString(), null, null, null, null);
                if (cursor.moveToFirst()) {
                    result = new String[cursor.getCount()];
                    int i = 0;
                    do {
                        try {
                            result[i] = cursor.getString(0);
                        } catch (NullPointerException ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                        i++;
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }

    public synchronized String[] getColumnValues(String query, String[] selectionArgs) {
        String[] result = null;
        try {
            if (null != query && query.length() > 0) {
                Cursor cursor = sqliteDatabase.rawQuery(query, selectionArgs);
                if (cursor.moveToFirst()) {
                    result = new String[cursor.getCount()];
                    int i = 0;
                    while (!cursor.isAfterLast()) {
                        try {
                            result[i] = cursor.getString(0);
                        } catch (NullPointerException ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                        i++;
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }


    /**
     * Returns the cell value from a specific table row
     *
     * @param tableField used to identify the name of the table name
     * @param criterions a collection of standards by with a query is filtered
     * @return a the value associated with the table row cell else NULL
     */
    public synchronized String getCellValue(TableFieldInterface tableField,
                                            ArrayList<Criterion> criterions) {
        String result = null;
        try {
            if (null != tableField) {
                StringBuilder selection = new StringBuilder();
                int size = criterions.size();

                for (int i = 0; i < size; i++) {
                    String clause = criterions.get(i).getClause();
                    if (clause.length() > 0) {
                        selection.append(clause);
                        if (i < (size - 1)) {
                            selection.append(" AND ");
                        }
                    }
                }
                Cursor cursor = sqliteDatabase.query(tableField.getTableName(),
                        new String[]{tableField.getColumnName()},
                        selection.toString(), null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            result = cursor.getString(0);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }


    /**
     * Returns a collection DataModel object given the table and search  parameters to query
     *
     * @param tableField used to identify the name of the table name
     * @param values     a collection of where criterias in with a query can be decided by.
     * @return a Collection of DataModel objects else a empty list.
     */
    public ArrayList<DataModel> getDataModels(TableFieldInterface tableField,
                                              Criterion[] values) {
        ArrayList<DataModel> result = new ArrayList<>();
        try {
            if (null != tableField && null != values && values.length > 0) {
                StringBuilder whereClause = new StringBuilder();

                int wIdx = 0;
                int len = values.length;
                for (Criterion entry : values) {
                    whereClause.append(entry.getClause());
                    if (wIdx < (len - 1)) {
                        whereClause.append(" AND ");
                    }
                    wIdx++;
                }

                Cursor cursor = sqliteDatabase.query(true,
                        tableField.getTableName(), /**
                 * < Table
                 * name.
                 */
                        null, /**
                 * < All the fields that you want the cursor to
                 * contain; null means all.
                 */
                        whereClause.toString(), /**
                 * < WHERE statement without the
                 * WHERE clause.
                 */
                        null, /** < Selection arguments. */
                        null, null, null, null);

                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    do {
                        DataModel model = new DataModel(tableField.getTableName());
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        result.add(model);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return result;
    }

    /**
     * Returns a collection DataModel object given the table and search  parameters to query
     *
     * @param tableField used to identify the name of the table name
     * @param values     a collection of where criterias in with a query can be decided by.
     * @return a Collection of DataModel objects else a empty list.
     */
    public ArrayList<DataModel> getDataModels(TableFieldInterface tableField,
                                              ArrayList<Criterion> values) {
        return getDataModels(tableField, values.toArray(new Criterion[values.size()]));
    }

    /**
     * Returns a collection DataModel object given the table and search  parameters to query
     *
     * @param query the SQL query. The SQL string must not be ; terminated
     * @return a Collection of DataModel objects else a empty list.
     */
    public synchronized ArrayList<DataModel> getDataModels(String query) {
        ArrayList<DataModel> list = new ArrayList<>();
        try {
            if (null != query) {
                Cursor cursor = sqliteDatabase.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    do {
                        DataModel model = new DataModel();
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        list.add(model);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return list;
    }

    /**
     * Returns a collection DataModel object given the table and search  parameters to query
     *
     * @param tableField used to identify the name of the table name
     * @return a Collection of DataModel objects else a empty list.
     */
    public synchronized ArrayList<DataModel> getDataModels(TableFieldInterface tableField) {
        ArrayList<DataModel> list = new ArrayList<>();
        try {
            if (null != tableField) {
                Cursor cursor = sqliteDatabase.query(true,
                        tableField.getTableName(), /**
                 * < Table
                 * name.
                 */
                        null, /**
                 * < All the fields that you want the cursor to
                 * contain; null means all.
                 */
                        null, /**
                 * < WHERE statement without the WHERE clause.
                 */
                        null, /** < Selection arguments. */
                        null, null, null, null);

                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    do {
                        DataModel model = new DataModel(tableField.getTableName());
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        list.add(model);
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return list;
    }

    /**
     * Returns a collection DataModel object given the table and search  parameters to query
     *
     * @param tableField used to identify the name of the table, and specific column name
     * @param value      the associated value of the column name.
     * @return a Collection of DataModel objects else a empty list.
     */
    @Deprecated
    public synchronized ArrayList<DataModel> getDataModels(
            TableFieldInterface tableField, String value) {
        ArrayList<DataModel> list = new ArrayList<>();
        try {
            if (null != tableField && null != value && value.length() > 0) {
                Cursor cursor = sqliteDatabase.query(true,
                        tableField.getTableName(),
                        null,
                        tableField.getColumnName() + " = ? ",
                        new String[]{value},
                        null, null, null, null);

                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    do {
                        DataModel model = new DataModel(tableField.getTableName());
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        list.add(model);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return list;
    }

    /**
     * Returns a collection DataModel object given the table and search  parameters to query
     *
     * @param tableField    used to identify the name of the table, and specific column name
     * @param criterion     a collection of standards by with a query is filtered
     * @param orderByColumn used to identify the column name to order by
     * @param orderByEnum   the ordering of progresssing sizes
     * @return a Collection of DataModel objects else a empty list.
     */
    public ArrayList<DataModel> getDataModels(TableFieldInterface tableField, Criterion criterion,
                                              TableFieldInterface orderByColumn,
                                              OrderByEnum orderByEnum) {
        ArrayList<DataModel> result = new ArrayList<>();
        try {
            if (null != tableField && null != criterion && null != orderByColumn) {
                Cursor cursor = sqliteDatabase.query(true,
                        tableField.getTableName(),
                        null,
                        criterion.getClause(),
                        null,
                        null, null, orderByColumn.getColumnName()
                                + " " + orderByEnum.getValue(), null);

                if (cursor.moveToFirst()) {
                    int colCount = cursor.getColumnCount();
                    do {
                        DataModel model = new DataModel(tableField.getTableName());
                        for (int idx = 0; idx < colCount; idx++) {
                            String colName = cursor.getColumnName(idx);
                            String colVal = cursor.getString(idx);
                            model.put(colName, colVal);
                        }
                        result.add(model);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }


    /**
     * Deletes the associated row of the DataModel object in the database.
     *
     * @param tableField      used to identify the name of the table and column name
     * @param whereConditions a collection of standards by with a query is filtered
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise.
     */
    public synchronized int deleteDataModel(TableFieldInterface tableField,
                                            Criterion[] whereConditions) {
        int result = -1;
        if (tableField != null) {
            try {
                String whereClause = null;
                if (whereConditions != null && whereConditions.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    int len = whereConditions.length;
                    for (int i = 0; i < len; i++) {
                        Criterion c = whereConditions[i];
                        sb.append(c.getClause());
                        if (i < (len - 1)) {
                            sb.append(" AND ");
                        }
                    }
                    whereClause = sb.toString();
                }
                result = sqliteDatabase.delete(tableField.getTableName(),
                        whereClause, null);
            } catch (SQLException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
        return result;
    }

    /**
     * Deletes the associated row of the DataModel object in the database.
     *
     * @param tableField      used to identify the name of the table and column name
     * @param whereConditions a collection of standards by with a query is filtered
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise.
     */
    public synchronized int deleteDataModel(TableFieldInterface tableField,
                                            Criterion whereConditions) {
        int result = -1;
        if (tableField != null) {
            try {
                String whereClause = null;
                if (null != whereConditions) {
                    String s = whereConditions.getClause();
                    whereClause = whereConditions.getClause();
                }
                result = sqliteDatabase.delete(tableField.getTableName(),
                        whereClause, null);
            } catch (SQLException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
        return result;
    }


    /**
     * Updates the associated row(s) of a DataModel Object in the database.
     *
     * @param tableField     used to identify the name of the table
     * @param valueSet       a column name and associated value
     * @param whereCondition a standards by with a query is filtered
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise.
     */
    public synchronized int updateDataModel(TableFieldInterface tableField, Criterion valueSet,
                                            Criterion whereCondition) {
        return updateDataModel(tableField, new Criterion[]{valueSet}, new Criterion[]{whereCondition});
    }


    /**
     * Updates the associated row(s) of a DataModel Object in the database.
     *
     * @param tableField      used to identify the name of the table
     * @param valueSet        a collection of column name and associated value
     * @param whereConditions a collection of standards by with a query is filtered
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise.
     */
    public synchronized int updateDataModel(TableFieldInterface tableField, Criterion[] valueSet,
                                            Criterion[] whereConditions) {
        int result = -1;
        if (null != tableField && null != valueSet && null != whereConditions) {
            try {
                ContentValues contentValues = new ContentValues();
                for (Criterion v : valueSet) {
                    //Left and right value
                    contentValues.put(v.getLeftValue(), v.getRightValue());
                }
                StringBuilder sb = new StringBuilder();
                int len = whereConditions.length;
                for (int i = 0; i < len; i++) {
                    Criterion c = whereConditions[i];
                    sb.append(c.getClause());
                    if (i < (len - 1)) {
                        sb.append(" AND ");
                    }
                }
                result = sqliteDatabase.update(tableField.getTableName(),
                        contentValues, sb.toString(), null);
                Log.d(TAG, "updateDataModel(....) : " + result);
            } catch (SQLException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
        return result;
    }

    /**
     * Updates the associated row(s) of a DataModel Object in the database.
     *
     * @param tableField      used to identify the name of the table
     * @param valueSet        a collection of column name and associated value
     * @param whereConditions a collection of standards by with a query is filtered
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise.
     */
    public synchronized int updateDataModel(TableFieldInterface tableField, ArrayList<Criterion>
            valueSet,ArrayList<Criterion> whereConditions) {
        return updateDataModel(tableField, valueSet.toArray(new Criterion[valueSet.size()]),
                whereConditions.toArray(new Criterion[whereConditions.size()]));
    }

    public void dropTable(TableFieldInterface tableField) {
        if (null != tableField) {
            dropTable(tableField.getTableName());
        }
    }

    public void dropTable(String tableName) {
        if (null != tableName && tableName.length() > 0) {
            try {
                sqliteDatabase.execSQL(String.format("DROP TABLE IF EXISTS '%s'",
                        tableName));
            } catch (SQLException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }


    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {

            for (Map.Entry<String, TableFieldInterface[]> entry : map.entrySet()) {
                String key = entry.getKey();
                TableFieldInterface[] fields = entry.getValue();
                try {
                    String query = createTableSQLite(key, fields);
                    Log.i(TAG, query);
                    db.execSQL(query);
                } catch (SQLException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            Log.i(this.getClass().getName(), "onCreate(SQLiteDatabase db)",
                    null);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed

            for (Map.Entry<String, TableFieldInterface[]> entry : map.entrySet()) {
                String key = entry.getKey();
                TableFieldInterface[] fields = entry.getValue();
                try {
                    String query = dropTableSQL(key);
                    db.execSQL(query);
                } catch (SQLException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            onCreate(db);
        }
    }
}
