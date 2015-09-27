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

import java.util.MissingFormatArgumentException;

import synergy.cs530.ccsu.mobileauthentication.dao.interfaces.TableFieldInterface;

/**
 * Created by ewint on 9/9/2015.
 */
public class Criterion {
    public final String TAG = this.getClass().getName();

    private String leftValue;
    private String rightValue;
    private String[] rightValues;
    private OperatorEnum operand = OperatorEnum.EQUALS;

    public Criterion(String leftValue, String rightValue) {
        this.leftValue = leftValue;
        this.rightValue = getSQLString(rightValue);
    }

    public Criterion(String leftValue, int rightValue) {
        this.leftValue = leftValue;
        this.rightValue = Integer.toString(rightValue);
    }

    public Criterion(String leftValue, long rightValue) {
        this.leftValue = leftValue;
        this.rightValue = Long.toString(rightValue);
    }

    public Criterion(String leftValue, boolean rightValue) {
        this.leftValue = leftValue;
        this.rightValue = (rightValue) ? "1" : "0";
    }

    public Criterion(TableFieldInterface leftValue, int rightValue) {
        this.leftValue = leftValue.getColumnName();
        this.rightValue = Integer.toString(rightValue);
    }

    public Criterion(TableFieldInterface leftValue, String rightValue) {
        this.leftValue = leftValue.getColumnName();
        this.rightValue = getSQLString(rightValue);
    }


    public Criterion(String leftValue, String rightValue, OperatorEnum whereOperand) {
        this.leftValue = leftValue;
        this.rightValue = getSQLString(rightValue);
        this.operand = whereOperand;
    }

    public Criterion(String leftValue, int rightValue, OperatorEnum whereOperand) {
        this.leftValue = leftValue;
        this.rightValue = Integer.toString(rightValue);
        this.operand = whereOperand;
    }

    public Criterion(TableFieldInterface leftValue, int rightValue, OperatorEnum whereOperand) {
        this.leftValue = leftValue.getColumnName();
        this.rightValue = Integer.toString(rightValue);
        this.operand = whereOperand;
    }

    public Criterion(TableFieldInterface leftValue, boolean rightValue) {
        this.leftValue = leftValue.getColumnName();
        this.rightValue = (rightValue) ? "1" : "0";
    }


    public Criterion(String leftValue, String[] rightInputs, OperatorEnum whereOperand) {
        this.leftValue = leftValue;
        if (rightInputs != null) {
            int len = rightInputs.length;
            this.rightValues = new String[len];
            for (int i = 0; i < len; i++) {
                this.rightValues[i] = getSQLString(rightInputs[i]);
            }
        }
        this.operand = whereOperand;
    }

    public Criterion(TableFieldInterface leftValue, String[] rightInputs, OperatorEnum whereOperand) {
        this.leftValue = leftValue.getColumnName();
        if (rightInputs != null) {
            int len = rightInputs.length;
            this.rightValues = new String[len];
            for (int i = 0; i < len; i++) {
                this.rightValues[i] = getSQLString(rightInputs[i]);
            }
        }
        this.operand = whereOperand;
    }

    private String getSQLString(String input) {
        return String.format("'%s'", input);
    }

    public String getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setRightValue(String[] values) {
        this.rightValue = getCommaDelimValues(values);
    }

    public void setValue(String value) {
        this.rightValue = value;
    }

    private String getCommaDelimValues(String[] values) {
        String result = null;
        if (null != values && values.length > 0) {
            StringBuilder sb = new StringBuilder();
            int len = values.length;
            for (int i = 0; i < len; i++) {
                sb.append(values[i]);
                if (i < (len - 1)) {
                    sb.append(", ");
                }
            }
            result = sb.toString();
        }
        return result;
    }

    public OperatorEnum getOperand() {
        return operand;
    }

    public void setOperand(OperatorEnum operand) {
        this.operand = operand;
    }

    public String getClause() {

        StringBuilder sb = new StringBuilder();
        if (leftValue != null && leftValue.length() > 0) {
            try {
                switch (operand) {
                    case IN:
                        String val = rightValue;
                        if (rightValues != null && rightValues.length > 0) {
                            val = getCommaDelimValues(rightValues);
                        }
                        if (val != null) {
                            sb.append(String.format("%s %s ( %s )", leftValue, operand.getValue(), val));
                        }
                        break;
                    case EXIST:
                        sb.append(String.format("%s %s ( %s )", leftValue, operand.getValue(), rightValue));
                        break;
                    case BETWEEN:
                        if (rightValues != null && rightValues.length == 2) {
                            sb.append(String.format("%s %s %s AND %s", leftValue, operand.getValue(),
                                    rightValues[0], rightValues[1]));
                        }
                        break;
                    case IS_NULL:
                    case IS_NOT_NULL:
                        if (rightValue != null) {
                            sb.append(String.format("%s %s", leftValue, operand.getValue()));
                        }
                        break;
                    default:
                        if (rightValue != null) {
                            sb.append(String.format("%s %s %s", leftValue, operand.getValue(), rightValue));
                        }
                        break;
                }
            } catch (MissingFormatArgumentException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
        return sb.toString();

    }

    @Override
    public String toString() {
        return getClause();
    }
}
