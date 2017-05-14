package sa.com.sure.task.webservicesconsumer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.model.UsState;

/**
 * Created by HussainHajjar on 5/13/2017.
 */

public class UsStateDatabase extends SQLiteOpenHelper {

    private Context mContext;
    public static final String DATABASE_NAME = DatabaseConstants.DATABASE_PREFIX + "US_STATE";
    public static final int DATABASE_VERSION = 1;

    public static class StateTable {
        private StateTable(){}
        public static final String TABLE_NAME = DatabaseConstants.TABLE_PREFIX + "STATE";
        public static class Columns implements BaseColumns{
            public static final ArrayList<Column> COLUMN_LIST = new ArrayList<Column>(){
                {
                    add(new Column(true, _ID, Column.Type.INTEGER));
                    add(new Column("CITY",      Column.Type.TEXT, false));
                    add(new Column("STATE",     Column.Type.TEXT, false ));
                    add(new Column("ZIP_CODE",  Column.Type.TEXT, false));
                    add(new Column("TIME_ZONE", Column.Type.TEXT, false));
                    add(new Column("AREA_CODE", Column.Type.TEXT, false));
                }
            };
            public static String getColumnString(String delimiter){
                String columnsString = "";
                for(Column column : COLUMN_LIST)
                    columnsString += column.getName() + delimiter;
                return columnsString.substring(0, columnsString.length() - delimiter.length());
            }

            public static Column getIdColumn(){         return COLUMN_LIST.get(0); }
            public static Column getCityColumn(){       return COLUMN_LIST.get(1); }
            public static Column getStateColumn(){      return COLUMN_LIST.get(2); }
            public static Column getZipCodeColumn(){    return COLUMN_LIST.get(3); }
            public static Column getTimeZoneColumn(){   return COLUMN_LIST.get(4); }
            public static Column getAreaCodeColumn(){   return COLUMN_LIST.get(5); }
        }
    }

    public ContentValues convertToContentValues(UsState.State state){
        ContentValues contentValues = new ContentValues();
        if(!state.getCity().isEmpty())
            contentValues.put(StateTable.Columns.getCityColumn().getName(), state.getCity());
        if(!state.getState().isEmpty())
            contentValues.put(StateTable.Columns.getStateColumn().getName(), state.getState());
        if(!state.getZipCode().isEmpty())
            contentValues.put(StateTable.Columns.getZipCodeColumn().getName(), state.getZipCode());
        if(!state.getTimeZone().isEmpty())
            contentValues.put(StateTable.Columns.getTimeZoneColumn().getName(), state.getTimeZone());
        if(!state.getAreaCode().isEmpty())
            contentValues.put(StateTable.Columns.getAreaCodeColumn().getName(), state.getAreaCode());
        return contentValues;
    }

    public List<UsState.State> convertCursorToStates(Cursor cursor){
        List<UsState.State> states = new ArrayList<UsState.State>();
        while(cursor.moveToNext()){
            UsState.State state = new UsState.State();
            state.setTableId( cursor.getLong(  cursor.getColumnIndex(StateTable.Columns.getIdColumn().getName())));
            state.setCity(    cursor.getString(cursor.getColumnIndex(StateTable.Columns.getCityColumn().getName())));
            state.setState(   cursor.getString(cursor.getColumnIndex(StateTable.Columns.getStateColumn().getName())));
            state.setZipCode( cursor.getString(cursor.getColumnIndex(StateTable.Columns.getZipCodeColumn().getName())));
            state.setTimeZone(cursor.getString(cursor.getColumnIndex(StateTable.Columns.getTimeZoneColumn().getName())));
            state.setAreaCode(cursor.getString(cursor.getColumnIndex(StateTable.Columns.getAreaCodeColumn().getName())));
            states.add(state);
        }
        return  states;
    }

    public UsStateDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSyntax = mContext.getString(R.string.table_create);
        String createTableColumnSyntax = mContext.getString(R.string.table_create_columns);
        String tableColumns = "";
        for(Column column : StateTable.Columns.COLUMN_LIST){
            tableColumns += createTableColumnSyntax
                    .replace("$_columnName", column.getName())
                    .replace("$_columnType", column.getType());
            String isMandatory = "";
            if(column.isMandatory()) isMandatory = mContext.getString(R.string.column_create_not_null);
            String isPrimary = "";
            if(column.isPrimary()) isPrimary = mContext.getString(R.string.column_create_primary);
            tableColumns = tableColumns.replace("$_null", isMandatory);
            tableColumns = tableColumns.replace("$_primary", isPrimary) + ",\n";
        }
        tableColumns = tableColumns.substring(0, tableColumns.length() - ",\n".length());
        db.execSQL(createTableSyntax.replace("$_tableName", StateTable.TABLE_NAME).replace("$_columns", tableColumns));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableSyntax = mContext.getString(R.string.table_drop_if_exist);
        db.execSQL(dropTableSyntax.replace("$_tableName", StateTable.TABLE_NAME));
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int insert(List<UsState.State> states){
        SQLiteDatabase db = getWritableDatabase();
        int numInsertedEntries = 0;
        for(UsState.State state : states) {
            db.beginTransaction();
            if(db.insert(StateTable.TABLE_NAME, null, convertToContentValues(state)) != -1)
                numInsertedEntries++;
            db.endTransaction();
        }
        return numInsertedEntries;
    }

    public boolean has(List<UsState.State> states){
        SQLiteDatabase db = getReadableDatabase();
        String ids = "";
        for(UsState.State state : states)
            ids += "'" + state.getZipCode() + "',";
        ids = ids.substring(0, ids.length() - 1);
        String condition = UsStateDatabase.StateTable.Columns.getZipCodeColumn().getName() + " " +
                DatabaseConstants.getLogicalOperator(DatabaseConstants.Operators_Logical.IN) +
                " ( " + ids + " )";
        String selectQuery = DatabaseConstants.getSelectStatement(mContext,
                StateTable.TABLE_NAME, StateTable.Columns.getColumnString(", "), condition);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() != 0;
    }

    public List<UsState.State> get(UsState.State... githubUsers){
        SQLiteDatabase db = getReadableDatabase();
        String ids = "";
        for(UsState.State state : githubUsers)
            ids += state.getZipCode() + ",";
        ids = ids.substring(0, ids.length() - 1);
        String condition = UsStateDatabase.StateTable.Columns.getZipCodeColumn().getName() + " " +
                DatabaseConstants.getLogicalOperator(DatabaseConstants.Operators_Logical.IN) +
                " ( " + ids + " )";
        String selectQuery = DatabaseConstants.getSelectStatement(mContext,
                StateTable.TABLE_NAME, StateTable.Columns.getColumnString(", "), condition);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return convertCursorToStates(cursor);
    }
}