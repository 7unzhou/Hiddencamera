package com.carsmart.hiddencamera.main;


import com.carsmart.hiddencamera.R;
import com.carsmart.hiddencamera.model.MyApplication;
import com.carsmart.hiddencamera.service.FloatService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    Button btnstart;
    Button btnstop;
    TextView tv;
    MyApplication app;
    Context mContext;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.app = (MyApplication) getApplication();
        mContext = getBaseContext();
        this.app.mCamRec = new CameraRecoder(this.app, mContext, null);
        btnstart = (Button) findViewById(R.id.btnstart);
        btnstart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnstart.setClickable(false);
                
                Intent service = new Intent();
                service.setClass(MainActivity.this, FloatService.class);
                startService(service);
            }
        });

        btnstop = (Button) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnstart.setClickable(true);
                Toast.makeText(mContext, "关闭录像", Toast.LENGTH_LONG).show();
                Intent serviceStop = new Intent();
                serviceStop.setClass(MainActivity.this, FloatService.class);
                stopService(serviceStop);
            }
        });
        tv = (TextView) findViewById(R.id.tv);

        tv.setText("have a try");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("stop", "stop");
        // createView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("restart", "restart");

    }

}
