package com.shingeku.root.pcsmaassignment3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 2/12/2015.
 */
public class VideosListAdapter extends BaseAdapter implements View.OnClickListener {

    private JSONArray videoList;
    private Activity myActivity;
    private Resources resources;
    private static LayoutInflater inflater;

    public VideosListAdapter(Activity myActivity, String jsonOutput, Resources resources) throws JSONException {
        videoList = new JSONArray(jsonOutput);
        this.myActivity = myActivity;
        this.resources = resources;
        inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return videoList.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return videoList.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        try {
            return Long.parseLong(((JSONObject) videoList.get(i)).getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VideoListViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.video, null);
            holder = new VideoListViewHolder();
            holder.Name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (VideoListViewHolder) view.getTag();
        }
        if(videoList.length() < 0) {
            holder.Name.setText("No Videos");
        } else {
            JSONObject curItem = null;
            try {
                curItem = (JSONObject) videoList.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert curItem != null;
            try {
                holder.Name.setText(curItem.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            view.setOnClickListener(new OnItemClickListener(i));
        }
        return view;
    }

    @Override
    public void onClick(View view) {

    }

    class VideoListViewHolder {
        public TextView Name;
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        public OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            Intent videoActivity = new Intent(myActivity, VideoActivity.class);
            Bundle args = new Bundle();
            args.putInt("videoID", (int) getItemId(mPosition));
            videoActivity.putExtras(args);
            myActivity.startActivity(videoActivity);
        }
    }
}
