package com.alitalhacoban.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alitalhacoban.weatherapp.Classes.MySingleton;
import com.alitalhacoban.weatherapp.Classes.WeatherDataService;
import com.alitalhacoban.weatherapp.Classes.WeatherReportModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ListView listView;
    Button btn_weatherByName;

    WeatherDataService weatherDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.cityName_editText);
        listView = findViewById(R.id.listView);

        btn_weatherByName = findViewById(R.id.btn_weatherByName);

        weatherDataService = new WeatherDataService(MainActivity.this);

        btn_weatherByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                weatherDataService.getCityForecastByName(editText.getText().toString(), new WeatherDataService.GetCityForecastByNameCallback() {

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        ArrayAdapter<WeatherReportModel> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });
    }
}
