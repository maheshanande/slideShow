package com.example.slideshow;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.logging.LogRecord;

public class MainActivity  extends AppCompatActivity  {
    private static final long POSE_SWITCH_DELAY = 5000; // Delay in milliseconds between pose switches
    private static final int MAX_POSE_INDEX = 2; // Maximum index of the available yoga poses

    private Handler poseSwitchHandler;
    private int currentPoseIndex;
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

        currentPoseIndex = 0;

        // Other initialization code

        // Start the curated yoga session
        startCuratedYogaSession();

        ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeCamera();
        }
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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
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

    private void startCuratedYogaSession() {
        // Schedule the initial pose switch after the delay
        poseSwitchHandler.postDelayed(poseSwitchRunnable, POSE_SWITCH_DELAY);
    }

    private Runnable poseSwitchRunnable = new Runnable() {
        @Override
        public void run() {
            // Switch to the next pose
            switchToNextPose();

            // Schedule the next pose switch after the delay
            poseSwitchHandler.postDelayed(this, POSE_SWITCH_DELAY);
        }
    };

    private void switchToNextPose() {
        // Increment the current pose index
        currentPoseIndex++;
        if (currentPoseIndex > MAX_POSE_INDEX) {
            currentPoseIndex = 0; // Reset to the first pose if the index exceeds the maximum
        }

        // Display the next pose
        showPoseImage(currentPoseIndex);
    }

    private void showPoseImage(int poseIndex) {
        // Get the ImageView reference for the yoga pose image
        ImageView yogaPoseImage = findViewById(R.id.yogaPoseImage);

        // Set the appropriate image resource based on the pose index
        switch (poseIndex) {
            case 0:
                yogaPoseImage.setImageResource(R.drawable.pose1);
                break;
            case 1:
                yogaPoseImage.setImageResource(R.drawable.pose2);
                break;
            case 2:
                yogaPoseImage.setImageResource(R.drawable.pose1);
                break;
            // Add more cases for additional pose indices
        }

        // Make the yoga pose image visible
        yogaPoseImage.setVisibility(View.VISIBLE);
    }

}