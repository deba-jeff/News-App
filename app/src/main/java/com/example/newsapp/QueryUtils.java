package com.example.newsapp;

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
import static com.example.newsapp.NewsActivity.LOG_TAG;



public class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a QueryUtils object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Return a list of News objects that has been built up from parsing a JSON response String
     */
    public static List<News> extractFeatureFromJson(String newsJSON) {

        // If the JSON String is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }

        List<News> myNewsArrayList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++){
                JSONObject resultsObject = results.getJSONObject(i);

                String newsType = "";
                if (resultsObject.has("sectionName")) {
                    newsType = resultsObject.getString("sectionName");
                }

                String date = "";
                if (resultsObject.has("webPublicationDate")) {
                    date = resultsObject.getString("webPublicationDate");
                }

                String newsHeading = "";
                if (resultsObject.has("webTitle")) {
                    newsHeading = resultsObject.getString("webTitle");
                }

                String newsUrl = "";
                if (resultsObject.has("webUrl")) {
                    newsUrl = resultsObject.getString("webUrl");
                }

                String imageUrl = null;
                JSONObject fields;
                if (resultsObject.has("fields")) {
                    fields = resultsObject.getJSONObject("fields");

                    if (fields.has("thumbnail")) {
                        imageUrl = fields.getString("thumbnail");
                    }
                }

                String writer = "";
                if (resultsObject.has("tags")) {
                    JSONArray tags = resultsObject.getJSONArray("tags");

                    for (int j = 0; j < tags.length(); j++) {
                        JSONObject tagsObject = tags.getJSONObject(j);
                        if (tagsObject.has("webTitle")) {
                            writer = tagsObject.getString("webTitle");
                        }
                    }

                }

                News updatedNews = new News(imageUrl, newsHeading, writer, date, newsType, newsUrl );
                myNewsArrayList.add(updatedNews);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of News
        return myNewsArrayList;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Problem building the URL", exception);
            return null;
        }

        return url;
    }




    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If url is null, return early
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            // Get an Http connection
            urlConnection = (HttpURLConnection) url.openConnection();

            // Specify the request method
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();

                // Convert the InputStream to a JSON String
                jsonResponse = readFromStream(inputStream);

            }
            else {
                Log.e(LOG_TAG, "Error response code:" + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
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



    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {

            // Read bytes and decode them into characters using a UTF-8 charset.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            // Wrap the InputStreamReader around the BufferedReader for efficiency
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Read a line of text
            String line = reader.readLine();
            while (line != null) {
                // Append each line
                output.append(line);
                line = reader.readLine();
            }
        }

        // Convert to String
        return output.toString();
    }



    /**
     * Query the guardian dataset and return a List of News Objects
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create a URL object from the URL String passed
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {

            // Perform HTTP request to the URL and receive a String of JSON response
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return a List of News Objects after parsing the JSON response String
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the List of News Objects
        return news;
    }




}
