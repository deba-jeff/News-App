package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class NewsAdapter extends ArrayAdapter<News>{

    public NewsAdapter(@NonNull Context context, int resource, @NonNull List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_adapter, parent, false);
        }

        TextView newsHeading = (TextView) listItemView.findViewById(R.id.newsHeading);
        newsHeading.setText(getItem(position).getNewsHeading());

        TextView writer = (TextView) listItemView.findViewById(R.id.writer);
        writer.setText(getItem(position).getWriter());

        TextView newsType = (TextView) listItemView.findViewById(R.id.newsType);
        newsType.setText(getItem(position).getNewsType());

        TextView dateText = (TextView) listItemView.findViewById(R.id.date);
        String presentDate = getItem(position).getDateTime();
        String date = correctDate(presentDate);
        dateText.setText(date);

        TextView timeText = (TextView) listItemView.findViewById(R.id.time);
        String presentTime = getItem(position).getDateTime();
        String time = correctTime(presentTime);
        timeText.setText(time);

        ImageView newsImage = (ImageView) listItemView.findViewById(R.id.newsImage);
        Picasso.with(getContext())
                .load(getItem(position).getImageUrl())
                .placeholder(R.drawable.guardian_logo)
                .error(R.drawable.error)
                .fit()
                .noFade()
                .into(newsImage);

        return listItemView;

    }


    /**
     * Date formatter
     */
    private String correctDate(String input){

        Instant instant;
        String dateOutput = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.parse(input);
            OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("LLL dd, yyyy");
            dateOutput = odt.format(dtf);
        }

        return dateOutput;
    }

    /**
     * Time formatter
     */
    private String correctTime(String input){
        Instant instant;
        String timeOutput = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.parse(input);
            OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
            timeOutput = odt.format(dtf);
        }

        return timeOutput;
    }



}
