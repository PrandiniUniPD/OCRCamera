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
import static org.junit.Assert.assertTrue;

public class GalleryTest {

    /**
     * Test the deletion of an image from the RecycleCardsAdapter
     * This method is called from the GalleryActivity
     */
    @Test
    public void getDeletedPhotoPosition() throws IOException {
        //Load the images
        Context ContextTest = InstrumentationRegistry.getContext();
        ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages();
        GalleryManager.RecycleCardsAdapter cardAdapter = new GalleryManager.RecycleCardsAdapter(ContextTest, photos);

        //Delete the image
        GalleryManager.PhotoStructure photoToDelete = photos.get(2);
        GalleryManager.deleteImage(photoToDelete);

        //Verify that Ive deleted the image in the correct position
        assertEquals(cardAdapter.getDeletedPhotoPosition(), 2);
    }

    /**
     * Test the cropping of image
     * This method is used in GalleryManager for improving the performance
     * of the gallery
     */
    @Test
    public void cropImage() {
        //Test with square image size
        int w = 500, h = 500;

        //Create a blank bitmap
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates an empty bitmap

        Bitmap newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,100, 100);
        assertTrue(newBmp.getWidth()<=100);
        assertTrue(newBmp.getHeight()<=100);

        newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,300, 100);
        assertTrue(newBmp.getWidth()<=300);
        assertTrue(newBmp.getHeight()<=100);

        newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,215, 326);
        assertTrue(newBmp.getWidth()<=215);
        assertTrue(newBmp.getHeight()<=326);

        //Tests with random image size
        w = 684;
        h = 426;
        bmp = Bitmap.createBitmap(w, h, conf);

        newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,100, 100);
        assertTrue(newBmp.getWidth()<=100);
        assertTrue(newBmp.getHeight()<=100);

        newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,300, 100);
        assertTrue(newBmp.getWidth()<=300);
        assertTrue(newBmp.getHeight()<=100);

        newBmp = GalleryManager.RecycleCardsAdapter.resize(bmp,215, 326);
        assertTrue(newBmp.getWidth()<=215);
        assertTrue(newBmp.getHeight()<=326);
    }

    /**
     * Test the method for count the number of images loaded into the gallery
     * Method used in RecycleCardsAdapter for create the cardViewHolder
     */
    @Test
    public void getItemCount() {
        Context contextTest = InstrumentationRegistry.getContext();
        ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages();

        GalleryManager.RecycleCardsAdapter cardAdapter = new GalleryManager.RecycleCardsAdapter(contextTest, photos);
        assertEquals(photos.size(), cardAdapter.getItemCount());
    }

}
