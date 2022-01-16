package com.example.nfcpay;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;

public class DBhelper extends SQLiteOpenHelper {

    private static final String DataBaseName = "DataBaseIt";
    private static final int DataBaseVersion = 1;
    private static String TableName = "Users";
    public DBhelper(@Nullable Context context, @Nullable String name, @Nullable CursorFactory factory, int version) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SqlTable = "CREATE TABLE IF NOT EXISTS "+ TableName +" (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "goods text not null," +
                "price INTEGER not null," +
                "category text not null," +
                "date text not null" +
                ")";

        sqLiteDatabase.execSQL(SqlTable);
    }

    public void setTableName(String name, SQLiteDatabase db) {
        TableName = name;
        String SqlTable = "CREATE TABLE IF NOT EXISTS "+ TableName +" (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "goods text not null," +
                "price INTEGER not null," +
                "category text not null," +
                "date text not null" +
                ")";
        db.execSQL(SqlTable);
    }

    public String getTableName() {
        return TableName;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL = "DROP TABLE Users";
        sqLiteDatabase.execSQL(SQL);
    }
}
