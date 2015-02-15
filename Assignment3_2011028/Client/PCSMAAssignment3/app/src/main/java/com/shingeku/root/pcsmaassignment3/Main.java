package com.shingeku.root.pcsmaassignment3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONException;

import java.util.HashMap;


public class Main extends Activity {

    protected static String MovieServiceURL = "http://192.168.53.188/Assignment3_2011028/";
    protected static HashMap<Integer, String> videos = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoController.getVideos(new Callback() {
            @Override
            public void onRequestComplete(String output) {
                /* Callback to update the listview */
                updateListView(output);
            }
        });
        getActionBar().setTitle("My Videos");
    }


    public void updateListView(String output) {
        ListView videosList = (ListView) findViewById(R.id.videosListView);
        VideosListAdapter adapter = null;
        try {
            adapter = new VideosListAdapter(this, output, getResources());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        videosList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openCreate() {
        Intent videoEditorActivity = new Intent(this, VideoEditorActivity.class);
        Bundle args = new Bundle();
        args.putInt("videoID", -1); /* -1 means Create a new Video */
        videoEditorActivity.putExtras(args);
        this.startActivity(videoEditorActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_create:
                openCreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
