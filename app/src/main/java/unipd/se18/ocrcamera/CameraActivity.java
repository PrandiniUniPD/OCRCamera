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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.camerakit.CameraKitView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * The Activity useful for making photos
 */
public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private static String orientationResult;

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
        },

                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

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
                Toast.makeText(getBaseContext(),"ok",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Takes a photo, saves it inside internal storage and resets the last extracted text
     *
     * @modify SharedPreferences
     * @author Romanello Stefano - modified by Leonardo Rossi
     */
    private void takePhoto() {
        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, final byte[] photo) {

                Bitmap bitmapImage = BitmapFactory.decodeByteArray(photo, 0, photo.length, null);
                boolean control = blurRecognition(bitmapImage);
                Toast.makeText(getBaseContext(), "applicato blur control",
                        Toast.LENGTH_LONG).show();

                //Image rotation
                if(orientationResult != null)
                {
                    switch (orientationResult)
                    {
                        case "LR": bitmapImage=rotateImage(bitmapImage,90); break;
                        case "LL": bitmapImage=rotateImage(bitmapImage,270); break;
                        case "PU": bitmapImage=rotateImage(bitmapImage,180); break;
                        default: break;
                    }

                    //Temporary stores the captured photo into a file that will be used from the Camera Result activity
                    String filePath= tempFileImage(CameraActivity.this, bitmapImage,"capturedImage");

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("imagePath", filePath.trim());
                    edit.apply();

                    //An intent that will launch the activity that will analyse the photo
                    Intent i = new Intent(CameraActivity.this, ResultActivity.class);
                    startActivity(i);

                }
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
     * Stores the captured image into a temporary file useful to pass large data between activities
     * and returns the file's path.
     * @param context The reference of the current activity
     * @param bitmap The captured image to store into the file. Not null or empty.
     * @param name The name of the file. Not null or empty.
     * @return The files path
     * @author Leonardo Rossi
     */
    private String tempFileImage(Context context, Bitmap bitmap, String name)
    {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

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

    public boolean blurRecognition(Bitmap bitmap1)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = bitmap1;
        int l = CvType.CV_8UC1; //8-bit grey scale image
        Mat matImage = new Mat();
        org.opencv.android.Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        org.opencv.android.Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, l);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, l);

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight()); // bmp为轮廓图

        int maxLap = -16777216; // 16m
        for (int pixel : pixels) {
            if (pixel > maxLap)
                maxLap = pixel;
            return false;
        }

        int soglia = -6118750;
        if (maxLap <= soglia) {
            System.out.println("is blur image");
            return true;
        }
        soglia += 6118750;
        maxLap += 6118750;
        // LogUtil.log("图片位置=" + picFilePath
        //         + "\nimage.w=" + image.getWidth() + ", image.h=" + image.getHeight()
        //         + "\nmaxLap= " + maxLap + "(清晰范围:0~6118750)"
        //         + "\n" + Html.fromHtml("<font color='#eb5151'><b>" + (maxLap <= soglia ? "模糊" : "清晰") + "</b></font>"));
        // opencvEnd = true;
        //isBlur = maxLap <= soglia;
        return true;

    }




}


