package com.shingeku.root.pcsmaassignment3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class VideoEditorActivity extends Activity {

    private int videoID;
    private Activity myActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor);
        Bundle args = getIntent().getExtras();
        this.videoID = args.getInt("videoID");
        if(this.videoID != -1) {
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
        } else {
            getActionBar().setTitle("Creating a new video");
            ((Button)findViewById(R.id.updateBtn)).setText("Create Video");
        }
    }

    public void updateVideoView(String output) throws JSONException {
        JSONObject videoData = new JSONObject(output);

        EditText name = (EditText) findViewById(R.id.name);
        EditText type = (EditText) findViewById(R.id.type);
        EditText description = (EditText) findViewById(R.id.description);
        EditText duration = (EditText) findViewById(R.id.duration);
        RatingBar rating = (RatingBar) findViewById(R.id.rating);

        name.setText(videoData.getString("name"));
        type.setText(videoData.getString("type"));
        description.setText(videoData.getString("description"));
        duration.setText(videoData.getString("duration"));
        rating.setRating((float) videoData.getDouble("rating"));
        getActionBar().setTitle("Editing - "+videoData.getString("name"));
    }

    public void updateVideo(View view) {

        EditText name = (EditText) findViewById(R.id.name);
        EditText type = (EditText) findViewById(R.id.type);
        EditText description = (EditText) findViewById(R.id.description);
        EditText duration = (EditText) findViewById(R.id.duration);
        RatingBar rating = (RatingBar) findViewById(R.id.rating);

        ArrayList<NameValuePair> videoData = new ArrayList<NameValuePair>();
        videoData.add(new BasicNameValuePair("name", name.getText().toString()));
        videoData.add(new BasicNameValuePair("type", type.getText().toString()));
        videoData.add(new BasicNameValuePair("description", description.getText().toString()));
        videoData.add(new BasicNameValuePair("duration", duration.getText().toString()));
        videoData.add(new BasicNameValuePair("rating", Float.toString(rating.getRating())));

        if(this.videoID != -1) {
            VideoController.putVideo(videoData, this.videoID, new Callback() {

                @Override
                public void onRequestComplete(String output) {
                    /* Go back to Video View */
                    JSONObject outputJSON = null;
                    try {
                        outputJSON = new JSONObject(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String status = null;
                    try {
                        status = outputJSON.getString("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status.equals("FAILED")) {
                        Toast.makeText(getApplicationContext(), "Failed to update video: Invalid Parameters", Toast.LENGTH_LONG).show();
                    }
                    Intent videoActivity = new Intent(myActivity, VideoActivity.class);
                    Bundle args = new Bundle();
                    args.putInt("videoID", videoID);
                    videoActivity.putExtras(args);
                    myActivity.startActivity(videoActivity);
                }
            });
        } else {
            VideoController.postVideo(videoData, new Callback() {
                @Override
                public void onRequestComplete(String output) {
                    /* Go back to Video View */
                    int newVideoID = 0;
                    JSONObject outputJSON = null;
                    try {
                        outputJSON = new JSONObject(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        newVideoID = outputJSON.getInt("videoID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String status = null;
                    try {
                        status = outputJSON.getString("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status.equals("FAILED")) {
                        Toast.makeText(getApplicationContext(), "Failed to create video: Invalid Parameters", Toast.LENGTH_LONG).show();
                        Intent mainActivity = new Intent(myActivity, Main.class);
                        myActivity.startActivity(mainActivity);
                    } else {
                        Intent videoActivity = new Intent(myActivity, VideoActivity.class);
                        Bundle args = new Bundle();
                        args.putInt("videoID", newVideoID);
                        videoActivity.putExtras(args);
                        myActivity.startActivity(videoActivity);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_editor, menu);
        return true;
    }

}
