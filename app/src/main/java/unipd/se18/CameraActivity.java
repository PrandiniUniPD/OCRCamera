package unipd.se18;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The Activity useful for making photos
 */
public class CameraActivity extends AppCompatActivity {

    /**
     * TAG used for logs
     */
    private static final String TAG = "CameraActivity";


    /**
     * Code for the permission requested
     */
    private final int MY_CAMERA_REQUEST_CODE = 200;

    /**
     * Gyroscope
     */
    private Sensor accelerometer;
    private Sensor magnetometer;
    private DeviceOrientation deviceOrientation;
    private SensorManager mSensorManager;

    // Shared variables
    /**
     * TextureView used for the camera preview
     */
    private TextureView mCameraTextureView;

    /**
     * The CameraDevice used to interact with the physical camera.
     */
    private CameraDevice mCameraDevice;

    /**
     * CaptureRequest.Builder used for the camera preview.
     */
    private CaptureRequest.Builder mCaptureRequestBuilder;

    /**
     * CameraCaptureSession used for the camera preview;
     */
    private CameraCaptureSession mCameraCaptureSession;

    /**
     * Size used for the camera preview
     */
    private Size mPreviewSize;

    /**
     * ImageReader used for capturing images.
     */
    private ImageReader mImageReader;

    /**
     * Thread used for running tasks in background
     */
    private HandlerThread mBackgroundHandlerThread;

    /**
     * Handler associated to the background Thread
     */
    private Handler mBackgroundHandler;


    /**
     * Semaphore to close the camera before exiting from the app
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Instance of InternalStorageManager for managing the pic data
     */
    public InternalStorageManager bitmapManager;

