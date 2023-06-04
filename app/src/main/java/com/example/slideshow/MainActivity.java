package com.example.slideshow;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class MainActivity  extends AppCompatActivity  {

    private List<Integer> yogaPoses;  // List of yoga pose image resources
    private int currentPoseIndex = 0;  // Index of the currently displayed yoga pose
    private Timer poseSwitchTimer;
    private static final long POSE_SWITCH_DELAY = 5000; // Delay in milliseconds between pose switches
    private static final int MAX_POSE_INDEX = 2; // Maximum index of the available yoga poses

    private Handler poseSwitchHandler;

    TextureView textureView;
    FrameLayout cameraLayout;
    RelativeLayout relativeLayout;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private SurfaceView surfaceView;
    private CameraPreview cameraPreview;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private ImageView yogaPoseImage;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraLayout = findViewById(R.id.cameraLayout);
        textureView = findViewById(R.id.cameraView);



        // Other initialization code

        // Start the curated yoga session
       // startCuratedYogaSession();

        ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeCamera();
        }
        yogaPoseImage = findViewById(R.id.yogaImage);
        yogaPoses = new ArrayList<>();
        yogaPoses.add(R.drawable.pose1);
        yogaPoses.add(R.drawable.pose2);
        yogaPoses.add(R.drawable.pose1);
        //adjustTextureViewSize();

       // yogaPoseImage = findViewById(R.id.yogaPoseImage);
        // Set initial yoga pose image

        // Start the curated yoga session with pose correction
        //startCuratedYogaSession();


    }

    private void initializeCamera() {
        camera = getFrontFacingCamera();
        if (camera != null) {
            cameraPreview = new CameraPreview(this, camera);
            cameraLayout.addView(cameraPreview);

            // Set autofocus mode
            Camera.Parameters parameters = camera.getParameters();
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            camera.setParameters(parameters);
        } else {
            // Handle camera not available
        }
    }



    private Camera getFrontFacingCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    return Camera.open(cameraId);
                } catch (Exception e) {
                    // Handle camera opening exception
                }
            }
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (camera != null) {
            camera.startPreview();
            startPoseSwitching();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            stopPoseSwitching();
        }
    }


    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    private void adjustTextureViewSize() {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode, reduce the height of the TextureView
            int desiredWidth = screenWidth / 50; // Set the desired height as half of the screen height
            textureView.setLayoutParams(new FrameLayout.LayoutParams(desiredWidth,FrameLayout.LayoutParams.MATCH_PARENT ));
        } else {
            // Portrait mode or other orientations, use the default layout params
            textureView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
    }

    // Pose switch with respect to timeframe

    private void startPoseSwitching() {
        poseSwitchTimer = new Timer();
        poseSwitchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchToNextPose();
                    }
                });
            }
        }, 0, 5000); // Switch to next pose every 5 seconds
    }

    private void stopPoseSwitching() {
        if (poseSwitchTimer != null) {
            poseSwitchTimer.cancel();
            poseSwitchTimer = null;
        }
    }

    private void switchToNextPose() {
        currentPoseIndex++;
        if (currentPoseIndex >= yogaPoses.size()) {
            currentPoseIndex = 0;
        }
        int poseImageRes = yogaPoses.get(currentPoseIndex);
        yogaPoseImage.setImageResource(poseImageRes);
    }




    // Other activity lifecycle methods and necessary implementations

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        // Remove any pending callbacks when the activity is destroyed

    }


}