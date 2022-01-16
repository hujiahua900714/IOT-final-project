package com.example.nfcpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayActivity extends AppCompatActivity {

    Button mainBtn, dbBtn, payBtn, logoutBtn;

    private static final String DataBaseName = "DataBaseIt";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase db;
    private DBhelper dbHelper;

    private CodeScanner cameraScanner;
    CodeScannerView cameraView;
    TextView uriText;
    ArrayList<String> parser = new ArrayList<String>();
    String uriRegex = "([^:\n]+):([\\d]+):([^:\n]+)([\n]?)";

    public static final String HOST = "192.168.137.1";
    public static final int PORT = 3308;
    String sendData, getData;
    Thread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setPermission();

        mainBtn = (Button) findViewById(R.id.mainBtn);
        dbBtn = (Button) findViewById(R.id.dbBtn);
        payBtn = (Button) findViewById(R.id.payBtn);


        //DataBaseTable = dbHelper.getTableName();
        dbHelper = new DBhelper(this.getBaseContext(),DataBaseName,null,DataBaseVersion);
        db = dbHelper.getWritableDatabase();

        cameraView = (CodeScannerView) findViewById(R.id.cameraView);
        cameraScanner = new CodeScanner(this, cameraView);

        uriText = (TextView) findViewById(R.id.uriText);
        //uriBtn = (Button) findViewById(R.id.uriBtn);
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        CameraSource cameraSource=new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(300,300).setAutoFocusEnabled(true).build();


        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        dbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this,DatabaseActivity.class);
                startActivity(intent);
            }
        });

        cameraScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPermission();
                        Toast.makeText(PayActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        uriText.setText(result.getText());
                        String uriStr = result.getText();
                        Pattern pattern = Pattern.compile(uriRegex);
                        Matcher matcher = pattern.matcher(uriStr);
                        if(matcher.find()) {
                            insertDB(matcher.group(1), matcher.group(2), matcher.group(3));
                        }
                        else {
                            Log.v("none", "find no group");
                        }
                    }
                });
            }
        });

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPermission();
                cameraScanner.startPreview();
            }
        });
    }

    private Runnable sendMsgToHost = new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            Log.v("connectThread", "the thread is start");
            try {
                socket = new Socket(HOST, PORT);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream);

                // 向伺服器傳輸自己的要求
                printWriter.write(sendData);
                printWriter.flush();

                // 根伺服器說已結束，要求只有這些，之後等待伺服器回傳資料
                socket.shutdownOutput();

                // 獲取確認連接資料
                InputStream inputStream = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                len = inputStream.read(buf);
                //System.out.println(new String(buf, 0, len, StandardCharsets.UTF_8));

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

                getData = stringBuffer.toString();
                //jsonTxt.setText(getData);
                Log.v("connectThread", getData);
                //return stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("connectThread", "the thread is end");
        }
    };

    void insertDB(String item, String price, String category) {
        long millis = System.currentTimeMillis();
        java.sql.Date sqlDate = new java.sql.Date(millis);
        String date = sqlDate.toString();

        long id;
        ContentValues contentValues = new ContentValues();
        contentValues.put("goods", item);
        contentValues.put("price", Integer.valueOf(price));
        contentValues.put("category", category.toLowerCase());
        Log.v("category",category.toLowerCase());
        contentValues.put("date", date);

        id = db.insert(DataBaseTable, null, contentValues);

        JSONObject jsonObject = new JSONObject();
        try {
            String str = new String();
            jsonObject.put("identity", "raspberryPi");
            jsonObject.put("user", dbHelper.getTableName());
            jsonObject.put("action", "POST");
            jsonObject.put("goods", item);
            jsonObject.put("price", price);
            jsonObject.put("category", category);
            jsonObject.put("spendTime", date);
            jsonObject.put("remark", "");
            //jsonTxt.setText(jsonObject.toString());
            str = jsonObject.toString();
            //rawData = sendMsgToHost(HOST,PORT,jsonObject.toString());
            //jsonObject = new JSONObject(rawData);
            //str = str + "\n" + rawData;
            sendData = str;
            connectThread = new Thread(sendMsgToHost);
            connectThread.start();
            //jsonTxt.setText(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //updateDB();
    }

    public void setPermission() {
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                    24);
        }
    }
}