    private static final SparseIntArray ExifInterfaceORIENTATIONS = new SparseIntArray();
    static {
        ExifInterfaceORIENTATIONS.append(ExifInterface.ORIENTATION_NORMAL, 90);
        ExifInterfaceORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_90, 0);
        ExifInterfaceORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_180, 270);
        ExifInterfaceORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_270, 180);
    }
    private static final SparseIntArray SurfaceORIENTATIONS = new SparseIntArray();
    static {
        SurfaceORIENTATIONS.append(Surface.ROTATION_0, 90);
        SurfaceORIENTATIONS.append(Surface.ROTATION_90, 0);
        SurfaceORIENTATIONS.append(Surface.ROTATION_180, 270);
        SurfaceORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * The id of the camera used
     */
    private String cameraId;


    /**
     * Callback of the camera states
     * author Pietro Prandini (g2)
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.v(TAG, "CameraDevice.StateCallback -> onOpened");
            mCameraOpenCloseLock.release();
            mCameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.v(TAG, "CameraDevice.StateCallback -> onDisconnected");
            mCameraOpenCloseLock.release();
            mCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.v(TAG, "CameraDevice.StateCallback -> onError");
            mCameraOpenCloseLock.release();
            mCameraDevice.close();
            mCameraDevice = null;

            // Put the activity in onDestroy if required
            Activity activity = getParent();
            if (null != activity) {
                // Notify by a toast
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, R.string.state_callback_onError,
                                Toast.LENGTH_LONG).show();
                    }
                });
                // Destroy the activity
                activity.finish();
            }
        }
    };

    /**
     * SurfaceTextureListener used for the preview
     * author Leonardo Rossi (g2)
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.v(TAG, "mSurfaceTextureListener -> onSurfaceTextureAvailable");
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    /**
     * onCreate method of the Android Activity Lifecycle
     * @param savedInstanceState The Bundle of the last instance state saved
     * @modify mButtonTakePhoto The Button used for taking photo
     * @modify mButtonLastPhoto The Button used for viewing the last photo captured
     * @modify mCameraTextureView The TextureView used for the camera preview
     * @author Leonardo Rossi (g2)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //Initialising gyroscope
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        deviceOrientation = new DeviceOrientation();

        bitmapManager = new InternalStorageManager(getApplicationContext(), "OCRPhoto", "lastPhoto");

        // Initializing of the UI components
        mCameraTextureView = findViewById(R.id.camera_view);
        mCameraTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        Button mButtonTakePhoto = findViewById(R.id.take_photo_button);
        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    /**
     * onResume method of the Android Activity lifecycle
     * @modify mCameraTextureView The TextureView used for the camera preview.
     * @author Pietro Prandini (g2), Mattia Molinaro (g1)
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");

        //Restarting gyroscope
        mSensorManager.registerListener(deviceOrientation.getEventListener(),
                accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(deviceOrientation.getEventListener(),
                magnetometer, SensorManager.SENSOR_DELAY_UI);

        startBackgroundThread();
        if (mCameraTextureView.isAvailable()) {
            Log.v(TAG, "onResume -> cameraPreview is available");
            openCamera();
        } else {
            Log.v(TAG, "onResume -> cameraPreview is not available");
            mCameraTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * onPause method of the Android Activity lifecycle
     * @author Pietro Prandini (g2), Mattia Molinaro (g1)
     */
    @Override
    protected void onPause() {
        Log.v(TAG,"onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();

        //Stop gyroscope
        mSensorManager.unregisterListener(deviceOrientation.getEventListener());

    }

    /**
     * Opens a connection with a camera if it is permitted, otherwise return.
     * <p>The method open a connection with the frontal camera if the permission is granted
     * otherwise return</p>
     * @modify Copy the Id of the frontal camera in variable cameraId
     * @modify open the connection with  the frontal camera
     * @modify create a Log.e "connected" if the connection succeed
     * @modify create a Log.e "impossible to open the camera" if the CameraAccessExeption succeed
     * @author Giovanni Furlan (g2)
     */
    private void openCamera() {
        Log.v(TAG, "Open the Camera");

        //Permission handling
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, MY_CAMERA_REQUEST_CODE);
            return;
        }

        //Camera opening process
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics mCameraCharacteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap stremCfgMap = mCameraCharacteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (stremCfgMap != null) {
                mPreviewSize = stremCfgMap.getOutputSizes(SurfaceTexture.class)[0];
            }
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraId, mStateCallback, mBackgroundHandler);
        }
        catch(CameraAccessException e) {
            Log.e(TAG, "impossible to open the camera");
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }

        Log.e(TAG, "connected");
    }

    /**
     * Controls the output of the permissions requests.
     * @param requestCode The code assigned to the request
     * @param permissions The list of the permissions requested
     * @param grantResults The results of the requests
     * @author Pietro Prandini (g2)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Notify by a toast
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this,
                                    R.string.permissions_not_granted, Toast.LENGTH_LONG).show();
                        }
                    });
                    // Destroy the activity
                    finish();
                }
            }
        }

    }


    /**
     * Create the Handler useful for the camera preview
     * @modify mBackgroundHandlerThread The HandlerThread useful for the camera background
     * operations Handler
     * @modify mBackgroundHandler The Handler useful for the camera background operations
     * @author Pietro Prandini (g2), Mattia Molinaro (g1)
     */
    private void startBackgroundThread()
    {
        Log.v(TAG, "Start the background Handler Thread");
        mBackgroundHandlerThread = new HandlerThread("Camera preview");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    /**
     * Stop the Handler useful for the camera preview
     * @modify mBackgroundHandlerThread The HandlerThread useful for the camera background
     * operations Handler
     * @modify mBackgroundHandler The Handler useful for the camera background operations
     * @author Pietro Prandini (g1), Mattia Molinaro (g2)
     */
    private void stopBackgroundThread()
    {
        Log.v(TAG, "Stop the background Handler Thread");
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the preview of the camera capture frames.
     * @modify mCameraDevice The CameraDevice associated to the camera used
     * @modify mCaptureRequestBuilder The CaptureRequest.Builder that build the request for the
     * camera capturing images
     * @modify mCameraCaptureSession The CameraCaptureSession that control the capture session
     * of the camera
     * @author Pietro Prandini (g2), Mattia Molinaro (g1)
     */
    private void createCameraPreview() {
        final String mTAG = "createCameraPreview -> ";
        final String mTAGCaptureSession = mTAG + "createCaptureSession -> ";
        Log.v(TAG, "Start the creation of camera preview");

        try
        {
            // Instance of the Surface tools useful for the camera preview
            SurfaceTexture mSurfaceTexture = null;
            if(mCameraTextureView.isAvailable())
            {
                mSurfaceTexture = mCameraTextureView.getSurfaceTexture();
            }

            // Check if the SurfaceTexture is not null
            if (mSurfaceTexture == null)
            {
                // There is a bug in the SurfaceTexture creation
                Log.v(TAG, mTAG + "The SurfaceTexture is null: there is a bug in the" +
                        " SurfaceTexture creation.");

                // Notify by a toast
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(CameraActivity.this, R.string.surface_texture_bug,
                                Toast.LENGTH_LONG).show();
                    }
                });

                // The app can't continue: launch onDestroy method
                finish();
            }

            // Set the size of the default buffer same as the size of the camera preview
            if (mSurfaceTexture != null) {
                mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                Log.v(TAG, "createCameraPreview -> Bug in mSurfaceTexture: it is null");
                return;
            }


            //Output Surface
            Surface mSurface = new Surface(mSurfaceTexture);

            // Set up the mCaptureRequestBuilder with the output surface mSurface
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(mSurface);

            Log.v(TAG, mTAG + "Creating the camera preview");
            // Create a camera preview
            mCameraDevice.createCaptureSession(Collections.singletonList(mSurface), new CameraCaptureSession.StateCallback() {

                /*
                    This method is called when the camera device has finished configuring itself,
                    and the session can start processing capture requests.
                    @param session The session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null
                    (g1)
                 */
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.v(TAG, mTAGCaptureSession + "onConfigured");
                    // Check: if the camera is closed return
                    if(mCameraDevice == null) {
                        return;
                    }
                    mCameraCaptureSession = session;
                    updatePreview();
                }

                /*
                    This method is called if the session cannot be configured as requested.
                    @param session The session returned by CameraDevice.createCaptureSession(SessionConfiguration).
                           This value must never be null
                      (g1)
                 */


                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.v(TAG, mTAGCaptureSession + "onConfigureFailed");

                    // Notify by a toast
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, R.string.preview_failed,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }, null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Updates the preview of the camera frames.
     * @modify mCameraDevice The CameraDevice associated to the camera used
     * @modify mCaptureRequestBuilder The CaptureRequest.Builder that build the request
     * for the camera capturing images
     * @modify mCameraCaptureSession The CameraCaptureSession that control the capture session
     * of the camera
     * @author Pietro Prandini (g2)
     */
    private void updatePreview() {
        CaptureRequest mCaptureRequest;
        // Check for every external call
        if(mCameraDevice == null)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraActivity.this,R.string.update_preview_failed,
                            Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        // Auto focus should be continuous for camera preview.
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);
        try
        {
            // Start the camera preview
            mCaptureRequest = mCaptureRequestBuilder.build();
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mBackgroundHandler);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Takes a photo, saves it inside internal storage and resets the last extracted text
     *
     * @modify mCameraDevice
     * @modify SharedPreferences
     * @modify Application private storage
     * @author Alberto Valente (g2), Taulant Bullaku (g2) - Modified by Luca Moroldo (g3)
     */
    private void takePhoto() {

        if(mCameraDevice == null) {
            Log.e("cameraDevice", "cameraDevice is null");
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = Objects.requireNonNull(characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP))
                    .getOutputSizes(ImageFormat.JPEG);

            //Set a default size for jpegs
            int width = 640;
            int height = 480;

            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader reader) {

                    //acquire image and convert it to Bitmap
                    Image image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                    //Image rotation
                    int rotation = deviceOrientation.getOrientation();
                    bitmapImage = imageOrientation(bitmapImage, rotation);

                    //save Bitmap inside internal storage and reset extracted text
                    bitmapManager.saveBitmapToInternalStorage(bitmapImage);
                    SharedPreferences sharedPref = getApplicationContext()
                            .getSharedPreferences("extractedText", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lastExtractedText", "");
                    editor.apply();

                    Intent i = new Intent(CameraActivity.this, ResultActivity.class);
                    startActivity(i);

                }
            };

            mImageReader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            //@author Leonardo Rossi

            //Output surfaces
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(mImageReader.getSurface());
            outputSurface.add(new Surface(mCameraTextureView.getSurfaceTexture()));

            //Creation of a capture request to take a photo from the camera
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);


            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            mCameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession)
                {
                }
            },mBackgroundHandler);
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }
    } //end takePhoto

    //TODO test this method
    /**
     * Rotate bitmap image relatively the rotation of the smartphone
     * @param bmp original bitmap image
     * @param rotation based on DeviceOrientation class // TODO check if unusued
     * @return Bitmap rotated image
     * @author Giovanni Furlan (g2), Pietro Prandini (g2)
     */
    private Bitmap imageOrientation(Bitmap bmp, int rotation){
        Log.v(TAG, "imageOrientation -> rotation == " + rotation);
        CameraManager internalCameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = 0;
        try {
            sensorOrientation = internalCameraManager
                    .getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // ExifInterfaceOrientations
        int rotationCompensation = ExifInterfaceORIENTATIONS.get(rotation);
        // SurfaceOrientations
        //int rotationCompensation = SurfaceORIENTATIONS.get(CameraActivity.this.getWindowManager().getDefaultDisplay().getRotation());
        Log.v(TAG, "imageOrientation -> sensorOrientation == " + sensorOrientation + ", rotationCompensation == "
                + rotationCompensation);
        rotation = (sensorOrientation + rotationCompensation + 270) % 360;
                Log.v(TAG, "imageOrientation -> calculated rotation == " + rotation);
        // ExifInterfaceOrientations
        switch (rotation) {
            case 0:
                return rotateImage(bmp, 90);
            case 90:
                return rotateImage(bmp, 0);
            case 180:
                return rotateImage(bmp, 270);
            case 270:
                return rotateImage(bmp, 180);
        }
        // SurfaceOrientations
        /*switch (rotation) {
            case 0:
                return rotateImage(bmp, 0);
            case 90:
                return rotateImage(bmp, 90);
            case 180:
                return rotateImage(bmp, 180);
            case 270:
                return rotateImage(bmp, 270);
        }*/
        return bmp;
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
     * Closes resources related to the camera.
     * @modify mCameraDevice The CameraDevice associated to the camera used
     * @modify mCameraCaptureSession The CameraCaptureSession that control the capture session of the camera
     * @modify mImageReader The ImageReader useful for storing the photo captured
     * @author Pietro Prandini (g2)
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameraCaptureSession) {
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        }
        finally {
            mCameraOpenCloseLock.release();
        }
    }
}
