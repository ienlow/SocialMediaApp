package com.socialmediaapp.ui.login;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socialmediaapp.R;

import java.util.List;

public class HomeScreenAdapter extends ArrayAdapter {
    List<NewsFeed> newsFeedList;
    Context context;

    public HomeScreenAdapter(@NonNull Context context, int resource, List<NewsFeed> textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.newsFeedList = textViewResourceId;
        this.context = context;
    }

    public class Holder {
        ImageView icon;
        TextView date;
        TextView newsFeedText;
        TextView likesNumber;
        TextView commentsNumber;
        TextView resharesNumber;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.home_screen_adapter, null);
            holder = new Holder();
            holder.newsFeedText = convertView.findViewById(R.id.news_feed_text);
            holder.date = convertView.findViewById(R.id.news_feed_date);
            holder.likesNumber = convertView.findViewById(R.id.likes_number);
            holder.commentsNumber = convertView.findViewById(R.id.comments_number);
            holder.resharesNumber = convertView.findViewById(R.id.reshares_number);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (holder != null) {
            holder.newsFeedText.setText(newsFeedList.get(position).getText());
            holder.likesNumber.setText(String.valueOf(newsFeedList.get(position).getNumLikes()));
            holder.commentsNumber.setText(String.valueOf(newsFeedList.get(position).getNumComments()));
            holder.resharesNumber.setText(String.valueOf(newsFeedList.get(position).getNumReshares()));
            holder.date.setText(newsFeedList.get(position).getDate());
        }
        return convertView;
    }
}
