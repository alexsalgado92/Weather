package com.tilt.weather;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunRise;
    private TextView sunSet;
    private TextView updated;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView)findViewById(R.id.cityText);
        iconView = (ImageView)findViewById(R.id.thumbnailIcon);
        temp = (TextView)findViewById(R.id.tempText);
        description = (TextView)findViewById(R.id.cloudText);
        humidity = (TextView)findViewById(R.id.humidText);
        pressure = (TextView)findViewById(R.id.pressureText);
        wind = (TextView)findViewById(R.id.windText);
        sunRise = (TextView)findViewById(R.id.riseText);
        sunSet = (TextView)findViewById(R.id.sunsetText);
        updated = (TextView)findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());

    }

    public void renderWeatherData(String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});
        DownloadImageTask imageTask = new DownloadImageTask();
        imageTask.execute();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... code) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(Utils.ICON_URL + weather.currentCondition.getIcon() + ".png");
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{


        @Override
        protected Weather doInBackground(String... params) {
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            weather = JSONWeatherParser.getWeather(data);

            Log.v("data:" , weather.currentCondition.getDescription());
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {

            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weather.place.getSunRise()));
            String sunsetDate = df.format(new Date(weather.place.getSunSet()));
            String updateDate = df.format(new Date(weather.place.getLastUpdated()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            //Celcius to Fahrenheit Multiply by 9, then divide by 5, then add 32
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature() * 9 / 5 + 32);

            cityName.setText("" + weather.place.getCity() + (", " + weather.place.getCountry()));
            temp.setText("" + tempFormat + "\u00b0" + "F");
            description.setText("Condition: " + weather.currentCondition.getCondition() + " (" + weather.currentCondition.getDescription() + ")");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + "hpa");
            wind.setText("Wind: " +  weather.wind.getSpeed() + "mps");
            sunRise.setText("Sunrise: " + sunriseDate);
            sunSet.setText("Sunset: " + sunsetDate);
            updated.setText("Updated: " + updateDate);
            super.onPostExecute(weather);
        }
    }

    public void showInputDialog(){
        final AlertDialog.Builder cityDialog = new AlertDialog.Builder(this);
        cityDialog.setTitle("Change City");

        final EditText cityText = new EditText(this);
        cityText.setInputType(InputType.TYPE_CLASS_TEXT);
        cityText.setHint("Name of City");
        cityDialog.setView(cityText);
        cityDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityText.getText().toString());

                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);

            }
        });
        cityDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.change_cityID:
                showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }
}
