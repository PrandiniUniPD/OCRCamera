package unipd.se18.ocrcamera;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.test.InstrumentationRegistry;
import android.support.test.internal.runner.InstrumentationConnection;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class GalleryTest {

    @Test
    public void getDeletedPhotoPosition() throws IOException {
        Context ContextTest = InstrumentationRegistry.getContext();
        ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages();
        GalleryManager.RecycleCardsAdapter cardAdapter = new GalleryManager.RecycleCardsAdapter(ContextTest, photos);

        GalleryManager.PhotoStructure photoToDelete = photos.get(2);
        GalleryManager.deleteImage(photoToDelete);

        assertEquals(cardAdapter.getDeletedPhotoPosition(), 2);
    }

    @Test
    public void cropImage() {
        int w = 200, h = 200;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates an empty bitmap

        Bitmap newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,100, 100);

        assertEquals(newBmp.getHeight(), 100);
        assertEquals(newBmp.getWidth(), 100);
    }

    @Test
    public void getItemCount() {
        Context contextTest = InstrumentationRegistry.getContext();
        ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages();
        GalleryManager.RecycleCardsAdapter cardAdapter = new GalleryManager.RecycleCardsAdapter(contextTest, photos);
        assertEquals(photos.size(), cardAdapter.getItemCount());
    }

}
