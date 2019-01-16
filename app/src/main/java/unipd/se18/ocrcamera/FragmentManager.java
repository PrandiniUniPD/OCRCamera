package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class FragmentManager extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private CameraFragment cameraFragment;
    private AllergensFragment allergensFragment;
    private ForumFragment forumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manager_layout);

        mMainFrame = (FrameLayout)findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView)findViewById(R.id.main_nav);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.camera :
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(cameraFragment);
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

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
