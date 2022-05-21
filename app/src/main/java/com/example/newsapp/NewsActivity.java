package com.example.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    private TextView mEmptyStateTextView;
    View loading_indicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     *  Replace test with your api key
     */
    private static final String API_KEY = "test";

    public static final String LOG_TAG = NewsActivity.class.getName();
    private NewsAdapter mAdapter;
    private static final int NEWS_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mEmptyStateTextView = (TextView)findViewById(R.id.empty_view);

        // Reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(getLoaderManager().getLoader(NEWS_LOADER_ID)!=null){
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, NewsActivity.this);
        }

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            showProgressBar();
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);
        }
        else {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Big_Dip_O_Ruby);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    getLoaderManager().restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);
                    mEmptyStateTextView.setText("");
                }
                else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

        ListView mainListView = (ListView)findViewById(R.id.mainListView);
        mAdapter = new NewsAdapter(this, 0, new ArrayList<News>());
        mainListView.setAdapter(mAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current News that was clicked
                News currentNews = mAdapter.getItem(position);

                Uri newsUri = null;
                if (currentNews != null) {
                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    newsUri = Uri.parse(currentNews.getNewsUrl());
                }

                // Create a new intent to view the News URI
                Intent newsWebsiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(newsWebsiteIntent);
            }
        });
    }



    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        Uri.Builder builder = new Uri.Builder();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPref.getString("order_by_key", "newest");
        String pageSize = sharedPref.getString("page_size_key", "10");
        String section = sharedPref.getString("section_key", "all sections");

        if (section.equals("all sections")){
            builder.scheme("https")
                    .authority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("show-fields", "thumbnail")
                    .appendQueryParameter("order-by", orderBy)
                    .appendQueryParameter("page-size", pageSize)
                    .appendQueryParameter("api-key", API_KEY )
                    .build();

            //Converts the url to a String
            String finalUrl = builder.toString();
            return new NewsLoader(this, finalUrl);
        }

        // The else statement carry out a search on the guardian api using "section" as a parameter
        else {
            builder.scheme("https")
                    .authority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("show-fields", "thumbnail")
                    .appendQueryParameter("order-by", orderBy)
                    .appendQueryParameter("page-size", pageSize)
                    .appendQueryParameter("section", section)
                    .appendQueryParameter("api-key", API_KEY)
                    .build();

            //Converts the url to a String
            String finalUrl = builder.toString();
            return new NewsLoader(this, finalUrl);
        }

    }



    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

        hideProgressBar();
        mSwipeRefreshLayout.setRefreshing(false);

        // Reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, display an empty String of Text so that the data
        // can be displayed on the ListView without obstruction
        if(networkInfo != null && networkInfo.isConnected()) {
            mEmptyStateTextView.setText("");
        }

        // Otherwise, display No internet connection meaning we where unable to get data from the api
        else if(networkInfo != null && !(networkInfo.isConnected())) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of News, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }


    }


    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clears out any existing data.
        mAdapter.clear();
        mEmptyStateTextView.setText("");
    }

    public void showProgressBar(){
        loading_indicator = findViewById(R.id.loading_indicator);
        loading_indicator.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        loading_indicator = findViewById(R.id.loading_indicator);
        loading_indicator.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu_item){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
