package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * @author xiaoq
 */
public class Weather {

    public String status;

    public Basic basic;

    public Now now;

    public Aqi aqi;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> dailyForecast;

}
