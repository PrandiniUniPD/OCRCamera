package unipd.se18.ocrcamera;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

/**
 * The Activity useful for making photos
 */
public class CameraActivity extends AppCompatActivity {

    // Final shared variables
    /**
     * TAG used for logs
     */
    private static final String TAG = "CameraActivity";

    /**
     * Code for the permission requested
     */
    private final int MY_CAMERA_REQUEST_CODE = 200;

    /**
     * SparseIntArray used for the conversion from screen rotation to Bitmap orientation
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    // Shared variables
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
     * Callback of the camera states
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    /**
     * SurfaceTextureListener used for the preview
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initializing of the UI components
        TextureView mCameraTextureView = findViewById(R.id.camera_view);
        Button mButtonTakePhoto = findViewById(R.id.take_photo_button);
        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Opens a connection with a camera if it is permitted, otherwise return.
     */
    private void openCamera() {

    }

    /**
     * Controls the output of the permissions requests.
     * @param requestCode The code assigned to the request
     * @param permissions The list of the permissions requested
     * @param grantResults The results of the requests
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

    }

    /**
     * Creates the preview of the camera capture frames.
     */
    private void createCameraPreview() {

    }

    /**
     * Updates the preview of the camera frames.
     */
    private void updatePreview() {

    }

    /**
     * Takes a photo.
     */
    private void takePhoto() {

    }

    /**
     * Saves a photo previously taken.
     */
    private void savePhoto() {

    }

    /**
     * Closes resources related to the camera.
     */
    private void closeCamera() {

    }
}
