package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Fragment that manages the gallery
 * @author Leonardo Pratesi
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";


    public GalleryFragment() {
        //null constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);


        File imgFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = view.findViewById(R.id.imageView);
            myImage.setImageBitmap(myBitmap);
            Log.e("imageload", "imageloaded");
        } else {
            Log.e("IMAGENOTLOADED", "ERROR");

        }

            return view;
        }

}
