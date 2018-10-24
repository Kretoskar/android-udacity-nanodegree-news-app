package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by admin on 25.03.2018.
 */

public class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();

    public static List<News> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        int length = numberOfNews(jsonResponse);
        List<News> newsList = new ArrayList<News>();
        for (int i = 0; i < length; i++) {
            newsList.add(extractFeatureFromJson(jsonResponse, i));
        }

        return newsList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static News extractFeatureFromJson(String newsJson, int currObject) {
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJson);
            JSONObject resultsObject = baseJsonResponse.getJSONObject("response");
            JSONArray featureArray = resultsObject.getJSONArray("results");
            int featureArrayLength = featureArray.length();

            if (featureArrayLength > 0) {
                JSONObject currentFeature = featureArray.getJSONObject(currObject);

                String title = "";
                if (currentFeature.has("webTitle")) {
                    title = currentFeature.getString("webTitle");
                }

                String section = "";
                if (currentFeature.has("sectionName")) {
                    section = currentFeature.getString("sectionName");
                }

                String url = "";
                if (currentFeature.has("webUrl")) {
                    url = currentFeature.getString("webUrl");
                }

                String author = "";
                if(currentFeature.has("tags")) {
                    JSONArray tags = currentFeature.getJSONArray("tags");
                    if (tags.length() != 0) {
                        JSONObject tagsObject = tags.getJSONObject(0);
                        if (tagsObject.has("webTitle")) {
                            author = tagsObject.getString("webTitle");
                        }
                    }
                }
                String date = "";
                if (currentFeature.has("webPublicationDate")) {
                    date = currentFeature.getString("webPublicationDate");
                }

                return new News(title, section, url, author, date);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return null;
    }

    private static int numberOfNews(String newsJson) {
        if (TextUtils.isEmpty(newsJson)) {
            return 0;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJson);
            JSONObject resultsObject = baseJsonResponse.getJSONObject("response");
            JSONArray featureArray = resultsObject.getJSONArray("results");
            int featureArrayLength = featureArray.length();
            return featureArrayLength;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return 0;
    }
}
