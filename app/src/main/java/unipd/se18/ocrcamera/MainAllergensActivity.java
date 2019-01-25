package unipd.se18.ocrcamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import unipd.se18.ocrcamera.forum.Forum;

/*
 * @author Pietro Balzan
 */
public class MainAllergensActivity extends AppCompatActivity {

    private static final String TAG= "MainAllergensActivity";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_allergens_main);
      Log.i(TAG, "Starting construction of the layout");

      //set up the ViewPager with the sections adapter
      mViewPager = findViewById(R.id.container);
      setupViewPager(mViewPager);

      //initialize the Tab Layout's content
      TabLayout tabLayout = findViewById(R.id.tabs);
      tabLayout.setupWithViewPager(mViewPager);

      //set reference to the BottomNavigationView
      BottomNavigationView bottomNav= findViewById(R.id.bottom_navigation);

      //react to clicks on the items of bottomView
      bottomNav.setOnNavigationItemSelectedListener(navListener);

      //set this activity's menu icon as checked
      Menu menu= bottomNav.getMenu();
      MenuItem thisActivityMenuIcon = menu.getItem(0);
      thisActivityMenuIcon.setChecked(true);
    }

    /**
     * This method is used to set the viewPager up with the fragments and the layout elements
     * @param viewPager the viewPager to be set up
     * @author Pietro Balzan
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        //get tab names to be shown in the activity
        String searchFragmentName= getString(R.string.allergens_search_fragment);
        String userFragmentName = getString(R.string.allergens_user_fragment);

        //add the two fragments
        Log.i(TAG, "Set up adapter");
        mSectionsPageAdapter.addFragment(new AllergensSearchFragment() ,searchFragmentName);
        mSectionsPageAdapter.addFragment(new UserAllergensFragment() ,userFragmentName);
        viewPager.setAdapter(mSectionsPageAdapter);
    }


    /**
     * this class is used to manage and add fragents to the activity
     * @author Pietro Balzan
     */
    private class SectionsPageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        //default constructor of FragmentPageAdapter
        public SectionsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * This method is used to add fragments to the list to be used in the layout of the Activity
         * @param fragment to be added to the list
         * @param title of the fragment to be shown in the activity
         */
        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        //getter methods

        @Override
        public CharSequence getPageTitle(int position){
           return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    //create a private listener to use in this Activity
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent;
                    //start an activity depending on what was chosen
                    switch (menuItem.getItemId()){
                        case R.id.nav_allergens:
                            //we already are in this activity
                            break;
                        case R.id.nav_result:
                            intent= new Intent(MainAllergensActivity.this, ResultActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityIfNeeded(intent, 0);
                            break;
                        case R.id.nav_picture:
                            intent= new Intent(MainAllergensActivity.this, CameraActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityIfNeeded(intent, 0);
                            break;
                        case R.id.nav_gallery:
                            intent= new Intent(MainAllergensActivity.this, GalleryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityIfNeeded(intent, 0);
                            break;
                        case R.id.nav_forum:
                            intent = new Intent(MainAllergensActivity.this, Forum.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityIfNeeded(intent, 0);
                            break;
                    }

                    return false;
                }
            };
}
