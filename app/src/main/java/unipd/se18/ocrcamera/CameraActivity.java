/*
 *  The main goal for this project is to test older Camera API (15 to 19) to see if it's
 *  plausible to support these devices and lower the minimum API requirement on CommonDemo
 *  so we can support full range of Android devices (API 15 and higher is 100% demographic)
 *  I used this library that interfaces with the Camera (1) API to make the program
 *      https://github.com/natario1/CameraView
 *  working with master build of the library
 *  The documentation starts here
 *      https://github.com/natario1/CameraView#usage
 */

package com.example.elia.compatibilitycamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.widget.ImageButton;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

/**
 * Activity for camera preview and taking photos
 */
public class CameraActivity extends AppCompatActivity {

    //variable used to interface with the CameraView library
    private CameraView camera;

    //variable used to connect to the capturePhoto button
    private ImageButton buttonPhoto;

    /**
     * onCreate method of the Android Activity Lifecycle
     * @param savedInstanceState the bundle of the last instance state saved
     * @author Bedin Elia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera = findViewById(R.id.viewCamera);

        //library method for automatic onResume, onPause, onDestroy
        camera.setLifecycleOwner(this);

        //Pinch to zoom support
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        //Tap to focus support
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);
        //Long tap to capture support (currently disabled, should be .CAPTURE)
        camera.mapGesture(Gesture.LONG_TAP, GestureAction.NONE);

        camera.addCameraListener(new CameraListener() {
            /**
             * Listening for photo taken by the camera
             * @param picture the taken photo as an array of byte (a stream of data)
             * @author Bedin Elia
             */
            public void onPictureTaken(byte[] picture) { onCapture(picture); }
        });

        //connecting the variable to the button capturePhoto
        buttonPhoto = findViewById(R.id.capturePhoto);
        //enabling the button
        buttonPhoto.setEnabled(true);

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            /**
             * Listening for button click, then call capturePhoto() to take a photo
             * @param view the layout components
             * @author Bedin Elia
             */
            @Override
            public void onClick(View view) {
                capturePhoto();
            }
        });
    }

    /**
     * Method for taking a photo
     * @author Bedin Elia
     */
    private void capturePhoto() {
        //checking if the app is ready to capture a photo (if the button is enabled)
        if (!buttonPhoto.isEnabled()) return;
        //if the button was enabled, set it to disabled and capture a photo
        buttonPhoto.setEnabled(false);
        camera.capturePicture();
    }

    /**
     * Method for sending the taken photo as an array of byte to the PicturePreviewActivity
     * @param picture the taken photo as an array of byte (a stream of data)
     * @author Bedin Elia
     */
    private void onCapture(byte[] picture) {
        //releasing the button, the app is ready to capture a photo again
        buttonPhoto.setEnabled(true);
        //sending the taken photo to PicturePreviewActivity via setImage
        PicturePreviewActivity.setImage(picture);
        //sending information to PicturePreviewActivity through Intent
        Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
        startActivity(intent);
    }

    /**
     * Explicit permission checker for Android M or higher
     * @param requestCode permission code
     * @param permissions the requested permissions
     * @param grantResults the grant results for the corresponding permissions
     * @author Bedin Elia
     */
    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }
}
