package com.coolweather.android.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author xiaoq
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        @SuppressLint("WrongConstant")
        SharedPreferences prefs = getSharedPreferences("weather_info", Context.MODE_PRIVATE);
        String weatherId = prefs.getString("weather_id", null);
        if (weatherId != null) {
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                    + "&key=171fa5674e574912bc8bda8b188ec687";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    assert response.body() != null;
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = getSharedPreferences
                                    ("weather_info", Context.MODE_PRIVATE).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                        }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                assert response.body() != null;
                String bingPic = response.body().string();
                @SuppressLint("CommitPrefEdits")
                SharedPreferences.Editor editor = getSharedPreferences
                        ("weather_info", Context.MODE_PRIVATE).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }
}
