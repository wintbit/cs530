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

package synergy.cs530.ccsu.mobileauthentication.dao.enums;

import synergy.cs530.ccsu.mobileauthentication.dao.interfaces.TableFieldInterface;

public enum TapSequenceTableEnum implements TableFieldInterface {


    KEY_ROWID("_id", "", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    KEY_SEQUENCE_ID("sequence_id", "", "NUMERIC "),
    KEY_X_AXIS("x_axis", "", "NUMERIC"),
    KEY_Y_AXIS("y_axis", "", "NUMERIC"),
    KEY_TIME("time", "", "NUMERIC")
;

    private final String fieldName;
    private final String mysqlFieldType;
    private final String sqliteFieldType;

    TapSequenceTableEnum(String fieldName, String mysqlFieldType,
                         String sqliteFieldType) {
        this.fieldName = fieldName;
        this.mysqlFieldType = mysqlFieldType;
        this.sqliteFieldType = sqliteFieldType;
    }

    public String getTableName() {
        return "tap_sequence";
    }

    @Override
    public String getColumnName() {
        return fieldName;
    }

    @Override
    public String getTableAlias() {
        return "tp_ptn";
    }

    @Override
    public String getTableWithAlias() {
        return getTableName() + " AS " + getTableAlias();
    }

    @Override
    public String getColumnNameWithAlias() {
        return getTableAlias() + "." + getColumnName();
    }

    @Override
    public String getMySQLFieldType() {
        return mysqlFieldType;
    }

    @Override
    public String getSQLiteFieldType() {
        return sqliteFieldType;
    }
}
