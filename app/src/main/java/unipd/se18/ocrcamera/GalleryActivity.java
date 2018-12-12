package unipd.se18.ocrcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.common.util.DataUtils;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //Create the container for all my cards
        RecyclerView picturesRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        picturesRecycleView.setHasFixedSize(true);

        //Set a grid (2 columns) from my recycleView
        GridLayoutManager recycleLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        picturesRecycleView.setLayoutManager(recycleLayoutManager);
        picturesRecycleView.setItemAnimator(new DefaultItemAnimator());

        //Load the cards using the RecycleCardsAdapter into the picturesRecycleView
        GalleryManager.RecycleCardsAdapter cardAdapter = new GalleryManager.RecycleCardsAdapter(this, GalleryManager.getImages(this));
        picturesRecycleView.setAdapter(cardAdapter);

    }
}
