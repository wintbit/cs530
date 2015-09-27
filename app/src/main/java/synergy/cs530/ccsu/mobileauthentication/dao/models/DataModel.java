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

package synergy.cs530.ccsu.mobileauthentication.dao.models;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import synergy.cs530.ccsu.mobileauthentication.BuildConfig;
import synergy.cs530.ccsu.mobileauthentication.dao.interfaces.TableFieldInterface;

public class DataModel implements Serializable {

    private static final String TAG = DataModel.class.getName();

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private HashMap<String, String> map;
    private String tableName;
    private TableFieldInterface tableFieldInterface;

    /**
     * Default constructor
     */
    public DataModel() {
        super();
        setDefaultValues();
    }

    /**
     * Create default DataModel with given table name
     *
     * @param tableName used the identify a table name
     */
    public DataModel(String tableName) {
        setDefaultValues();
        this.tableName = tableName;
    }

    /**
     * Create default DataModel with given table name
     *
     * @param tableField a valid TableFieldInterface object
     */
    public DataModel(TableFieldInterface tableField) {
        setDefaultValues();
        if (null != tableField) {
            this.tableFieldInterface = tableField;
            this.tableName = tableField.getTableName();
        }
    }

    /**
     * Returns whether this map contains the specified key.
     *
     * @param key the key to search for.
     * @return true if key exist else false
     */
    public boolean hasKey(String key) {
        return (null != key && key.length() > 0) && map.containsKey(key);
    }

    public boolean hasKey(TableFieldInterface tableField) {
        return (null != tableField) ? hasKey(tableField.getColumnName()) : null;
    }

    private void setDefaultValues() {
        this.tableName = null;
        this.map = new HashMap<>();
    }

    public TableFieldInterface getTableFieldInterface() {

        return tableFieldInterface;
    }

    public void put(String key, String value) {
        if (null != key && key.length() > 0) {
            map.put(key, value);
        }
    }

    public void put(String key, int value) {
        put(key, Integer.toString(value));
    }

    public void put(String key, long value) {
        put(key, Long.toString(value));
    }

    public void put(String key, boolean value) {
        if (null != key && key.length() > 0) {
            put(key, (value) ? 1 : 0);
        }
    }

    public void put(TableFieldInterface tableField, String value) {
        if (null != tableField) {
            put(tableField.getColumnName(), value);
        }
    }

    public void put(TableFieldInterface tableField, long time) {
        if (null != tableField) {
            put(tableField.getColumnName(), time);
        }
    }

    public void put(TableFieldInterface tableField, boolean value) {
        if (null != tableField) {
            put(tableField.getColumnName(), (value) ? 1 : 0);
        }
    }

    public void put(TableFieldInterface tableField, int value) {
        if (null != tableField) {
            put(tableField.getColumnName(), value);
        }
    }

    private String get(String field) {
        return (null != field && field.length() > 0) ? map.get(field) : null;
    }


    public String get(TableFieldInterface tableField) {
        String result = null;
        if (null != tableField) {
            result = get(tableField.getColumnName());
        }
        return result;
    }

    public String get(TableFieldInterface tableField, String defaultValue) {
        String result = defaultValue;
        if (null != tableField) {
            String res = get(tableField.getColumnName());
            if (null != res) {
                result = res;
            }
        }
        return result;
    }


    public String getTableName() {
        return tableName;
    }

    public int size() {
        return map.size();
    }


    public long getLong(TableFieldInterface tableField, long defaultValue) {
        long result = defaultValue;
        if (null != tableField) {
            String res = get(tableField.getColumnName());
            if (null != res) {
                try {
                    result = Long.parseLong(res);
                } catch (NullPointerException ex) {
                    Log.e(TAG + " getLong(..)", ex.getMessage());
                }
            }
        }
        return result;
    }

    public int getInt(TableFieldInterface tableField, int defaultValue) {
        int result = defaultValue;
        if (null != tableField) {
            String res = get(tableField.getColumnName());
            if (null != res) {
                try {
                    result = Integer.parseInt(res);
                } catch (NumberFormatException ex) {
                    Log.e(TAG + " getInt(..)", String.format("key:%s, %s",
                            tableField.getColumnName(), ex.getMessage()));
                }
            }
        }
        return result;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    private void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public Boolean getBoolean(TableFieldInterface tableField, boolean defaultValue) {
        boolean result = defaultValue;
        if (null != tableField) {
            result = (getInt(tableField, (defaultValue) ? 1 : 0) > 0);
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(tableName);
        if (BuildConfig.DEBUG) {
            sb.append(" { ");
            int i = 0;
            int size = map.size();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey()).append(" = ").append(entry.getValue());
                if (i < (size - 1)) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append(" }");
        }
        return sb.toString();
    }

    public String getValueAtIndex(int position) {
        String result = null;
        int i = 0;
        for (String entry : map.values()) {
            if (i == position) {
                result = entry;
                break;
            }
            i++;
        }
        return result;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean isKeyEmpty(TableFieldInterface fieldInterface) {
        return (null != fieldInterface && !map.isEmpty()) && map.containsKey(fieldInterface.getColumnName());
    }

}
