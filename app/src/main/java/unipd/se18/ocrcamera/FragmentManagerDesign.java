package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Class that manages all the Fragment changes
 * @author Leonardo Pratesi
 */
public class FragmentManagerDesign extends AppCompatActivity {

    /**
     * The bottom layout with all the buttons
     */
    private BottomNavigationView mMainNav;

    /**
     * Object handling Fragments transitions
     */
    private ViewPager mMainPager;

    /**
     *  Result activity Fragment
     */
    private Fragment resultActivityFragment;

    /**
     * Camera Fragment
     */
    private Fragment camera2Fragment;

    /**
     * Forum Fragment //TODO implements this
     */
    private Fragment forumFragment;


    /**
     * variable indicating the menu item currently active
     */
    MenuItem prevMenuItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manager_layout);

        mMainPager = (ViewPager) findViewById(R.id.main_viewpager);
        mMainNav = (BottomNavigationView)findViewById(R.id.main_nav);

        //listener for menu selection
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.result :  //the position of ResultFragment is 0 in the ViewPagerAdapter List
                        setMenuChecked(0);
                        mMainPager.setCurrentItem(0);
                        return true;
                    case R.id.camera :  //the position of ResultFragment is 1 in the ViewPagerAdapter List
                        setMenuChecked(1);
                        mMainPager.setCurrentItem(1);
                        return true;
                    case R.id.forum :   //the position of ResultFragment is 2 in the ViewPagerAdapter List
                        setMenuChecked(2);
                        mMainPager.setCurrentItem(2);
                        return true;
                    default:
                        return false;

                }
            }
        });

        //listener for swipe gestures
        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    //do nothing
            }

            @Override
            public void onPageSelected(int position) {
              setMenuChecked(position);

            }


            @Override
            public void onPageScrollStateChanged(int state) {
                //do nothing
            }
        });

        setupViewPager(mMainPager);

    }

    /**
     * method to setup the view Pager that hold all the fragment in a List
     * @param viewPager
     * @author Leonardo Pratesi
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        resultActivityFragment = new ResultActivityFragment();
        forumFragment = new Fragment();
        camera2Fragment = new Camera2Fragment();
        viewPagerAdapter.addFragment(resultActivityFragment); //first fragment added so position = 0
        viewPagerAdapter.addFragment(camera2Fragment);        //first fragment added so position = 1
        viewPagerAdapter.addFragment(forumFragment);          //first fragment added so position = 2
        viewPager.setAdapter(viewPagerAdapter);
    }

    /**
     * Method to add a graphic output in the user interface to show a change of page
     * sets and animation in the icon of the fragment
     * @param position the position of the fragment in the viewPagerAdapter
     *
     */
    private void setMenuChecked(int position) {
        if (prevMenuItem != null) {
            prevMenuItem.setChecked(false);
        } else {
            mMainNav.getMenu().getItem(0).setChecked(false);
        }
        Log.d("page", "onPageSelected: " + position);
        mMainNav.getMenu().getItem(position).setChecked(true);
        prevMenuItem = mMainNav.getMenu().getItem(position);
    }


    /** NOT USEFULL ANYMORE MAYBE USEFULL LATER
     * Method to set the fragment to be viewed
     * @param fragment the frangment that needs to be inflated
     * @author Leonardo Pratesi
     */
    public void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_viewpager, fragment);
        fragmentTransaction.commit();
    }
}
