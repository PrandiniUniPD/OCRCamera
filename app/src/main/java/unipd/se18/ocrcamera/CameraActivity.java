package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camerakit.CameraKitView;

/**
 * The Activity useful for making photos
 */
public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    //Se the default orientation as portrait
    private static DeviceOrientation orientationResult = DeviceOrientation.PORTRAIT;


    /**
     * onCreate method of the Android Activity Lifecycle
     * @param savedInstanceState The Bundle of the last instance state saved
     * @author Romanello Stefano
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraKitView = findViewById(R.id.cameraKitView);

        //Load sensor for understand the orientation of the phone
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(new SensorEventListener() {
            private int mOrientationDeg; //last rotation in degrees
            private int ORIENTATION_UNKNOWN = -1;

            /**
             * Called when there is a new sensor event. Note that "on changed" is somewhat of a misnomer, as this will also be called if we have a new reading from a sensor with the exact same sensor values (but a newer timestamp).
             * Documentation for SensorEvent: https://developer.android.com/reference/android/hardware/SensorEvent
             */
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                float[] values = event.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[0];
                float Y = -values[1];
                float Z = -values[2];
                float magnitude = X*X + Y*Y;
                // Don't trust the angle if the magnitude is small compared to the y value
                if (magnitude * 4 >= Z*Z) {
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float)Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - (int)Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }
                //now we must figure out which orientation based on the degrees
                if (orientation != mOrientationDeg)
                {
                    mOrientationDeg = orientation;

                    View takePicButton = findViewById(R.id.take_photo_button);

                    //figure out actual orientation
                    if(orientation == ORIENTATION_UNKNOWN){//basically flat
                    }
                    else if(orientation <= 45 || orientation > 315){//round to 0
                        //Portrait
                        orientationResult=DeviceOrientation.PORTRAIT;
                        takePicButton.setRotation(0); //rotate take picture button
                    }
                    else if(orientation > 45 && orientation <= 135){//round to 90
                        //LandscapeRight
                        orientationResult=DeviceOrientation.LANDSCAPERIGHT;
                        takePicButton.setRotation(270);
                    }
                    else if(orientation > 135 && orientation <= 225){//round to 180
                        //PortraitUpside
                        orientationResult=DeviceOrientation.PORTRAITUPSIDEDOWN;
                        takePicButton.setRotation(180);
                    }
                    else if(orientation > 225 && orientation <= 315){//round to 270
                        //LandscapeLeft
                        orientationResult=DeviceOrientation.LANDSCAPELEFT;
                        takePicButton.setRotation(90);
                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        FloatingActionButton mButtonTakePhoto = findViewById(R.id.take_photo_button);
        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("text", null);
                edit.putString("imageDataPath", null);
                edit.apply();
                takePhoto();
            }
        });

        //set reference to the BottomNavigationView
        BottomNavigationView bottomNav= findViewById(R.id.bottom_navigation);

        //react to clicks on the items of bottomView
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //set this activity's menu icon as checked
        Menu menu= bottomNav.getMenu();
        MenuItem thisActivityMenuIcon = menu.getItem(2);
        thisActivityMenuIcon.setChecked(true);
    }

    /**
     * Takes a photo, saves it inside internal storage and resets the last extracted text
     *
     * @modify SharedPreferences
     * @author Romanello Stefano - modified by Leonardo Rossi and Balzan Pietro
     */
    private void takePhoto() {
        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, final byte[] photo) {

                Bitmap bitmapImage = BitmapFactory.decodeByteArray(photo, 0, photo.length, null);

                //Image rotation
                switch (orientationResult)
                {
                    case LANDSCAPERIGHT: bitmapImage=rotateImage(bitmapImage,90); break;
                    case LANDSCAPELEFT: bitmapImage=rotateImage(bitmapImage,270); break;
                    case PORTRAITUPSIDEDOWN: bitmapImage=rotateImage(bitmapImage,180); break;
                    default: break; //orientationResult by default is Portrait, if none of the previous cases are triggered i leave the image  as is.
                }

                //Temporary stores the captured photo into a file that will be used from the Camera Result activity
                String filePath= Utils.tempFileImage(CameraActivity.this, bitmapImage,"capturedImage");

                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(getString(R.string.sharedPrefNameForImagePath), filePath.trim());
                edit.putString(getString(R.string.sharedPrefNameProcessedImage), null);
                edit.apply();

                //An intent that will launch the activity that will analyse the photo
                Intent i = new Intent(CameraActivity.this, ResultActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * Rotate the bitmap image of the angle
     * @param source the image
     * @param angle angle of rotation
     * @return Bitmap image rotated
     */
    public static Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * Enum used to manage the orientation of the device
     */
    private enum DeviceOrientation {
        PORTRAIT(0),
        PORTRAITUPSIDEDOWN(1),
        LANDSCAPELEFT(2),
        LANDSCAPERIGHT(3);

        private int value;

        /**
         * Constructor
         * @param value The number assigned to the constant
         */
        DeviceOrientation(int value){
            this.value = value;
        }


        /**
         * Return the mode
         * @return the mode of the constant
         */
        int getValue(){
            return this.value;
        }
    }

    //create a private listener to use in this Activity
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent newActivity;
                    //start an activity depending on what was chosen
                    switch (menuItem.getItemId()){
                        case R.id.nav_allergens:
                            newActivity= new Intent(CameraActivity.this, MainAllergensActivity.class);
                            startActivity(newActivity);
                            finish();
                            break;
                        case R.id.nav_result:
                            newActivity= new Intent(CameraActivity.this, ResultActivity.class);
                            startActivity(newActivity);
                            finish();
                            break;
                        case R.id.nav_picture:
                            //we already are in this activity
                            break;
                        case R.id.nav_gallery:
                            newActivity= new Intent(CameraActivity.this, GalleryActivity.class);
                            startActivity(newActivity);
                            finish();
                            break;
                    }

                    return false;
                }
            };
}