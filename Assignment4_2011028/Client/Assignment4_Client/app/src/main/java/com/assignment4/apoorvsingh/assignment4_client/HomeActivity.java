package com.assignment4.apoorvsingh.assignment4_client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class HomeActivity extends Activity {

    private Activity myActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        refreshList();
    }

    private void refreshList() {
        Main.getAPI().resources(new Callback<List<Resource>>() {
            @Override
            public void success(List<Resource> resources, Response response) {
                updateListView(resources);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void updateListView(List<Resource> resources) {
        ListView resourcesList = (ListView) findViewById(R.id.resourcesListView);
        ResourcesListAdapter adapter = new ResourcesListAdapter(this, resources, getResources());
        resourcesList.setAdapter(adapter);
    }

    private void openCreate() {
        /* Upload PDF */
        // This always works
        Intent i = new Intent(myActivity, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        startActivityForResult(i, 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            uploadFile(uri);
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            uploadFile(uri);
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                uploadFile(uri);
            }
        }
    }

    private void uploadFile(Uri uri) {
        String name = uri.getLastPathSegment();
        TypedFile resource = new TypedFile("image/*", new File(uri.getPath()));
        Main.getAPI().upload(name, resource, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                refreshList();
            }

            @Override
            public void failure(RetrofitError error) {
                refreshList();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void downloadResource(View view) {
        String resourceID = String.valueOf(view.getTag());
        Log.d("IDDDD", String.valueOf(view.getTag()));
        Main.getAPI().resource(Integer.parseInt(resourceID), new Callback<String>() {

            @Override
            public void success(String s, Response response) {
                Log.d("IDDDD", "WOO");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("IDDDD", "WOOHOOO");
                Response response = error.getResponse();
                try {
                    byte[] bytes = getBytesFromStream(response.getBody().in());
                    FileOutputStream fileOuputStream = new FileOutputStream("download.pdf");
                    fileOuputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static byte[] getBytesFromStream(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
        }
        buf = bos.toByteArray();

        return buf;
    }


    public void deleteResource(View view) {
        String resourceID = String.valueOf(view.getTag());
        Main.getAPI().delete(Integer.parseInt(resourceID), new Callback<String>() {

            @Override
            public void success(String s, Response response) {
                Log.d("IDDDD", "DELWOO");
                refreshList();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("IDDDD", "DELWOOHOOO");
                refreshList();
            }
        });
    }

}
