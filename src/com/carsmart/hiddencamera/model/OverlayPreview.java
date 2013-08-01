/**
 * Copyright©2006-2012 Beijing Carsmart Technology Co., LTD..
 * 
 * @Title: OverlayPreview.java
 * @Package com.carsmart.demo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhoujunzhou
 * @date 2012-5-30 下午02:41:20
 * @version V1.0
 */
package com.carsmart.hiddencamera.model;


import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @ClassName: OverlayPreview
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhoujunzhou
 * @date 2012-5-30 下午02:41:20
 * @version 1.0
 */
public class OverlayPreview extends SurfaceView implements SurfaceHolder.Callback {
    MyApplication app;
    Context mCtx = null;
    
    
    

    public OverlayPreview(Context paramContext, MyApplication paramDRApp) {
        super(paramContext);
        this.mCtx = paramContext;
        this.app = paramDRApp;
    }

    public void setup() {
        System.out.println("overlay setup");
        SurfaceHolder holder = getHolder();// 取得holder  
        holder.addCallback(this); // holder加入回调接口  
        // setType必须设置，要不出错.  
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {
        this.app.surfHolder = paramSurfaceHolder;
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        System.out.println("overlay surfaceCreated");
        this.app.surfHolder = paramSurfaceHolder;
        this.app.mCamRec.releaseCamera();
        this.app.mCamRec.initializeCamera();
        //if (MediaRecorderActivity.videoButton.isChecked()) {
           this.app.mCamRec.startVideoCapture(2);
            //return;
       // }
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        System.out.println("overlay surfaceDestroyed");
    }
}
