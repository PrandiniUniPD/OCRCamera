package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> bitmapList;

    public ImageAdapter(Context c, ArrayList<Bitmap> bitmapList) {
        this.context = c;
        this.bitmapList= bitmapList;
    }

    // returns the number of images
    public int getCount() {
        return bitmapList.size();
    }

    // returns the ID of an item
    public Object getItem(int position) {
        return position;
    }

    // returns the ID of an item
    public long getItemId(int position) {
        return position;
    }

    // returns an ImageView view
    public View getView(int position, View convertView, ViewGroup parent) {

        // crea ImageView automaticamente
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    ,115));
            imageView.setScaleType((ImageView.ScaleType.CENTER_CROP));
        }
        else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(this.bitmapList.get(position));
        return imageView;
    }
}
