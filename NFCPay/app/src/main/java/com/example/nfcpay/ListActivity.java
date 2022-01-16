package com.example.nfcpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ListActivity extends AppCompatActivity {

    Button backBtn;

    LinearLayout dbLayout;

    private static final String DataBaseName = "DataBaseIt";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase db;
    private DBhelper dbHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        backBtn = (Button) findViewById(R.id.backBtn);

        dbLayout = (LinearLayout) findViewById(R.id.dbLayout);


        dbHelper = new DBhelper(this.getBaseContext(),DataBaseName,null,DataBaseVersion);
        DataBaseTable = dbHelper.getTableName();
        Log.v("ListActivity",DataBaseTable);
        db = dbHelper.getWritableDatabase();

        updateDB();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this,DatabaseActivity.class);
                startActivity(intent);
            }
        });
    }

    void updateDB() {
        StringBuilder allData = new StringBuilder();
        Cursor c = db.rawQuery("SELECT * FROM " + DataBaseTable, null);
        String[] idArray = new String[c.getCount()];
        String[] priceArray = new String[c.getCount()];
        String[] goodsArray = new String[c.getCount()];
        String[] categoryArray = new String[c.getCount()];
        String[] dateArray = new String[c.getCount()];
        dbLayout.removeAllViews();
        c.moveToFirst();
        String[] formatting = new String[5];
        allData.append(String.format("%-4s%-16s%-8s%-16s%-16s%n","_id","goods","price","category","date"));

        for(int i = 0; i < c.getCount(); i++) {
            Button btn = new Button(this);
            String revealedStr = new String();
            String hiddenStr = new String();
            idArray[i] = c.getString(0);
            formatting[0] = c.getString(0);

            goodsArray[i] = c.getString(1);
            formatting[1] = c.getString(1);

            priceArray[i] = c.getString(2);
            formatting[2] = c.getString(2);

            categoryArray[i] = c.getString(3);
            formatting[3] = c.getString(3);

            dateArray[i] = c.getString(4);
            formatting[4] = c.getString(4);

            revealedStr = String.format("%16s%8s", formatting[1], formatting[2]);
            hiddenStr = String.format("%4s%16s%16s", formatting[0], formatting[3], formatting[4]);
            btn.setText(revealedStr);
            btn.setTextColor(Color.BLACK);
            btn.setBackgroundColor(Color.LTGRAY);
            setClick(btn, revealedStr, hiddenStr, btn.getWidth(), btn.getHeight());
            dbLayout.addView(btn);
            c.moveToNext();
        }
        dbLayout.setPadding(8,8,8,8);
    }

    public void setClick(final Button btn, String str1, String str2, Integer int1, Integer int2) {
        btn.setOnClickListener(new View.OnClickListener() {
            boolean press = false;
            String revealedStr = str1;
            String hiddenStr = str2;
            Integer width = int1;
            Integer height = int2;
            LinearLayout background = findViewById(R.id.dbLayout);
            @Override
            public void onClick(View v) {
                background.setPadding(8,8,8,8);
                if(!press) {
                    press = true;
                    btn.setText(hiddenStr);
                    btn.setTextColor(Color.BLACK);
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setWidth(width);
                    btn.setHeight(height);
                }
                else {
                    press = false;
                    btn.setText(revealedStr);
                    btn.setTextColor(Color.BLACK);
                    btn.setBackgroundColor(Color.LTGRAY);
                    btn.setWidth(width);
                    btn.setHeight(height);
                }
            }
        });
    }
}