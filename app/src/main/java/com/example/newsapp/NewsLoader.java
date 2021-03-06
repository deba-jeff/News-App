package com.example.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class NewsLoader extends AsyncTaskLoader<List<News>>{

   private String mUrl;

   public NewsLoader(Context context, String url) {
      super(context);
      mUrl = url;
   }


   /**
    *  System calls the "onStartLoading" whenever we start the loader
    */
   @Override
   protected void onStartLoading() {
      forceLoad();
   }


   @Override
   public List<News> loadInBackground() {

      if (mUrl == null) {
         return null;
      }

      // Perform the network request, parse the response, and extract a list of News.
      List<News> news = QueryUtils.fetchNewsData(mUrl);

      return news;
   }


}
