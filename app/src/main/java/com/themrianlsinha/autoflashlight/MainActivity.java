package com.themrianlsinha.autoflashlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView iv_status;
    TextView tv_data, tv_status, tv_sensitivity;
    Camera camera;
    Camera.Parameters parameters;
    SeekBar seekBar;
    int progress_value;


    //----------------------------------//
    Sensor sensor;
    SensorManager sensorManager;
    //----------------------------------//


    boolean isflash = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_sensitivity = (TextView) findViewById(R.id.tv_sensitivity);
        iv_status = (ImageView) findViewById(R.id.iv_status);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        //--------------------------------------------------------------------//
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorManager.registerListener(lightListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //--------------------------------------------------------------------//

        setSeekBar();

        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
        {
            camera = Camera.open();
            parameters = camera.getParameters();
            isflash = true;
        }
    }




    public void setSeekBar()
    {

        tv_sensitivity.setText(seekBar.getProgress() + " / " + seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        progress_value = i;
                        tv_sensitivity.setText(i + " / " + seekBar.getMax());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        tv_sensitivity.setText(progress_value + " / " + seekBar.getMax());
                    }
                }
        );
    }




    @Override
    protected void onStop() {
        super.onStop();
        if(camera != null)
        {
            camera.release();
            camera = null;
        }

        //-----------------------------------------------------------//
        sensorManager.unregisterListener(lightListener);
        //-----------------------------------------------------------//
    }

    //-----------------------------------------------------------------------//
    public SensorEventListener lightListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            int val = (int) x;
            String str = String.valueOf(x);
            tv_data.setText(str);

            if(val >= progress_value)
            {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                tv_status.setText("ON");
                iv_status.setImageResource(R.drawable.ic_btn_on);
                camera.setParameters(parameters);
                camera.startPreview();
            }
            else
            {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                tv_status.setText("OFF");
                iv_status.setImageResource(R.drawable.ic_btn_off);
                camera.setParameters(parameters);
                camera.stopPreview();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    //-----------------------------------------------------------------------//
}
