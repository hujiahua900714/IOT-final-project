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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseActivity extends AppCompatActivity {

    Button mainBtn, dbBtn, payBtn;

    private static final String DataBaseName = "DataBaseIt";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase db;
    private DBhelper dbHelper;

    boolean flag;
    Thread thread;
    AtomicInteger k = new AtomicInteger(1);

    Button renewBtn;
    TextView jsonTxt;
    String rawData = new String();

    public static final String HOST = "192.168.10.130";
    public static final int PORT = 3308;

    LinearLayout dbLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        mainBtn = (Button) findViewById(R.id.mainBtn);
        dbBtn = (Button) findViewById(R.id.dbBtn);
        payBtn = (Button) findViewById(R.id.payBtn);

        dbHelper = new DBhelper(this.getBaseContext(),DataBaseName,null,DataBaseVersion,DataBaseTable);
        db = dbHelper.getWritableDatabase();

        renewBtn = (Button) findViewById(R.id.renewBtn);
        jsonTxt = (TextView) findViewById(R.id.jsonTxt);

        dbLayout = (LinearLayout) findViewById(R.id.dbLayout);

        flag = true;

        thread = new Thread(()->{
            while(flag) {
                Log.v("thread", String.valueOf(k.get()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                k.addAndGet(1);
            }
        });
        thread.start();

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    threadJoin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(DatabaseActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    threadJoin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(DatabaseActivity.this,PayActivity.class);
                startActivity(intent);
            }
        });

        renewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataFormat dataFormat = new DataFormat("user","jeff",1);
                JSONObject jsonObject = new JSONObject();

                //send the id and get the data from server
//                try {
//                    String str = new String();
//                    jsonObject.put("identity", dataFormat.getIdentity());
//                    jsonObject.put("user", dataFormat.getUser());
//                    jsonObject.put("action", dataFormat.getAction());
//                    //jsonTxt.setText(jsonObject.toString());
//                    str = jsonObject.toString();
//                    rawData = sendMsgToHost(HOST,PORT,jsonObject.toString());
//                    jsonObject = new JSONObject(rawData);
//                    str = str + "\n" + rawData;
//                    jsonTxt.setText(str);
//                } catch (JSONException | IOException e) {
//                    e.printStackTrace();
//                }
                insertDB();
            }
        });
    }

    private String sendMsgToHost(String hostIP, int hostPort, String Msg) throws IOException {
        Socket socket = new Socket(hostIP, hostPort);

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);

        // 向伺服器傳輸自己的要求
        printWriter.write(Msg);
        printWriter.flush();

        // 根伺服器說已結束，要求只有這些，之後等待伺服器回傳資料
        socket.shutdownOutput();

        // 獲取確認連接資料
        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        len = inputStream.read(buf);
        System.out.println(new String(buf, 0, len, StandardCharsets.UTF_8));

        // 獲取伺服器傳回來資料
        byte[] bytes = new byte[1024];
        len = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while ((len = inputStream.read(bytes)) != -1){
            stringBuffer.append(new String(bytes, 0, len,"UTF-8"));
        }

        // 關閉所有接口
        inputStream.close();
        outputStream.close();
        socket.close();

        return stringBuffer.toString();
    }

    void insertDB() {
//        String goods = goodsInput.getText().toString();
//        String category = categoryInput.getText().toString();
//        String price = priceInput.getText().toString();
//        goodsInput.setText("");
//        categoryInput.setText("");
//        priceInput.setText("");

        long millis = System.currentTimeMillis();
        java.sql.Date sqlDate = new java.sql.Date(millis);
        String date = sqlDate.toString();

        long id;
        ContentValues contentValues = new ContentValues();
        contentValues.put("goods", "test");
        contentValues.put("price", "230");
        contentValues.put("category", "food");
        contentValues.put("date", date);

        id = db.insert(DataBaseTable, null, contentValues);
        updateDB();
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

    private void threadJoin() throws InterruptedException {
        flag = false;
        thread.join();
    }
}