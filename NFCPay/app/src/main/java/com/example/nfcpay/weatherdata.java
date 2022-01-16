package com.example.nfcpay;

import java.io.*;
import java.util.ArrayList;

public class weatherdata {
    private String city = "";
    private String weather = "";
    private String time = "";
    private String htemp = "";
    private String ltemp = "";
    public weatherdata(String city,String weather,String time,String htemp,String ltemp){
        this.city = city;
        this.time = time;
        this.weather = weather;
        this.htemp = htemp;
        this.ltemp = ltemp;
    }

    public String getCity() {
        return city;
    }

    public String getWeather() {
        return weather;
    }

    public String getTime() {
        return time;
    }

    public String getHtemp() {
        return htemp;
    }

    public String getLtemp() {
        return ltemp;
    }
    public String getvalue(String type,int position){
        String value = "";
        int counter = 0;
        if(type.equals("weather")){
            for(int i = 0;i < weather.length(); i++){
                if(counter == position && weather.charAt(i) != '|'){
                    value += weather.charAt(i);
                }
                if(weather.charAt(i) == '|'){
                    counter++;
                }
            }
        }else if(type.equals("time")){
            for(int i = 0;i < time.length(); i++){
                if(counter == position && time.charAt(i) != '|'){
                    value += time.charAt(i);
                }
                if(time.charAt(i) == '|'){
                    counter++;
                }
            }
        }else if(type.equals("htemp")){
            for(int i = 0;i < htemp.length(); i++){
                if(counter == position && htemp.charAt(i) != '|'){
                    value += htemp.charAt(i);
                }
                if(htemp.charAt(i) == '|'){
                    counter++;
                }
            }
        }else if(type.equals("ltemp")){
            for(int i = 0;i < ltemp.length(); i++){
                if(counter == position && ltemp.charAt(i) != '|'){
                    value += ltemp.charAt(i);
                }
                if(ltemp.charAt(i) == '|'){
                    counter++;
                }
            }
        }
        return value;
    }
}
