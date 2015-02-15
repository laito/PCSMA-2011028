package com.shingeku.root.pcsmaassignment3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class VideoActivity extends Activity {

    private int videoID;
    private Activity myActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Bundle args = getIntent().getExtras();
        this.videoID = args.getInt("videoID");
        VideoController.getVideo(videoID, new Callback() {
            @Override
            public void onRequestComplete(String output) {
                /* Callback to update the listview */
                try {
                    updateVideoView(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getActionBar().setTitle("My Videos");
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void updateVideoView(String output) throws JSONException {
        JSONObject videoData = new JSONObject(output);

        TextView name = (TextView) findViewById(R.id.name);
        TextView type = (TextView) findViewById(R.id.type);
        TextView description = (TextView) findViewById(R.id.description);
        TextView duration = (TextView) findViewById(R.id.duration);
        RatingBar rating = (RatingBar) findViewById(R.id.rating);

        name.setText(videoData.getString("name"));
        type.setText(videoData.getString("type"));
        description.setText(videoData.getString("description"));
        duration.setText(videoData.getString("duration"));
        rating.setRating((float) videoData.getDouble("rating"));

        rating.setVisibility(View.VISIBLE);
        findViewById(R.id.editBtn).setVisibility(View.VISIBLE);
        findViewById(R.id.deleteBtn).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        getActionBar().setTitle(videoData.getString("name"));
    }

    public void  deleteVideo(View view) {
        VideoController.deleteVideo(videoID, new Callback() {
            @Override
            public void onRequestComplete(String output) {
                /* Go to main activity */
                Intent mainActivity = new Intent(myActivity, Main.class);
                myActivity.startActivity(mainActivity);
            }
        });
    }

    public void editVideo(View view) {
        Intent videoEditorActivity = new Intent(this, VideoEditorActivity.class);
        Bundle args = new Bundle();
        args.putInt("videoID", this.videoID);
        videoEditorActivity.putExtras(args);
        this.startActivity(videoEditorActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainActivity = new Intent(this, Main.class);
                this.startActivity(mainActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
