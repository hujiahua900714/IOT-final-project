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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {

    ArrayList<weatherdata>cityweather = new ArrayList<weatherdata>();

    Button mainBtn, dbBtn, payBtn, logoutBtn;
    Spinner spinner;

    String[] permissionCode = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE };

    TextView label;
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
        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        label = (TextView) findViewById(R.id.label);
        weatherDataTxt = (TextView) findViewById(R.id.weatherDataTxt);

        label.setText("天氣預報");
        //Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.city_array,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(2, false);
        AdapterView.OnItemSelectedListener spnOnItemSelected
                = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String sPos=String.valueOf(pos);
                String text = "";
                String sInfo=parent.getItemAtPosition(pos).toString();
                for(weatherdata i : cityweather){
                    if(i.getCity().equals(sInfo)){
                        text += i.getCity() + "\n";
                        for(int j = 0;j < 3; j++){
                            text += i.getvalue("time",j) + "\n" + i.getvalue("weather",j) + "\n"
                                    + "最高溫: " + i.getvalue("htemp",j) + "度\n" + "最低溫: " +i.getvalue("ltemp",j) + "度\n";
                        }
                        weatherDataTxt.setText(text);
                        break;
                    }
                }
                //String sInfo=parent.getSelectedItem().toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        };
        spinner.setOnItemSelectedListener(spnOnItemSelected);

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
        int count = 0;
        String cityname = "",elementname = "",weather = "",time = "",htemp = "",ltemp = "";
        JSONObject jsonObject = new JSONObject(weatherData);
        String allData = jsonObject.getString("records");
        JSONObject jsonObjectAllLocationData = new JSONObject(allData);
        JSONArray allLocationData = jsonObjectAllLocationData.getJSONArray("location");
        Log.v("json array", String.valueOf(allLocationData.length()));

        String test = new String();

        for(int i = 0; i < allLocationData.length(); i++) {
            JSONObject jsonObjectLocationData = (JSONObject) allLocationData.get(i);
            test = test + jsonObjectLocationData.getString("locationName") + "\n";
            cityname = jsonObjectLocationData.getString("locationName");

            JSONArray allWeatherElementData = jsonObjectLocationData.getJSONArray("weatherElement");
            Log.v("allWeatherElementData", String.valueOf(allWeatherElementData.length()));

            for(int j = 0; j < allWeatherElementData.length(); j++) {
                test = test + ((JSONObject)allWeatherElementData.get(j)).getString("elementName") + "\n";
                elementname = ((JSONObject)allWeatherElementData.get(j)).getString("elementName");
                JSONArray allTimeData = ((JSONObject)allWeatherElementData.get(j)).getJSONArray("time");
                Log.v("allTimeData", String.valueOf(allTimeData.length()));
                count = 0;
                for(int k = 0; k < allTimeData.length(); k++) {
                    JSONObject jsonObjectSingleData = (JSONObject) allTimeData.get(k);
                    test = test + jsonObjectSingleData.get("startTime") + "\n";
                    if(count == 0){
                        time += (jsonObjectSingleData.getString("startTime")) + "|";
                    }
                    JSONObject jsonObjectSingleDataElement = jsonObjectSingleData.getJSONObject("parameter");
                    test = test + jsonObjectSingleDataElement.getString("parameterName") + " " +
                            /*jsonObjectSingleDataElement.getString("parameterUnit") + */"\n";
                    if(elementname.equals("Wx")){
                        weather += (jsonObjectSingleDataElement.getString("parameterName")) + "|";
                    }else if(elementname.equals("MinT")){
                        ltemp += (jsonObjectSingleDataElement.getString("parameterName")) + "|";
                    }else if(elementname.equals("MaxT")){
                        htemp += (jsonObjectSingleDataElement.getString("parameterName")) + "|";
                    }
                }
                count++;
            }
            cityweather.add(new weatherdata(cityname,weather,time,htemp,ltemp));
            Log.v("city name", cityname);
            Log.v("i-th", String.valueOf(i));
            Log.v("size",String.valueOf(cityweather.size()));
            /*for(int j = 0; j < cityweather.size(); j++) {
                Log.v("j-th",String.valueOf(j));
                Log.v("city",cityweather.get(j).getCity());
                Log.v("Htemp", cityweather.get(j).getHtemp().toString());
            }*/
            weather = "";
            time = "";
            htemp = "";
            ltemp = "";
        }

        //weatherDataTxt.setText(test);
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