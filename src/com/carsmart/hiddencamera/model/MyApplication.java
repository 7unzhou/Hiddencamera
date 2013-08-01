package com.carsmart.hiddencamera.model;



import com.carsmart.hiddencamera.R;
import com.carsmart.hiddencamera.main.CameraRecoder;
import com.carsmart.hiddencamera.main.MainActivity;
import com.carsmart.hiddencamera.service.FloatService;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class MyApplication extends Application {

    private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
	public WindowManager.LayoutParams getMywmParams(){
		return wmParams;
	}
	
	
	
	
	public static final String INTENT_ACTION_MAIN = "android.intent.action.MAIN";
    public static final String INTENT_ACTION_RESCUE_VIDEO = "com.dailyroads.intent.action.RESCUE_VIDEO";
    public static final String INTENT_ACTION_START_PHOTO = "com.dailyroads.intent.action.START_PHOTO";
    public static final String INTENT_ACTION_START_VIDEO = "com.dailyroads.intent.action.START_VIDEO";
    public static final String INTENT_ACTION_STOP_PHOTO = "com.dailyroads.intent.action.STOP_PHOTO";
    public static final String INTENT_ACTION_STOP_VIDEO = "com.dailyroads.intent.action.STOP_VIDEO";
    public boolean bckgrMode = false;
    public CameraRecoder mCamRec;
    public NotificationManager mNotificationManager;
    public FloatService mOverlayService;
    public Build phoneBuild;
    public Build.VERSION phoneBuildVersion;
    public SurfaceHolder surfHolder = null;

    
    public void notifyVideo(boolean paramBoolean)
    {
        //判断video是处于打开还是关闭
      CharSequence localCharSequence1;
      //当打开时
      if (paramBoolean){
          localCharSequence1 = "notif_video_on";
      }else{
          localCharSequence1 = "notif_video_off";
      }

        CharSequence localCharSequence2 = "app_name";
        long l = System.currentTimeMillis();
        Notification localNotification = new Notification(R.drawable.icon, localCharSequence1, l);
        Intent localIntent = new Intent(this, MainActivity.class);
        PendingIntent localPendingIntent = PendingIntent.getActivity(this, 0, localIntent, 0);
        localNotification.setLatestEventInfo(this, localCharSequence2, localCharSequence1, localPendingIntent);
        int j = localNotification.flags | 0x2;
        localNotification.flags = j;
        int k = localNotification.flags | 0x20;
        localNotification.flags = k;
        if (this.mNotificationManager == null)
          return;
        this.mNotificationManager.notify(0, localNotification);
        return;
    }

    public void onCreate()
    {
      super.onCreate();
     // this.contentRes = localContentResolver;
      NotificationManager localNotificationManager = (NotificationManager)getSystemService("notification");
      this.mNotificationManager = localNotificationManager;
    }


}
