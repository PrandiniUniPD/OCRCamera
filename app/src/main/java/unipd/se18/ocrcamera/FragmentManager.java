package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

/**
 * Class that manages all the Fragment changes
 * @author Leonardo Pratesi
 */
public class FragmentManager extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private Fragment galleryFragment;
    private Fragment allergensFragment;
    private Fragment forumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manager_layout);


        galleryFragment = new GalleryFragment();
        allergensFragment = new Fragment();
        forumFragment = new Fragment();

        mMainFrame = (FrameLayout)findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView)findViewById(R.id.main_nav);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.camera :
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(galleryFragment);
                        return true;
                    case R.id.allergens :
                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(allergensFragment);
                        return true;
                    case R.id.forum :
                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(forumFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });
    }

    /**
     * Method to set the fragment to be viewed
     * @param fragment the frangment that needs to be inflated
     */
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
