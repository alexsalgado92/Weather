package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.Place;
import model.Weather;

/**
 * Created by student on 10/4/2016.
 */

public class JSONWeatherParser {
    public static Weather getWeather(String data){

        Weather weather = new Weather();

        try {
            JSONObject jsonObject = new JSONObject(data);

            Place place = new Place();

            JSONObject coordObj = Utils.getObject("coord", jsonObject);

            place.setLat(Utils.getFloat("lat", coordObj));
            place.setLon(Utils.getFloat("lon", coordObj));

            //get the sys obj
            JSONObject sysObj = Utils.getObject("sys", jsonObject);
            place.setCountry(Utils.getString("country", sysObj));
            place.setLastUpdated(Utils.getInt("dt", jsonObject));
            place.setSunRise(Utils.getInt("sunrise", sysObj));
            place.setSunSet(Utils.getInt("sunset", sysObj));
            place.setCity(Utils.getString("name", jsonObject));

            weather.place = place;

            //Get weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherID(Utils.getInt("id", jsonWeather));
            weather.currentCondition.setDescription(Utils.getString("description", jsonWeather));
            weather.currentCondition.setCondition(Utils.getString("main", jsonWeather));
            weather.currentCondition.setIcon(Utils.getString("icon", jsonWeather));

            JSONObject windObj = Utils.getObject("wind", jsonObject);
            weather.wind.setDeg(Utils.getFloat("deg", windObj));
            weather.wind.setSpeed(Utils.getFloat("speed", windObj));

            JSONObject cloudObj = Utils.getObject("clouds", jsonObject);
            weather.cloud.setPercipatation(Utils.getInt("all", cloudObj));

            JSONObject mainObj = Utils.getObject("main" , jsonObject);
            weather.currentCondition.setTemperature(Utils.getDouble("temp", mainObj));
            weather.currentCondition.setPressure(Utils.getFloat("pressure", mainObj));
            weather.currentCondition.setHumidity(Utils.getFloat("temp_min", mainObj));
            weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max", mainObj));

            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
