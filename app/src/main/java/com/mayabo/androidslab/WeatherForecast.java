package com.mayabo.androidslab;

import androidx.appcompat.app.AppCompatActivity;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute();

    }


    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        String min;
        String max;
        String uv;
        String currentTemp;
        Bitmap weatherPicture;

        @Override
        protected String doInBackground(String... strings) {
            String ret = null;
            String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
            String uvRatingURL = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";

            try {
                URL url = new URL(weatherURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(inStream, "UTF-8");

                int EVENT_TYPE;
                while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) {
                    switch (EVENT_TYPE) {
                        case START_TAG:
                            String tagName = xpp.getName();
                            if (tagName.equals("temperature")) {
                                currentTemp = xpp.getAttributeValue(null, "value");
                                publishProgress(25);
                                min = xpp.getAttributeValue(null, "min");
                                publishProgress(50);
                                max = xpp.getAttributeValue(null, "max");
                                publishProgress(75);
                            }
                            if (tagName.equals("weather")) {
                                String iconName = xpp.getAttributeValue(null, "icon");
                                weatherPicture = getImage(iconName);
                                publishProgress(100);
                            }
                            break;
                        case END_TAG:
                            break;
                        case TEXT:
                            break;
                    }
                    xpp.next(); // move the pointer to next XML element
                }

            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            } catch (XmlPullParserException pe) {
                ret = "XML Pull exception. The XML is not properly formed";
            }


            try {
                URL url = new URL(uvRatingURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jObject = new JSONObject(result);
                double value = jObject.getDouble("value");
                uv = String.valueOf(value);

            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            } catch (JSONException e) {
                ret = "JSON Exception";
            }
            return ret;
        }

        @Override                       //Type 2
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);

        }

        @Override                   //Type 3
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);

            TextView temp = findViewById(R.id.currentTemp);
            TextView maxTemp = findViewById(R.id.maxTemp);
            TextView minTemp = findViewById(R.id.minTemp);
            TextView uvRating = findViewById(R.id.uvRating);
            ImageView image = findViewById(R.id.weatherPicture);

            temp.setText(temp.getText().toString() + " " + currentTemp);
            maxTemp.setText(maxTemp.getText().toString() + " " + max);
            minTemp.setText(minTemp.getText().toString() + " " + min);
            uvRating.setText(uvRating.getText().toString() + " " + uv);
            image.setImageBitmap(weatherPicture);

            progressBar.setVisibility(View.INVISIBLE);

        }

        private Bitmap downloadImage(String iconName) {
            Bitmap image = null;
            String urlString = "http://openweathermap.org/img/w/" + iconName + ".png";

            Log.i("Download: ", "Download Image start!");
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());
                    saveImage(iconName, image);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return image;
        }

        private void saveImage(String iconName, Bitmap image) {
            try {
                FileOutputStream outputStream = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.i("Download: ", "Download Image finished!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean fileExistance(String imagefile) {
            File file = getBaseContext().getFileStreamPath(imagefile);
            return file.exists();
        }

        private Bitmap getImage(String iconName) {
            Bitmap image;
            String imagefile = iconName + ".png";
            Log.i("Image name: ", iconName);
            Log.i("Image file: ", imagefile);

            if (fileExistance(imagefile)) {
                Log.i("Does image file exits: ", "file exists");
                FileInputStream fis = null;
                try {
                    fis = openFileInput(imagefile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                image = BitmapFactory.decodeStream(fis);
            } else {
                Log.i("Does image file exits: ", "file does not exists");
                image = downloadImage(iconName);
            }
            return image;
        }
    }
}


