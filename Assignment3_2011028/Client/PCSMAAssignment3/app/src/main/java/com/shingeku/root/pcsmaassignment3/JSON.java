package com.shingeku.root.pcsmaassignment3;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/12/2015.
 */
public class JSON {

    public static String request(URLContext URL) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String type = URL.getType();
        String url = URL.getURL();
        HttpResponse response = null;
        InputStream responseStream;

        if(type.equals("GET")) {
            response = httpclient.execute(new HttpGet(url));
        } else if(type.equals("POST")) {
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(URL.getData()));
            response = httpclient.execute(httppost);
        } else if(type.equals("PUT")) {
            HttpPut httpput = new HttpPut(url);
            httpput.setEntity(new UrlEncodedFormEntity(URL.getData()));
            response = httpclient.execute(httpput);
        } else if (type.equals("DELETE")) {
            HttpDelete httpdelete = new HttpDelete(url);
            response = httpclient.execute(httpdelete);
        }
        responseStream = response.getEntity().getContent();
        if(responseStream != null) {
            Log.d("Request", type + " : " + url + " " + response.toString());
            Log.d("Request", type + " : " + url + " " + response.toString());
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(responseStream));
            StringBuilder output = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                output.append(line);
            }
            return output.toString();
        } else {
            return "[]";
        }
    }
}