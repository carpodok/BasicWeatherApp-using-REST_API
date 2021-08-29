package com.alitalhacoban.weatherapp.Classes;

import android.content.Context;
import android.widget.Toast;

import com.alitalhacoban.weatherapp.MainActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_ID = "https://www.metaweather.com/api/location/";

    Context context;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {

        void onError(String message);

        void onResponse(String cityID);
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener) {
        String url = QUERY_FOR_CITY_ID + cityName;


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                String cityID = "";

                try {
                    JSONObject cityInfo = response.getJSONObject(0);

                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                volleyResponseListener.onResponse(cityID);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show();

                volleyResponseListener.onError("That didn't work!");

            }
        });

        //  queue.add(request);

        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    public interface GetCityForecastByNameCallback {

        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }
    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback) {

        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

                getCityForecastByNameCallback.onError("Error (forcast weather by name)");

            }

            @Override
            public void onResponse(String cityID) {

                getCityForecastByID(cityID, new ForecastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });

    }

    public interface ForecastByIDResponse {

        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastByID(String cityID, ForecastByIDResponse forecastByIDResponse) {

        List<WeatherReportModel> reportModels = new ArrayList<>();

        String url = QUERY_FOR_CITY_WEATHER_ID + cityID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray weather_list = response.getJSONArray("consolidated_weather");

                    for (int i = 0; i < weather_list.length(); i++) {
                        WeatherReportModel one_day_weather = new WeatherReportModel();

                        JSONObject firstDayFromApi = (JSONObject) weather_list.get(i);

                        one_day_weather.setWeather_state_name(firstDayFromApi.getString("weather_state_name"));
                        one_day_weather.setMin_temp(firstDayFromApi.getLong("min_temp"));
                        one_day_weather.setMax_temp(firstDayFromApi.getLong("max_temp"));
                        one_day_weather.setThe_temp(firstDayFromApi.getLong("the_temp"));
                        one_day_weather.setApplicable_date(firstDayFromApi.getString("applicable_date"));

                        reportModels.add(one_day_weather);
                    }


                    forecastByIDResponse.onResponse(reportModels);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);

    }
}
