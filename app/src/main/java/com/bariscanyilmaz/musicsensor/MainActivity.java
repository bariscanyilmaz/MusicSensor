package com.bariscanyilmaz.musicsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {

    SensorManager sensorManager;
    Sensor light;
    Sensor acceleration;


    private final String sensorBroadcast="com.bariscanyilmaz.musicsensor.MUSIC_SENSOR";

    private final float lightTreshold=100f;
    private final float accelerationTreshold=0.1f;

    private boolean isMoving=false;
    private boolean isInside=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager=(SensorManager)getSystemService(Service.SENSOR_SERVICE);
        light=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        acceleration=sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);


    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        boolean isLightSensorChanged=false;
        boolean isAccSensorChanged=false;

        if (sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            float value=sensorEvent.values[0];

            boolean inside=false;
            inside=value<lightTreshold;

            isLightSensorChanged=(inside!=isInside);
            isInside=inside;

        }


        if (sensorEvent.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            Boolean moving=false;
            float xAxis=sensorEvent.values[0];
            float yAxis=sensorEvent.values[0];
            float zAxis=sensorEvent.values[0];

            moving=(Math.abs(xAxis)>accelerationTreshold || Math.abs(yAxis)>accelerationTreshold || Math.abs(zAxis)>accelerationTreshold);
            isAccSensorChanged=(moving!=isMoving);
            isMoving=moving;

        }

        if(isAccSensorChanged || isLightSensorChanged){

            Intent intent=new Intent();
            //intent.setClass(getApplicationContext(),)
            intent.setAction(sensorBroadcast);

            intent.putExtra("isMoving",isMoving);
            intent.putExtra("isInside",isInside);

            Log.v("isMoving",isMoving+"");
            Log.v("isInside",isInside+"");

            sendBroadcast(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}