package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {
    private final String tag= "OnSwipeTouvhListener";
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    // Actions to perform on swipe towards the right
    public void onSwipeRight() {
    }
    // Actions to perform on swipe towards the left
    public void onSwipeLeft() {
    }
    // Actions to perform on swipe towards the top
    public void onSwipeTop() {
    }
    // Actions to perform on swipe towards the bottom
    public void onSwipeBottom() {
    }

    /*
     * this class is used to calculate which gesture was performed by the user
     */
    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float distanceY = e2.getY() - e1.getY();
                float distanceX = e2.getX() - e1.getX();
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    if (Math.abs(distanceX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (distanceX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(distanceY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                Log.e(tag, "error");
                exception.printStackTrace();
            }
            return result;
        }
    }
}