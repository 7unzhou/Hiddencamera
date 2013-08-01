/**   
 *Copyright©2006-2012 Beijing Carsmart Technology Co., LTD..
 *
* @Title: CameraRecoder.java 
* @Package com.carsmart.demo 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhoujunzhou  
* @date 2012-5-29 下午03:39:38 
* @version V1.0   
*/
package com.carsmart.hiddencamera.main;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.carsmart.hiddencamera.model.MyApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

/** 
 * @ClassName: CameraRecoder 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhoujunzhou 
 * @date 2012-5-29 下午03:39:38 
 * @version 1.0 
 */
public class CameraRecoder {
      static final int CAPTURE_MODE_AUTO = 2;
      static final int CAPTURE_MODE_MANUAL = 1;
      public static final int LAST_SEQ = 101010;
      public static final int PHOTO_FILE = 1;
      static final String PHOTO_FOLDER = "Photos";
      static final int RECORDING_STOPPEDBY_ACCEL = 5;
      static final int RECORDING_STOPPEDBY_MAXDURATION = 3;
      static final int RECORDING_STOPPEDBY_NEUTRAL = 0;
      static final int RECORDING_STOPPEDBY_PHOTO = 2;
      static final int RECORDING_STOPPEDBY_RESCUE = 4;
      static final int RECORDING_STOPPEDBY_VIDEO = 1;
      public static final int TEMPVIDEO_FILE = 3;
      static final String TEMPVIDEO_FOLDER = "Tempvideos";
      public static final int VIDEO_FILE = 2;
      static final String VIDEO_FOLDER = "Videos";
      public static String mCurrentPhotoFolder;
      public static String mCurrentTempVideoFolder;
      public static String mCurrentVideoFolder = "";
      MyApplication app;
      public boolean cameraOK = false;
      private Camera.ErrorCallback errorCallback;
      public Camera.AutoFocusCallback focusCallback;
      public Camera mCamera;
      private Camera.Parameters mCameraParams;
      public boolean mCapturePhoto = false;
      Context mContext;
      private String mCurrentPhotoPath;
      private String mCurrentTempVideoPath;
      private String mCurrentVideoPath;
      public String mDefaultVideoTitle = "";
      public String mElevSeq = "";
      Handler mHandler;
      public boolean mInVideo = false;
      public String mLatSeq = "";
      public String mLonSeq = "";
      public MediaRecorder mMediaRecorder;
      public boolean mPhotoFocusStarted = false;
      private String mPhotoRes;
      public String mPhotoResEntriesS = "";
      public String mPhotoResEntryValuesS = "";
      private int mPhotoSetNr;
      private int mPhotoSetSeq = 0;
      public String mSpeedSeq = "";
      private String mVideoFileWithPath;
      private String mVideoFilename;
      private int mVideoGps;
      private int mVideoLength;
      private String mVideoRes;
      public String mVideoResEntriesS = "";
      public String mVideoResEntryValuesS = "";
      private int mVideoSetNr;
      private int mVideoSetSeq = 0;
      private long mVideoStartTime;
      private Camera.PictureCallback pictCallback;
      long previousRecordingId;
      int previousRecordingStop;
      int previousRecordingUpload;
      SharedPreferences settings;
      private MediaRecorder.OnErrorListener videoErrorListener;
      private MediaRecorder.OnInfoListener videoInfoListener;

      static
      {
        mCurrentTempVideoFolder = "";
        mCurrentPhotoFolder = "";
      }

