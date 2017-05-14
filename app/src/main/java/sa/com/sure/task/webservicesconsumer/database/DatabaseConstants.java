package sa.com.sure.task.webservicesconsumer.database;

import android.content.Context;

import sa.com.sure.task.webservicesconsumer.R;

/**
 * Created by HussainHajjar on 5/13/2017.
 */

public final class DatabaseConstants {
    public static final String DATABASE_PREFIX = "DB_";
    public static final String TABLE_PREFIX = "TB_";
    public static final String COLUMN_PREFIX   = "CL_";

    public enum Operators_Comparison{
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL
    }
    public static String getComparisonOperator(Operators_Comparison operator){
        switch(operator){
            case EQUAL: default: return "=";
            case GREATER_THAN: return ">";
            case GREATER_THAN_OR_EQUAL: return ">=";
            case LESS_THAN: return "<";
            case LESS_THAN_OR_EQUAL: return "<=";
            case NOT_EQUAL: return "!=";
        }
    }

    public enum Operators_Logical{
        AND,
        BETWEEN,
        EXISTS,
        IN,
        NOT_IN,
        LIKE,
        GLOB,
        NOT,
        OR,
        IS_NULL,
        IS,
        IS_NOT,
        UNIQUE,
    }

    public static String getLogicalOperator(Operators_Logical operator){
        switch(operator){
            case IS_NOT: case IS_NULL: case NOT_IN: return operator.toString().replace("_", " ");
            default: return operator.toString();
        }
    }

    public enum Order{ ASC, DESC }

    public static String getSelectStatement(Context context, String tableName, String columns, String condition){
        String selectQuery = context.getString(R.string.query_select)
                .replace("$_tableName", tableName)
                .replace("$_columns", columns)
                .replace("$_group", "")
                .replace("$_having", "")
                .replace("$_order", "")
                .replace("$_limit", "");
        if(condition != null && !condition.isEmpty()){
            String whereQuery = context.getString(R.string.query_select_where);
            selectQuery = selectQuery.replace("$_where", whereQuery.replace("$_condition", condition));
        }
        return selectQuery;
    }
}
