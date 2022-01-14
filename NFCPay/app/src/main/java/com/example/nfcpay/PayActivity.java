package com.example.nfcpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {

    Button mainBtn, dbBtn, payBtn;

    Button sendBtn;
    TextView resultTxt;

    static String json = "{\"contacDetails\": {\n" +   //JSON text in the file is written here
            "            \"firstName\": \"Ram\",\n" +
            "            \"lastName\": \"Sharma\"\n" +
            "    },\n" +
            "    \"phoneNumbers\": [\n" +
            "            {\n" +
            "                \"type\": \"home\",\n" +
            "                \"phone-number\": \"212 888-2365\"\n" +
            "            }\n" +
            "    ]" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mainBtn = (Button) findViewById(R.id.mainBtn);
        dbBtn = (Button) findViewById(R.id.dbBtn);
        payBtn = (Button) findViewById(R.id.payBtn);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        resultTxt = (TextView) findViewById(R.id.resultTxt);

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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(json);
                    Log.v("obj","\n===== obj success =====\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray arr = null;
                try {
                    arr = obj.getJSONArray("phoneNumbers");
                    Log.v("arr","\n===== arr success =====\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String str = new String();
                for (int i = 0; i < arr.length(); i++) {
                    String post_id = null;
                    try {
                        post_id = arr.getJSONObject(i).getString("phone-number");
                        str = str + post_id + "\n";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                resultTxt.setText(str);
            }
        });
    }
}