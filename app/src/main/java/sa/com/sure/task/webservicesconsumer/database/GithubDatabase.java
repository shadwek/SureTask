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
import sa.com.sure.task.webservicesconsumer.model.GithubUser;

/**
 * Created by HussainHajjar on 5/14/2017.
 */

public class GithubDatabase extends SQLiteOpenHelper{
    private Context mContext;
    public static final String DATABASE_NAME = DatabaseConstants.DATABASE_PREFIX + "GITHUB";
    public static final int DATABASE_VERSION = 1;

    public static class UserTable {
        private UserTable(){}
        public static final String TABLE_NAME = DatabaseConstants.TABLE_PREFIX + "USER";
        public static class Columns implements BaseColumns {
            public static final ArrayList<Column> COLUMN_LIST = new ArrayList<Column>(){
                {
                    add(new Column(true, _ID, Column.Type.INTEGER));
                    add(new Column("ID",      Column.Type.TEXT, false));
                    add(new Column("AVATAR_URL",     Column.Type.TEXT, false ));
                    add(new Column("HTML_URL",  Column.Type.TEXT, false));
                    add(new Column("LOGIN", Column.Type.TEXT, false));
                }
            };
            public static String getColumnString(String delimiter){
                String columnsString = "";
                for(Column column : COLUMN_LIST)
                    columnsString += column.getName() + delimiter;
                return columnsString.substring(0, columnsString.length() - delimiter.length());
            }

            public static Column getTableIdColumn(){    return COLUMN_LIST.get(0); }
            public static Column getIdColumn(){         return COLUMN_LIST.get(1); }
            public static Column getLoginColumn(){      return COLUMN_LIST.get(2); }
            public static Column getAvatarUrlColumn(){  return COLUMN_LIST.get(3); }
            public static Column getHtmlUrlColumn(){    return COLUMN_LIST.get(4); }
        }
    }

    public ContentValues convertToContentValues(GithubUser user){
        ContentValues contentValues = new ContentValues();
        if(!user.getLogin().isEmpty())
            contentValues.put(GithubDatabase.UserTable.Columns.getLoginColumn().getName(), user.getLogin());
        if(!user.getAvatar_url().isEmpty())
            contentValues.put(GithubDatabase.UserTable.Columns.getAvatarUrlColumn().getName(), user.getAvatar_url());
        if(!user.getHtml_url().isEmpty())
            contentValues.put(GithubDatabase.UserTable.Columns.getHtmlUrlColumn().getName(), user.getHtml_url());
        if(user.getId() > 0)
            contentValues.put(GithubDatabase.UserTable.Columns.getIdColumn().getName(), user.getId());
        return contentValues;
    }

    public List<GithubUser> convertCursorToUsers(Cursor cursor){
        List<GithubUser> users = new ArrayList<GithubUser>();
        while(cursor.moveToNext()){
            GithubUser user = new GithubUser();
            user.setTableId(    cursor.getLong(  cursor.getColumnIndex(GithubDatabase.UserTable.Columns.getTableIdColumn().getName())));
            user.setLogin(      cursor.getString(cursor.getColumnIndex(GithubDatabase.UserTable.Columns.getLoginColumn().getName())));
            user.setAvatar_url( cursor.getString(cursor.getColumnIndex(GithubDatabase.UserTable.Columns.getAvatarUrlColumn().getName())));
            user.setHtml_url(   cursor.getString(cursor.getColumnIndex(GithubDatabase.UserTable.Columns.getHtmlUrlColumn().getName())));
            user.setId(         cursor.getInt(   cursor.getColumnIndex(GithubDatabase.UserTable.Columns.getIdColumn().getName())));
            users.add(user);
        }
        return  users;
    }

    public GithubDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSyntax = mContext.getString(R.string.table_create);
        String createTableColumnSyntax = mContext.getString(R.string.table_create_columns);
        String tableColumns = "";
        for(Column column : GithubDatabase.UserTable.Columns.COLUMN_LIST){
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
        db.execSQL(createTableSyntax.replace("$_tableName", GithubDatabase.UserTable.TABLE_NAME).replace("$_columns", tableColumns));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableSyntax = mContext.getString(R.string.table_drop_if_exist);
        db.execSQL(dropTableSyntax.replace("$_tableName", GithubDatabase.UserTable.TABLE_NAME));
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int insert(List<GithubUser> users){
        SQLiteDatabase db = getWritableDatabase();
        int numInsertedEntries = 0;
        for(GithubUser user : users) {
            db.beginTransaction();
            if(db.insert(GithubDatabase.UserTable.TABLE_NAME, null, convertToContentValues(user)) != -1)
                numInsertedEntries++;
            db.endTransaction();
        }
        return numInsertedEntries;
    }

    public boolean has(List<GithubUser> githubUsers){
        SQLiteDatabase db = getReadableDatabase();
        String ids = "";
        for(GithubUser user : githubUsers)
            ids += "'" + user.getId() + "',";
        ids = ids.substring(0, ids.length() - 1);
        String condition = UserTable.Columns.getIdColumn().getName() + " " +
                DatabaseConstants.getLogicalOperator(DatabaseConstants.Operators_Logical.IN) +
                " ( " + ids + " )";
        String selectQuery = DatabaseConstants.getSelectStatement(mContext,
                UserTable.TABLE_NAME, UserTable.Columns.getColumnString(", "), condition);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() != 0;
    }

    public List<GithubUser> get(List<GithubUser> githubUsers){
        SQLiteDatabase db = getReadableDatabase();
        String ids = "";
        for(GithubUser user : githubUsers)
            ids += user.getId() + ",";
        ids = ids.substring(0, ids.length() - 1);
        String condition = UserTable.Columns.getIdColumn().getName() + " " +
                DatabaseConstants.getLogicalOperator(DatabaseConstants.Operators_Logical.IN) +
                " ( " + ids + " )";
        String selectQuery = DatabaseConstants.getSelectStatement(mContext,
                UserTable.TABLE_NAME, UserTable.Columns.getColumnString(", "), condition);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return convertCursorToUsers(cursor);
    }
}
