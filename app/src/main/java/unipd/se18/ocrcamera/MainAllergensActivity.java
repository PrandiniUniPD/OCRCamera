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
public class MainAllergensActivity extends BaseActivity {

    private static final String TAG= "MainAllergensActivity";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Log.i(TAG, "Starting construction of the layout");

      //set up the ViewPager with the sections adapter
      mViewPager = findViewById(R.id.container);
      setupViewPager(mViewPager);

      //initialize the Tab Layout's content
      TabLayout tabLayout = findViewById(R.id.tabs);
      tabLayout.setupWithViewPager(mViewPager);

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

    /*
     * override BaseActivity's abstract methods to give information
     * about the layout and menu item that should be selected
     */

    @Override
    int getContentViewId(){
        return R.layout.activity_allergens_main;
    }

    @Override
    int getNavigationMenuItemId(){
        return R.id.nav_allergens;
    }

}
