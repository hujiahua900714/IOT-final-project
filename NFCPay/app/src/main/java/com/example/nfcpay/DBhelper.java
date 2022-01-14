package com.example.nfcpay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {

    private static final String DataBaseName = "DataBaseIt";
    private static final int DataBaseVersion = 1;

    public DBhelper(@Nullable Context context, @Nullable String name, @Nullable CursorFactory factory, int version, String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SqlTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "goods text not null," +
                "price INTEGER not null," +
                "category text not null," +
                "date text not null" +
                ")";
        sqLiteDatabase.execSQL(SqlTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL = "DROP TABLE Users";
        sqLiteDatabase.execSQL(SQL);
    }
}
