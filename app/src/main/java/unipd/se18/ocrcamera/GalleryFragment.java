package unipd.se18.ocrcamera;

import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment that manages the gallery
 * @author Leonardo Pratesi
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    private Bitmap lastPhoto;



    public GalleryFragment() {
        //null constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);


        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        String pathImage = prefs.getString("filePath", null);
        //String OCRText = prefs.getString("text", null);

        lastPhoto = BitmapFactory.decodeFile(pathImage);
        ImageView imageView =view.findViewById(R.id.imageView);
        imageView.setImageBitmap(lastPhoto);

            return view;
        }

}