      public CameraRecoder(MyApplication paramMyApplication, Context paramContext, Handler paramHandler)
      {
        this.pictCallback = new Camera.PictureCallback()
        {
            // ERROR //
            public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
            {
    
            }
          };
        this.focusCallback = new Camera.AutoFocusCallback()
        {
            public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
            {
              MyApplication localMyApplication = CameraRecoder.this.app;
              CameraRecoder.this.snapPicture();
            }
          };
          
        this.errorCallback =new Camera.ErrorCallback()
        {
            public void onError(int paramInt, Camera paramCamera)
            {
              String str = "camera onError: " + paramInt;
              System.out.println(str);
              if ((paramInt != 1) && (paramInt != 100))
                return;
              Toast.makeText(CameraRecoder.this.mContext, "camera_err", 1).show();
              CameraRecoder.this.releaseCamera();
              CameraRecoder.this.initializeCamera();
            }
          };
          
        this.videoInfoListener = new MediaRecorder.OnInfoListener()
        {
            public void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
            {
              switch (paramInt1)
              {
              default:
                return;
              case 800:
              }
              CameraRecoder.this.stopVideoRecording(3);
              CameraRecoder.this.restartVideoCapture();
            }
          };
          
        this.videoErrorListener = new MediaRecorder.OnErrorListener()
        {
            public void onError(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
            {
              System.out.println("mediaRecorder onError: ");
      
              CameraRecoder.this.stopVideoRecording(0);
              CameraRecoder.this.releaseMediaRecorder();
              CameraRecoder.this.initializeCamera();
              //voyager.videoButton.setChecked(false);
              if (CameraRecoder.this.app.bckgrMode)
              {
                  System.out.println("on videoError notify");
                CameraRecoder.this.app.notifyVideo(false);
                if (CameraRecoder.this.app.mOverlayService != null)
                    System.out.println("this.app.mOverlayService.videoLightOn");
                  //CameraRecoder.this.app.mOverlayService.videoLightOn(false);
              }
              File localFile = Environment.getDataDirectory();
              StatFs localStatFs1 = new android.os.StatFs(localFile.getPath());
              
             System.out.println("videoErrorListener"); 
              
            }
          };
          
        this.app = paramMyApplication;
        this.mContext = paramContext;
        this.mHandler = paramHandler;
      }




      public void initializeCamera()
      {
        MyApplication localMyApplication1 = this.app;
        //helper.writeDebug("initializeCamera", localMyApplication1);
        if (this.app.surfHolder == null)
        {
          MyApplication localMyApplication2 = this.app;
          System.out.println("surfHolder null");
        }
        int i;
        int j;
       if(this.mCamera == null)
        {
         // i = this.settings.getInt("preview_width", 0);
         // j = this.settings.getInt("preview_height", 0);
          //if ((i == 0) || (j == 0))
         // {
         ///   System.out.println("waiting for surfaceChanged...");
          //  return;
          //}else{
          //    System.out.println("surfHolder OK");
         // }
          System.out.println("opening camera...");
        }
        try{
          
          this.mCamera = Camera.open();
          Camera.ErrorCallback localErrorCallback = this.errorCallback;
          this.mCamera.setErrorCallback(localErrorCallback);
          this.mCameraParams = this.mCamera.getParameters();
          this.mCameraParams.setPictureFormat(ImageFormat.JPEG);
          this.mCamera.unlock();
        //  SharedPreferences localSharedPreferences = this.settings;
         
          //this.mCameraParams.setPreviewSize(848, 480);
         // this.mCameraParams.setPictureSize(1600, 1200);
         // this.mCameraParams.set("jpeg-quality", 90);
          //this.mCamera.setParameters(this.mCameraParams);
          this.cameraOK = true;
          if (this.app.surfHolder != null)
            try
            {
              this.mCamera.setPreviewDisplay(this.app.surfHolder);
              this.mCamera.startPreview();
              return;
            }
            catch (Exception localException1)
            {
              System.out.println("Camera preview error: ");
              //Handler localHandler1 = this.mHandler;
             // MyApplication localMyApplication8 = this.app;
              //helper.showDialogBckgr(localHandler1, 104, localMyApplication8, null);
              releaseCamera();
              return;
            }
        }catch (Exception localException2)
        {
          System.out.println("Camera initialization error: ");
          //helper.showDialogBckgr(localHandler2, 102, localMyApplication10, null);
          releaseCamera();
          this.cameraOK = false;
          return;
        }
        releaseCamera();
      }
      
      
      public boolean initializeVideo()
      {
          mMediaRecorder = new MediaRecorder();// 创建mediarecorder对象
          // 指定源，如果声音源不行，就用麦克作为声音源
          //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
          // 设置录制视频源为Camera(相机)
          mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
          // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
          mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
          // 设置录制的视频编码h263 h264
          mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
          // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
          mMediaRecorder.setVideoSize(176, 144);
          // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
          mMediaRecorder.setVideoFrameRate(20);
          if(this.app.surfHolder!=null){
              System.out.println("this.app.surfHolder is oK");
          }
          Surface localSurface = this.app.surfHolder.getSurface();
          mMediaRecorder.setPreviewDisplay(localSurface);
          
          // 设置视频文件输出的路径
          mMediaRecorder.setOutputFile("/sdcard/love.3gp");
          
          
          try {
              // 准备录制
              mMediaRecorder.prepare();
              //mMediaRecorder.start();
              return true;
          }catch (IllegalStateException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
              return false;
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
              return false;
          }
      }


      public void releaseCamera()
      {
        System.out.println("releaseCamera");
        if (this.mCamera == null)
          return;
        this.mCamera.lock();
        this.mCamera.setPreviewCallback(null);
        this.mCamera.setErrorCallback(null);
        this.mCamera.stopPreview();
        this.mCamera.release();
        this.mCamera = null;
      }

      public void releaseMediaRecorder()
      {
        System.out.println("releaseMediaRecorder");
        if (this.mMediaRecorder == null)
          return;
        this.mMediaRecorder.reset();
        this.mMediaRecorder.release();
        this.mMediaRecorder = null;
      }

      public void rescueVideo(int paramInt)
      {
        String str = "rescueVideo: " + paramInt;
        System.out.println(str);
       // if (!MediaRecorderActivity.videoButton.isChecked())
      //    return;
        stopVideoRecording(paramInt);
        restartVideoCapture();
        //helper.showCenterToast(2131034183);
      }

      public void restartVideoCapture()
      {
        System.out.println("restartVideoCapture");
        releaseMediaRecorder();
        if (this.mCapturePhoto)
        {
          releaseCamera();
          initializeCamera();
         // boolean bool1 = snapFocusedPicture();
          return;
        }
        startVideoCapture(2);
      }

      /*      public boolean snapFocusedPicture()
      {
       MyApplication localMyApplication1 = this.app;
        helper.writeDebug("snapFocusedPicture", localMyApplication1);
        int i;
        if (this.mCamera == null)
          i = 0;
        while (true)
        {
          return i;
          try
          {
            Camera localCamera = this.mCamera;
            Camera.AutoFocusCallback localAutoFocusCallback = this.focusCallback;
            localCamera.autoFocus(localAutoFocusCallback);
            Handler localHandler = this.mHandler;
            Message localMessage = this.mHandler.obtainMessage(1);
            boolean bool = localHandler.sendMessageDelayed(localMessage, 2000L);
            this.mPhotoFocusStarted = true;
            i = 1;
          }
          catch (Exception localException)
          {
            while (true)
            {
              snapPicture();
              StringBuilder localStringBuilder = new StringBuilder("autofocus failed: ");
              String str1 = localException.getMessage();
              String str2 = str1;
              MyApplication localMyApplication2 = this.app;
              helper.writeDebug(str2, localMyApplication2);
            }
          }
        }
      }*/

      // 中断拍照
      public void snapPicture()
      {
        System.out.println("snapPicture");
        if (this.mCamera == null)
          return;
        boolean bool = false;
        try
        {
          this.mPhotoFocusStarted = bool;
          Camera localCamera = this.mCamera;
          Camera.PictureCallback localPictureCallback = this.pictCallback;
          localCamera.takePicture(null, null, localPictureCallback);
          return;
        }
        catch (Exception localException)
        {
          Handler localHandler = this.mHandler;
          //MyApplication localMyApplication2 = this.app;
          //helper.showDialogBckgr(localHandler, 103, localMyApplication2, null);
          releaseCamera();
          initializeCamera();
         // voyager.photoButton.setChecked(false);
          this.mHandler.removeMessages(0);
        }
      }
      
      
      public void stopVideoPhoto()
      {
        System.out.println("stopVideoPhoto");
        stopVideoRecording(0);
        releaseMediaRecorder();
        this.mHandler.removeMessages(2);
        releaseCamera();
        this.mHandler.removeMessages(0);
       // MediaRecorderActivity.videoButton.setChecked(false);
        //voyager.photoButton.setChecked(false);
        //if (!this.app.bckgrMode)
         // return;
        
        this.app.notifyVideo(false);
        if (this.app.mOverlayService == null)
          return;
        //this.app.mOverlayService.videoLightOn(false);
       // this.app.mOverlayService.photoLightOn(false);
      }


/**
 * 
*
* @Title: startVideoCapture 
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @param @param paramInt 参数为1是存放在可视的文件夹下，其他为临时文件夹
* @param @return    设定文件 
* @return boolean    返回类型 
* @throws
 */
      public boolean startVideoCapture(int paramInt)
      {
        MyApplication localMyApplication1 = this.app;
        //如果不是正在录像
        if (!this.mInVideo){
            if (!initializeVideo())
            {
              releaseMediaRecorder();
              initializeCamera();
              //voyager.videoButton.setChecked(false);
              return false;
            }else{
              try
              {
                this.mMediaRecorder.start();
                this.mInVideo = true;
                if (paramInt == 1)
                {
                  Handler localHandler3 = this.mHandler;
                  Message localMessage = this.mHandler.obtainMessage(2);
                  boolean bool5 = localHandler3.sendMessageDelayed(localMessage, 1000L);
                 // helper.showCenterToast(2131034184);
                }
                return true;
              }
              catch (RuntimeException localRuntimeException)
              {
                System.out.println("Could not start media recorder: ");
                String str37 = localRuntimeException.getMessage();
                String str38 = str37;
                //MyApplication localMyApplication4 = this.app;
                //helper.writeDebug(str38, localMyApplication4);
                return false;
              }
            }
        }else{
            System.out.println("is in video");
            return false;
        }
        
      }



      public void stopVideoCapture()
      {
        System.out.println("stopVideoCapture");
        this.mVideoSetSeq = 101010;
        stopVideoRecording(1);
        mCurrentTempVideoFolder = "";
        mCurrentVideoFolder = "";
        initializeCamera();
        //SharedPreferences localSharedPreferences = this.settings;
        //String str = voyager.screenElevPrefDef;
       // if (localSharedPreferences.getString("screen_elev", str).equals("no"))
        //  voyager.elevText.setText("");
        //this.mHandler.removeMessages(2);
        //helper.showCenterToast(2131034185);
      }

/*      public void stopVideoPhoto()
      {
        System.out.println("stopVideoPhoto");
        stopVideoRecording(0);
        releaseMediaRecorder();
        this.mHandler.removeMessages(2);
        releaseCamera();
        this.mHandler.removeMessages(0);
        voyager.videoButton.setChecked(false);
        voyager.photoButton.setChecked(false);
        if (!this.app.bckgrMode)
          return;
        this.app.notifyVideo(false);
        if (this.app.mOverlayService == null)
          return;
        this.app.mOverlayService.videoLightOn(false);
        this.app.mOverlayService.photoLightOn(false);
      }*/

      public void stopVideoRecording(int paramInt)
      {
        System.out.println("stopVideoRecording");
        if (!this.mInVideo)
          return;
        if (this.mMediaRecorder == null)
          return;
        try
        {
          this.mMediaRecorder.stop();
          this.mInVideo = false;
          
          long len =SystemClock.uptimeMillis() - this.mVideoStartTime;
          this.mVideoLength = (int)Math.floor(len / 1000L);
          System.out.println("Video duration: "+this.mVideoLength+"s");
          return;
          
        }catch (RuntimeException localRuntimeException)
        {
            localRuntimeException.printStackTrace();
        }
      }
    
}
