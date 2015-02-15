package com.shingeku.root.pcsmaassignment3;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by root on 2/12/2015.
 */
public class AsyncHTTP extends AsyncTask<URLContext, Integer, String> {

    ArrayList<String> output = new ArrayList<String>();
    Callback callback;

    public AsyncHTTP(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(URLContext... urls) {
        int count = urls.length;
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < count; i++) {
            try {
                output.append(JSON.request(urls[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return output.toString();
    }

    protected void onPostExecute(String output) {
        callback.onRequestComplete(output);
    }
}
