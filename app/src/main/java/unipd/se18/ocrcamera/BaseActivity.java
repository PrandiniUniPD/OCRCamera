package unipd.se18.ocrcamera;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import unipd.se18.ocrcamera.forum.Forum;


/**
 * This class is used to define the common behavior that all activities that extend this one
 * will have. They just have to define a BottomNavigationView with "bottom_navigation" as its id
 * in their layout xml file; all the instructions that manage the bottom menu are defined here and
 * made the same for all activities, removing the need to define a BottomNavigationView
 * and the cases for every button every time, reducing as such the redundancy of the code.
 *
 * @author Pietro Balzan
 */
public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the content view as the layout of the activity that's currently active
        setContentView(getContentViewId());

        //get the BottomNavigationView from the layout that was just set
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

        //set the bottombaar menu correctly
        updateNavigationBarState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        navigationView.post(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                int itemId= item.getItemId();
                if (itemId == R.id.nav_result){
                    intent= new Intent(BaseActivity.this, ResultActivity.class);

                    /*
                     * this flag will cause the launched activity to be brought
                     * to the front of this task's history stack if it is already running
                     */
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    /*
                     * launch the activity only if a new actiity instance is needed
                     * to handle the given intent
                     */
                    startActivityIfNeeded(intent, 0);
                }
                else if (itemId == R.id.nav_allergens){
                    intent= new Intent(BaseActivity.this, MainAllergensActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(intent, 0);
                }
                else if (itemId == R.id.nav_picture){
                    intent= new Intent(BaseActivity.this, CameraActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(intent, 0);
                }
                else if (itemId == R.id.nav_gallery){
                    intent= new Intent(BaseActivity.this, GalleryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(intent, 0);
                }
                else if (itemId == R.id.nav_forum){
                    intent= new Intent(BaseActivity.this, Forum.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(intent, 0);
                }
            }
        });

        return false;
    }

    /**
     * This method is used to update the visuals of the BottomBar to show which item
     * of the menu is selected
     *
     * @author Pietro Balzan
     */
    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    /**
     * This method sets the correct item of the menu as selected depending on
     * which actuvity we are in
     * @param itemId the id int of the menu item to be set as selected
     *
     * @author Pietro Balzan
     */
    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i=0, size = menu.size(); i<size; i++ ){
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked= item.getItemId()==itemId;
            if (shouldBeChecked){
                item.setChecked(true);
            }
        }
    }

    /**
     * This method must be overridden by the activities that extend this class.
     * It's used to retrieve the id of the layout xml file of the corresponding activity
     * @return the id of the layout xml file for the corresponding activity
     *
     * @author Pietro Balzan
     */
    abstract int getContentViewId();

    /**
     * This method must be overridden by the activities that extend this class.
     * It's used to retrieve the id of the menu item of the corresponding activity
     * @return the id of the menu item for the corresponding activity
     *
     * @author Pietro Balzan
     */
    abstract  int getNavigationMenuItemId();

}
