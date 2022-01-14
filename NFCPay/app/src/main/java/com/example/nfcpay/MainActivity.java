package com.example.nfcpay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button mainBtn, dbBtn, payBtn;

    String[] permissionCode = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE };

    TextView weatherTxt;
    TextView weatherDataTxt;
    String TAG = "MainActivity";
    String getWeatherURL = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/" +
            "F-D0047-007" +
            "?Authorization=CWB-A78BDE64-AEBA-4C17-BB26-DF01D492EF41" +
            "&format=JSON";
    String weatherURL = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/" +
            "F-C0032-001" +
            "?Authorization=CWB-A78BDE64-AEBA-4C17-BB26-DF01D492EF41" +
            "&format=JSON" +
            "&elementName=MinT,MaxT,Wx" +
            "&sort=time";
    Gson gson = new Gson();
    String weatherData;
    URL url;
    File fileDirectory;
    String weatherDirectory = "weather";
    String fileName = "test.json";

    private Handler handler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermission();


        mainBtn = (Button) findViewById(R.id.mainBtn);
        dbBtn = (Button) findViewById(R.id.dbBtn);
        payBtn = (Button) findViewById(R.id.payBtn);

        weatherTxt = (TextView) findViewById(R.id.weatherTxt);
        weatherDataTxt = (TextView) findViewById(R.id.weatherDataTxt);

        handler = new Handler();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //handler.post(downloadWeatherJSON());
                downloadWeatherJSON();
                //revealWeatherJSON();
            }
        });

        dbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DatabaseActivity.class);
                startActivity(intent);
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PayActivity.class);
                startActivity(intent);
            }
        });
    }

    private void downloadWeatherJSON() {
        new Thread(()-> {
            try {
                fileDirectory = getExternalFilesDir(weatherDirectory);
                if (!fileDirectory.exists()) {
                    Log.v(TAG,"create directory");
                    fileDirectory.mkdirs();
                }

                //String fileName = "test.json";
                File file = new File(fileDirectory + "/" + fileName);
                Log.v(TAG,"create new file");

                Log.v(TAG,file.getAbsolutePath());

                FileOutputStream fileWriter = new FileOutputStream(file);
                Log.v(TAG,"write file");

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileWriter);

                //outputStreamWriter.write("");
                Log.v(TAG,"create new file writer");

                url = new URL(weatherURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));

                String line = in.readLine();
                StringBuffer json = new StringBuffer();

                while (line != null) {
                    json.append(line);
                    //fileWriter.write(line);
                    //fileWriter.flush();
                    outputStreamWriter.write(line);
                    outputStreamWriter.flush();
                    line = in.readLine();
                }
                //fileWriter.close();
                outputStreamWriter.close();

                //Log.v(TAG,""+json);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(revealWeatherJSON);
        }).start();
    }

    Runnable revealWeatherJSON = new Runnable() {
        @Override
        public void run() {
            String ret = "";
            fileDirectory = getExternalFilesDir(weatherDirectory);
            try{
                if (!fileDirectory.exists()) {
                    Log.v(TAG,"create directory");
                    fileDirectory.mkdirs();
                }
                File file = new File(fileDirectory+"/"+fileName);
                FileInputStream fileReader = new FileInputStream(file);
                Log.v(TAG,"read file");
                if ( fileReader != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(fileReader);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        Log.v(TAG, "receiving\n");
                        Log.v(TAG, receiveString.toString());
                        stringBuilder.append("\n").append(receiveString);
                    }

                    fileReader.close();
                    ret = stringBuilder.toString();
                    weatherData = ret;
                    weatherTxt.setText(ret);
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
                e.printStackTrace();
            }

            try {
                setWeatherData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void setWeatherData() throws JSONException {
        JSONObject jsonObject = new JSONObject(weatherData);
        String allData = jsonObject.getString("records");
        JSONObject jsonObjectAllLocationData = new JSONObject(allData);
        JSONArray allLocationData = jsonObjectAllLocationData.getJSONArray("location");
        Log.v("json array", String.valueOf(allLocationData.length()));

        String test = new String();

        for(int i = 0; i < allLocationData.length(); i++) {
            JSONObject jsonObjectLocationData = (JSONObject) allLocationData.get(i);
            test = test + jsonObjectLocationData.getString("locationName") + "\n";

            JSONArray allWeatherElementData = jsonObjectLocationData.getJSONArray("weatherElement");
            Log.v("allWeatherElementData", String.valueOf(allWeatherElementData.length()));

            for(int j = 0; j < allWeatherElementData.length(); j++) {
                test = test + ((JSONObject)allWeatherElementData.get(j)).getString("elementName") + "\n";
                JSONArray allTimeData = ((JSONObject)allWeatherElementData.get(j)).getJSONArray("time");
                Log.v("allTimeData", String.valueOf(allTimeData.length()));

                for(int k = 0; k < allTimeData.length(); k++) {
                    JSONObject jsonObjectSingleData = (JSONObject) allTimeData.get(k);
                    test = test + jsonObjectSingleData.get("startTime") + "\n";
                    JSONObject jsonObjectSingleDataElement = jsonObjectSingleData.getJSONObject("parameter");
                    test = test + jsonObjectSingleDataElement.getString("parameterName") + " " +
                            /*jsonObjectSingleDataElement.getString("parameterUnit") + */"\n";
                }
            }
        }

        weatherDataTxt.setText(test);
    }

    public void setPermission() {
        for(int i = 0; i < permissionCode.length; i++) {
            Log.v("per", "i");
            if(ContextCompat.checkSelfPermission(this,
                    permissionCode[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        permissionCode,
                        24);
                break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("methodName").equals("downloadWeatherJSON")){
            downloadWeatherJSON();
        }
    }
}