package unipd.se18.ocrcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.io.File;
import java.util.ArrayList;

public class GallerySelectionActivity extends AppCompatActivity {
    String dirPath = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_PICTURES+"/camera2";
    private GridView gridView;
    private GridViewAdapter customGridAdapter;
    private String[] imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = (GridView) findViewById(R.id.gridView);
        customGridAdapter = new GridViewAdapter(this, R.layout.activity_gallery_row_grid, getData());
        gridView.setAdapter(customGridAdapter);
    }

    private ArrayList getData() {
        final ArrayList imageItems = new ArrayList();
        File directory = new File(dirPath);
        File[] imageFiles = directory.listFiles();
        for (int i = 0; i < imageFiles.length; i++) {
            String imagePath = imageFiles[i].getAbsolutePath();
            String imageName = imageFiles[i].getName();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFiles[i].getAbsolutePath());
            Bitmap imageThumbnail = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
            imageItems.add(new ImageItem(imageThumbnail, imageName, imagePath));
            bitmap.recycle();
        }

        return imageItems;}



    public class ImageItem {
        private Bitmap image;
        private String title;
        private String path;

        public ImageItem(Bitmap image, String title, String path) {
            super();
            this.image = image;
            this.title = title;
            this.path = path;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPath(){
            return path;
        }

        public void setPath (String path) {
            this.path = path;
        }
    }



    public class GridViewAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList data;

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final ViewHolder holder;
            String image_path;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                //holder.imageTitle = (TextView) row.findViewById(R.id.text);
                holder.setImage((ImageView) row.findViewById(R.id.image));
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.image.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("IMAGE_PATH", holder.getPath());
                    startActivity(intent);
                }
            });

            ImageItem item = (ImageItem) data.get(position);
            holder.setPath(item.getPath());
            holder.image.setImageBitmap(item.getImage());
            return row;
        }

        class ViewHolder {
            String imagePath;
            ImageView image;

            public void setPath(String path){
                this.imagePath = path;
            }

            public String getPath(){
                return imagePath;
            }

            public void setImage(ImageView image){
                this.image = image;
            }

            public ImageView getImage(){
                return image;
            }
        }
    }

    public void imageClick(View view){
        Log.v("GallerySelectActivity", "Clicked an image");
        //Log.v("GallerySelectActivity", image_path);
    }
}
