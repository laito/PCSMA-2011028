package com.shingeku.root.pcsmaassignment1;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/*
 * A simple android app to log accelerometer data to a file.
 *
 * Author: Apoorv Singh
 */
public class Main_2011028 extends Activity implements SensorEventListener {

    static boolean logAccelerometer;
    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logAccelerometer = false;
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void startAccelerometer(View view) {
        CharSequence text = "Starting Accelerometer";
        showToast(text);
        logAccelerometer = true;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopAccelerometer(View view) {
        CharSequence text = "Stopping Accelerometer";
        showToast(text);
        logAccelerometer = false;
        mSensorManager.unregisterListener(this);
        TextView accelText = (TextView) findViewById(R.id.accelText);
        accelText.setText("");
    }

    public void onSensorChanged(SensorEvent event){
        if (logAccelerometer) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                Float x = event.values[0];
                Float y = event.values[1];
                Float z = event.values[2];
                SimpleDateFormat curTime = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
                String timeRightNow = curTime.format(new Date());
                appendToAccelerometerLog(timeRightNow+","+x.toString() + "," + y.toString() + "," + z.toString()+"\n");
                TextView accelText = (TextView) findViewById(R.id.accelText);
                accelText.setText("x: "+x.toString() + "\ny: " + y.toString() + "\nz: " + z.toString());
                Log.d("accelero", x.toString() + " " + y.toString() + " " + z.toString());
            }
        }
    }

    /* Delete the log before starting application */
    public void deleteAccelerometerLog() {
        File accelerometerLog = new File("sdcard/data.csv");
        accelerometerLog.delete();
    }

    /* Append the data to accelerometer log */
    public void appendToAccelerometerLog(String text) {
        File accelerometerLog = new File("sdcard/data.csv");
        if(!accelerometerLog.exists()) {
            try {
                accelerometerLog.createNewFile();
                appendToAccelerometerLog("Time,x,y,z\n");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter logFileWriter = new FileWriter(accelerometerLog, true);
            BufferedWriter bufferedLogFileWriter = new BufferedWriter(logFileWriter);
            bufferedLogFileWriter.append(text);
            bufferedLogFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}