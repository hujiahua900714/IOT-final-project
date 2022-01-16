package com.example.nfcpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseActivity extends AppCompatActivity {

    Button mainBtn, dbBtn, payBtn, logoutBtn;

    private static final String DataBaseName = "DataBaseIt";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase db;
    private DBhelper dbHelper;
    private Cursor cursor;

    int sumValue;
    int[] categoryValue;
    //food, drink, fun, traffic, 3c
    String[] categories;

    BarChart barChart;

    Thread connectThread;
    AtomicInteger k = new AtomicInteger(1);
    String sendData,getData;

    Button listBtn;

    public static final String HOST = "192.168.137.1";
    public static final int PORT = 3308;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        mainBtn = (Button) findViewById(R.id.mainBtn);
        dbBtn = (Button) findViewById(R.id.dbBtn);
        payBtn = (Button) findViewById(R.id.payBtn);

        listBtn = (Button) findViewById(R.id.listBtn);

        //DataBaseTable = dbHelper.getTableName();
        dbHelper = new DBhelper(this.getBaseContext(),DataBaseName,null,DataBaseVersion);
        db = dbHelper.getWritableDatabase();
        categoryValue = new int[]{0, 0, 0, 0, 0};
        categories = new String[]{"food","drink","fun","traffic","3c"};

        barChart = (BarChart) findViewById(R.id.barChart);

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatabaseActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatabaseActivity.this,PayActivity.class);
                startActivity(intent);
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatabaseActivity.this,ListActivity.class);
                startActivity(intent);
            }
        });

        setGraph();
    }

    private void setGraph() {
        cursor = db.rawQuery("SELECT SUM(price) FROM Users", null );
        cursor.moveToFirst();
        Log.v("all price",String.valueOf(cursor.getInt(0)));
        sumValue = cursor.getInt(0);
        cursor.close();

        for(int i = 0; i < 5; i++) {
            cursor = db.rawQuery("SELECT SUM(price) FROM Users WHERE category=\'" + categories[i] + "\'", null );
            cursor.moveToFirst();
            //Log.v("all price",String.valueOf(cursor.getInt(0)));
            categoryValue[i] = cursor.getInt(0);
            Log.v("category" + String.valueOf(i), String.valueOf(categoryValue[i]));
            cursor.close();
        }



        // Set the percentage of language used
//        PieModel pie = new PieModel();
        // Set the data and color to the pie chart

        barChart.addBar(
                new BarModel(
                        categories[0],
                        Integer.valueOf(categoryValue[0]),
                        Color.parseColor("#FFA726")));
        barChart.addBar(
                new BarModel(
                        categories[1],
                        Integer.valueOf(categoryValue[1]),
                        Color.parseColor("#66BB6A")));
        barChart.addBar(
                new BarModel(
                        categories[2],
                        Integer.valueOf(categoryValue[2]),
                        Color.parseColor("#EF5350")));
        barChart.addBar(
                new BarModel(
                        categories[3],
                        Integer.valueOf(categoryValue[3]),
                        Color.parseColor("#29B6F6")));
        barChart.addBar(
                new BarModel(
                        categories[4],
                        Integer.valueOf(categoryValue[4]),
                        Color.parseColor("#29B6F6")));
//        pieChart.setInnerValueString(categories[2]);
        // To animate the pie chart
        barChart.startAnimation();
    }
}