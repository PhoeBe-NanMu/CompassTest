package com.example.compasstest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private ImageView compassImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(listener,sensorAccelerometer,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener,sensorMagnetic,SensorManager.SENSOR_DELAY_GAME);
        compassImageView = (ImageView) findViewById(R.id.compass_img);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null ) {
            sensorManager.unregisterListener(listener);
        }
    }

    SensorEventListener listener = new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magneticValues = new float[3];
        float lastRotateDegree = 0f;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values.clone();
            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticValues = sensorEvent.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
            SensorManager.getOrientation(R,values);
            Log.i("info","value[0] is " + Math.toDegrees(values[0]));

            float rotateDegree = - (float) Math.toDegrees(values[0]);

            if (Math.abs(rotateDegree - lastRotateDegree) > 1) {
                RotateAnimation rotateAnimation = new RotateAnimation(lastRotateDegree,rotateDegree,
                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setFillAfter(true);
                compassImageView.startAnimation(rotateAnimation);
                lastRotateDegree = rotateDegree;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}
