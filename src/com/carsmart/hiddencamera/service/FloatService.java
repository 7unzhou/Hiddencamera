package com.carsmart.hiddencamera.service;


import java.util.Timer;

import com.carsmart.hiddencamera.model.MyApplication;
import com.carsmart.hiddencamera.model.OverlayPreview;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class FloatService extends Service {
    MyApplication app;
    private OverlayPreview bckgrOverlay;
    LinearLayout overlayGroup;
    WindowManager wm = null;
    WindowManager.LayoutParams wmParams = null;
    View view;
    private SurfaceView surfaceview;// 显示视频的控件
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    int state;
    TextView tx1;
    TextView tx;
    ImageView iv;
    private float StartX;
    private float StartY;
    int delaytime = 1000;

    @Override
    public void onCreate() {
        Log.d("FloatService", "onCreate");
        super.onCreate();
        app = (MyApplication) getApplication();
        
        this.bckgrOverlay = new OverlayPreview(this, (MyApplication) getApplication());

        createView();
        handler.postDelayed(task, delaytime);
    } 

    private void createView() {

        if (wm == null) {
            wm = (WindowManager) getApplicationContext().getSystemService("window");
        }

        /*FloatService.this.overlayGroup = new LinearLayout(this);
        FloatService.this.overlayGroup.setLayoutParams(new WindowManager.LayoutParams(-1, -1));
        FloatService.this.overlayGroup.setOrientation(1);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.bckgr_btn_small, null);
        // FloatService.this.overlayGroup.addView(layout);

        View view1 = new View(this);
        view1.setBackgroundColor(-7829368);
        LinearLayout.LayoutParams lyParams = new LayoutParams(1, 1);
        view1.setLayoutParams(lyParams);
        FloatService.this.overlayGroup.addView(view1);

        // if ((FloatService.this.videoOn) && (localBoolean2.booleanValue()))
        // FloatService.this.rescueBtn = layout;
        TextView tv = (TextView) layout.findViewById(R.id.btn_label);
        tv.setText("video");

        View sview = layout.findViewById(R.id.btn_light);
        sview.setBackgroundColor(0);
        FloatService.this.overlayGroup.addView(layout);

        if (wm != null) {
            WindowManager.LayoutParams Params8 = new WindowManager.LayoutParams(-1, -1, 50, 50, 2003, 40, -1);
            Params8.gravity = Gravity.LEFT | Gravity.TOP;
            System.out.println("add view overlayGroup");
            wm.addView(overlayGroup, Params8);
        }*/

        wmParams = new WindowManager.LayoutParams(1, 1, 2003, 40, -1);
        // wmParams.type = 2003;
        // wmParams.flags |= 40;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //wmParams.x = 0;
        //wmParams.y = 0;
       // wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // wmParams.format = 1;
         
        System.out.println("add view bckgrOverlay");
        wm.addView(bckgrOverlay, wmParams); 
        bckgrOverlay.setup();

    } 

    public void showImg() {
        if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5 && !iv.isShown()) {
            iv.setVisibility(View.VISIBLE);
        } else if (iv.isShown()) {
            iv.setVisibility(View.GONE);
        }
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            // dataRefresh();
            // handler.postDelayed(this, delaytime);
            // wm.updateViewLayout(view, wmParams);
        }
    };

    public void dataRefresh() {
        
    }

    private void updateViewPosition() {
        // ���¸�������λ�ò���
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        // wm.updateViewLayout(view, wmParams);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("FloatService", "onStart");
        setForeground(true);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(task);
        Log.d("FloatService", "onDestroy");
        //appmCamRec.stopVideoCapture();
        wm.removeView(bckgrOverlay);
        this.app.mCamRec.stopVideoCapture();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
