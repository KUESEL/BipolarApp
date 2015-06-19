package kr.ac.korea.embedded.lightapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class TestActivity extends Activity implements SensorEventListener, Runnable, View.OnClickListener {
    SharedPreferences pfSetting;
    SensorManager sensorManager;
    Handler handler = new Handler();
    float[] values;
    int userId;

    Intent service;
    
    Button serveyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        pfSetting = getSharedPreferences("setting", MODE_PRIVATE);
        String service_name = Context.SENSOR_SERVICE;
        sensorManager = (SensorManager)getSystemService(service_name);
        initLightSensor();

        userId = pfSetting.getInt("userId", 0);

        TextView txtUserId = (TextView)findViewById(R.id.id_user);
        txtUserId.setText(Integer.toString(userId));

        TextView rebootable = (TextView)findViewById(R.id.avail_reboot);
        if (!pfSetting.contains("chk_reboot")) {
            pfSetting.edit().putBoolean("chk_reboot", false).apply();
            rebootable.setText("재부팅 필요");
        } else {
            boolean available = pfSetting.getBoolean("chk_reboot", false);
            if (available) {
                rebootable.setText("가능");
            } else {
                rebootable.setText("불가능");
            }
        }

        findViewById(R.id.start).setOnClickListener(this);
        serveyButton = (Button)findViewById(R.id.button_opensurvey);
        
        serveyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fierce-sea-3163-395.herokuapp.com/user/" + userId + "/daily/survey"));
				 startActivity(browserIntent);
			}
		});
    }

    @Override
    public void onResume() {
        super.onResume();
        initLightSensor();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (service != null) {
            stopService(service);
            service = null;
        }
    }

    public void initLightSensor(){
        Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        TextView availLight = (TextView)findViewById(R.id.avail_light);
        if(light == null){
            availLight.setText("불가능");
        } else {
            availLight.setText("가능");
        }

        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        values = event.values;
        handler.post(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void run() {
        if (values == null) return;
        TextView valueLight = (TextView)findViewById(R.id.value_light);
        valueLight.setText(Float.toString(values[0]));
    }

    @Override
    public void onClick(View v) {
        //R.id.start Only
        service = new Intent(this, InitService.class);
        startService(service);
    }
}