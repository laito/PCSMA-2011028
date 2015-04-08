package com.assignment4.apoorvsingh.assignment4_client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Main extends Activity {

    private static String cookie = null;
    private static SpringAPI springAPI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cookie = sharedPref.getString("cookie", null);
        if(cookie == null) {
            // Open the login activity
            Intent loginActivity = new Intent(this, LoginActivity.class);
            this.startActivity(loginActivity);
        } else {
            // Open Home Page activity
            Intent homeActivity = new Intent(this, HomeActivity.class);
            this.startActivity(homeActivity);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public static SpringAPI getAPI() {
        if(springAPI == null) {
            springAPI = SpringService.getService();
        }
        return springAPI;
    }
}