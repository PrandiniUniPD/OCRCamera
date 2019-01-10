package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Class that implements the adapter to show the list of BlurObject
 * Leonardo Pratesi - gruppo 1
 * using MODEL-VIEW-CONTROLLER
 */
public class BlurObjectAdapter extends ArrayAdapter<BlurObject> {

    private static class ViewHolder {                       //for better performance I use a ViewHolder
        private TextView itemView;
        private ImageView imageView;
        public int position;                                //counter to save the position of the adapter to keep correct onClick listeners

    }

    /**
     * Constructor
     * @param context , the activity context
     * @param textViewResourceId the layout resource that works as an UI
     * @param items items that needs to be shown in the list
     */
    public BlurObjectAdapter(Context context, int textViewResourceId, ArrayList<BlurObject> items) {
        super(context, textViewResourceId, items);
    }


    /**
     *  Extented method of android.widget.Adapter
     * @param position the item position
     * @param convertView parameter to recycle the view
     * @param parent the ListView of the Gallery
     * @return a view for each item of the list
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            Log.e("erre", "Viewholder Does Not Exist");

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.adapterview
                            , parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);


        } else {
            Log.e("err", "ViewHolderExist");
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(viewHolder);
        BlurObject item = getItem(position);

        /////////Click Listener
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO show images in new Activity with infos
            }
        });


        if (item!= null) {
            Log.e("err", item.toString());
            viewHolder.itemView.setText(item.toString());
            viewHolder.imageView.setImageBitmap(item.getImage());
        }
        viewHolder.position=position;
        return convertView;
    }

    /**
     *  Method to pass the bitmap between activities
     * @param bitmap
     * @return
     */
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }



}
