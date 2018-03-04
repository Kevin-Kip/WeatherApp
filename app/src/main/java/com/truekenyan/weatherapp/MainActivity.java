package com.truekenyan.weatherapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextClock textClock;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String city = "Nairobi";
    //TODO get an api key from api.openweathermap.org
    private final String APP_ID =  "YOUR API KEY HERE";
    private String URL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID="+APP_ID;
    private TextView cityName;
    private TextView temperature;
    private ImageView icon;
    private String DEGREE_CELSIUS = "\u2103";
    private TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext().getSharedPreferences("clock_format", MODE_PRIVATE);

        textClock = findViewById(R.id.clock);
        cityName = findViewById(R.id.city_name);
        temperature = findViewById(R.id.temp);
        icon = findViewById(R.id.cloud);
        ImageView temp_icon = findViewById(R.id.temp_icon);
        desc = findViewById(R.id.status);
        //converting the black image to white
        temp_icon.setColorFilter(Color.WHITE);
        icon.setColorFilter(Color.WHITE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (connectionAvailable()){
            getWeather();
        } else {
            Toast.makeText(getApplicationContext(), "No Network Connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLatLang(){

    }

    private void getWeather(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray weatherData = response.getJSONArray("weather");
                    JSONObject weather = weatherData.getJSONObject(0);
                    String description = weather.getString("description");
                    desc.setText(Character.toUpperCase(description.charAt(0)));

                    setIcon(icon, description);

                    String town = response.getString("name");

                    JSONObject sys = response.getJSONObject("sys");
                    String country = sys.getString("country");
                    String fullCity = town + " ," + country;
                    cityName.setText(fullCity);

                    JSONObject main = response.getJSONObject("main");
                    String temp = main.getString("temp");
                    String celsius = String.valueOf(toCelsius(Float.parseFloat(temp)));
                    temperature.setText(String.format("%s %s",celsius,DEGREE_CELSIUS));
                } catch (JSONException e){
                    Toast.makeText(getApplicationContext(), "Oops! Unable to fetch data.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Oops! Unable to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(objectRequest);
    }

    private float toCelsius(float i) {
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        return Float.parseFloat(decimalFormat.format(i - 273.15F));
    }

    private void setIcon(ImageView imageView, String description) {
        switch (description) {
            case "clear sky":
                imageView.setImageResource(R.drawable.clear_sky);
                break;
            case "few clouds":
                imageView.setImageResource(R.drawable.few_clouds);
                break;
            case "scattered clouds":
                imageView.setImageResource(R.drawable.scattered_clouds);
                break;
            case "broken clouds":
                imageView.setImageResource(R.drawable.broken_clouds);
                break;
            case "shower rain":
                imageView.setImageResource(R.drawable.shower_rain);
                break;
            case "rain":
                imageView.setImageResource(R.drawable.rain);
                break;
            case "thunderstorm":
                imageView.setImageResource(R.drawable.thunderstorm);
                break;
            case "snow":
                imageView.setImageResource(R.drawable.snow);
                break;
            case "mist":
                imageView.setImageResource(R.drawable.mist);
                break;
        }
    }

    private boolean connectionAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo.isConnected();
    }
}
