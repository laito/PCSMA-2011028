package com.assignment4.apoorvsingh.assignment4_client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.io.IOUtils;
import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class LoginActivity extends Activity {

    private Activity myActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        LoginRequest loginData = new LoginRequest(username, password);
        SpringAPI api = Main.getAPI();

        api.login(loginData, new Callback<String>() {

            @Override
            public void success(String body, Response response) {
                String cookie = getCookieString(response);
                saveCookie(cookie);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Response response = retrofitError.getResponse();
                String cookie = getCookieString(response);
                saveCookie(cookie);
            }
        });
    }

    private void saveCookie(String cookie) {
        SpringService.setCookies(cookie);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cookie = sharedPref.getString("cookie", null);
        startHomeActivity();
    }

    private void startHomeActivity() {
        Intent homeActivity = new Intent(myActivity, HomeActivity.class);
        myActivity.startActivity(homeActivity);
    }

    /**
     * Method extracts cookie string from headers
     * @param response with headers
     * @return cookie string if present or null
     */
    private String getCookieString(Response response) {
        for (Header header : response.getHeaders()) {
            if (null!= header.getName() && header.getName().equals("Set-Cookie")) {
                return header.getValue();
            }
        }
        return null;
    }
}
