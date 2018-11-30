package unipd.se18.ocrcamera;

import android.app.Activity;
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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.camerakit.CameraKitView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * The Activity useful for making photos
 */
public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private static String orientationResult;

    private final String CROPPED_IMAGE_PATH = "/data/user/0/unipd.se18.ocrcamera/cache/AFile.jpg";

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
            public int mOrientationDeg; //last rotation in degrees
            private static final int _DATA_X = 0;
            private static final int _DATA_Y = 1;
            private static final int _DATA_Z = 2;
            private int ORIENTATION_UNKNOWN = -1;

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                float[] values = event.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
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
                    if(orientation == -1){//basically flat
                    }
                    else if(orientation <= 45 || orientation > 315){//round to 0
                        Log.d("Sensor", "P"); //Portrait
                        orientationResult="P";
                        takePicButton.setRotation(0); //rotate take picture button
                    }
                    else if(orientation > 45 && orientation <= 135){//round to 90
                        Log.d("Sensor", "LR"); //LandscapeRight
                        orientationResult="LR";
                        takePicButton.setRotation(270);
                    }
                    else if(orientation > 135 && orientation <= 225){//round to 180
                        Log.d("Sensor", "PU"); //PortraitUpside
                        orientationResult="PU";
                        takePicButton.setRotation(180);
                    }
                    else if(orientation > 225 && orientation <= 315){//round to 270
                        Log.d("Sensor", "LL"); //LandscapeLeft
                        orientationResult="LL";
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
    }

    /**
     * Takes a photo, saves it inside internal storage and resets the last extracted text
     * @modify SharedPreferences
     * @author Romanello Stefano - modified by Leonardo Rossi
     */
    private void takePhoto()
    {
        //Calling camera kit capture photo method.
        //It has as parameters the view that has captured the image and
        //an array of bytes representing the image data
        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, final byte[] photo) {

                //First of all the byte array is converted into bitmap for an easier manipulation
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(photo, 0, photo.length, null);

                //If the app has detected the capture's image orientation we go ahead with the code
                if(orientationResult != null)
                {
                    //Based on the detected image's rotation, it is rotated so that it will always be portrait
                    switch (orientationResult)
                    {
                        case "LR": bitmapImage=rotateImage(bitmapImage,90); break;
                        case "LL": bitmapImage=rotateImage(bitmapImage,270); break;
                        case "PU": bitmapImage=rotateImage(bitmapImage,180); break;
                        default: break;
                    }

                    //Temporary stores the captured photo into a file that will be used by the Camera Result activity
                    //So that the captured image can be passed with an intent
                    String filePath= tempFileImage(CameraActivity.this, bitmapImage,"capturedImage");

                    //At this point of code we will give the user the ability to crop or manually
                    //rotate the image so that the ingredients portion can be easily detected by OCR.
                    //To do this the UCrop library is used and in particular it works with Uris so,
                    //first of all we have to convert our paths into Uri objects

                    //First the path where the cropped image will be saved is defined and converted into Uri
                    final Uri resultImageUri = Uri.fromFile(new File(CROPPED_IMAGE_PATH));
                    //Second the captured image's path is converted into Uri object
                    Uri.Builder builder = new Uri.Builder().scheme("file").path(filePath);
                    final Uri captureImageUri = builder.build();

                    //Now the UCrop library is ready to be used but first some crop options are defined
                    UCrop.Options options = new UCrop.Options();
                    options.setHideBottomControls(false); //Crop buttons are used to manipulate the photo
                    options.setFreeStyleCropEnabled(true); //The crop rectangle's size can be modified like user wants
                    //Some color options are defined
                    options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
                    options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                    options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                    //The crop activity title is defined
                    options.setToolbarTitle("Focus on the ingredients");
                    //The crop activity is finally called
                    UCrop.of(captureImageUri, resultImageUri)
                            .withOptions(options)
                            .start(CameraActivity.this);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //If the user has successfully used UCrop activity we enter into the if
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP)
        {
            //The cropped image's Uri is caught from the UCrop activity
            final Uri resultUri = UCrop.getOutput(data);

            //The cropped image's path is stored into shared preferences so that it can be
            //reused by the app and in particular by result activity to invoke on it the OCR
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("imagePath", resultUri.getPath());
            edit.apply();

            //At this point the crop process is finished and the app can show the user the result activity
            Intent i = new Intent(CameraActivity.this, ResultActivity.class);
            startActivity(i);

        }
        else if (resultCode == UCrop.RESULT_ERROR)
        {
            //If an error happens after cropping the image this exception is thrown
            final Throwable cropError = UCrop.getError(data);
        }
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
     * Stores the captured image into a temporary file useful to pass large data between activities
     * and returns the file's path.
     * @param context The reference of the current activity
     * @param bitmap The captured image to store into the file. Not null or empty.
     * @param name The name of the file. Not null or empty.
     * @return The file's path
     * @author Leonardo Rossi
     */
    private String tempFileImage(Context context, Bitmap bitmap, String name)
    {
        //Creating an instance of the file where the image will be saved
        //It will be stored at the path contained into the variable outputDir
        //It name will be the composition of the name variable's value and .jpg
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        try
        {
            //Enabling the previous file to writing
            OutputStream os = new FileOutputStream(imageFile);
            //Before storing the image it is compressed in JPEG format and then written into the file's output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            //This method ensures that all the bytes in the output stream will be written into the specified file
            os.flush();
            //At the end the output stream is closed
            os.close();
        }
        catch (Exception e)
        {
            //If an error happens while writing data into a file, then an exceprion is thrown and
            //some logs are created
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        //If all has gone well, the file's path is returned
        return imageFile.getAbsolutePath();
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






}


