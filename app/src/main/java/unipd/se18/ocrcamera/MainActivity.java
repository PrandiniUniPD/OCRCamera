package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Class that manages all the Fragment changes
 * @author Leonardo Pratesi
 */
public class MainActivity extends AppCompatActivity {

    //Log tag
    private final static String TAG = "MainActivity";

    // The bottom layout with all the buttons
    private BottomNavigationView bottomNavigationView;

    // Object handling Fragments transitions
    private ViewPager viewPager;

    // variable indicating the menu item currently active
    MenuItem currentMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manager_layout);

        /**
         * Create the page adapter, with the associated bottonNavigationMenu items id associated
         * with the fragment.
         */
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(R.id.result, new ResultActivityFragment());
        viewPagerAdapter.addFragment(R.id.gallery, new GalleryFragment());
        viewPagerAdapter.addFragment(R.id.camera, new Camera2Fragment());

        /**
         *  get and set the BottomNavigationVeiw, and ViewPager
         */
        viewPager = findViewById(R.id.main_viewpager);
        bottomNavigationView = findViewById(R.id.main_nav);

        //listener for menu selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.result :  //the position of ResultFragment is 0 in the ViewPagerAdapter List
                        setMenuChecked(bottomNavigationView, currentMenuItem, R.id.result);
                        viewPager.setCurrentItem(viewPagerAdapter.findFragmentIndexByMenuItemId(R.id.result));
                        return true;
                    case R.id.camera :  //the position of ResultFragment is 1 in the ViewPagerAdapter List
                        Log.d(TAG,"bottomNavigationView: onNavigationItemSelected: ");
                        setMenuChecked(bottomNavigationView, currentMenuItem, R.id.camera);
                        viewPager.setCurrentItem(viewPagerAdapter.findFragmentIndexByMenuItemId(R.id.camera));
                        return true;
                    case R.id.gallery:   //the position of ResultFragment is 2 in the ViewPagerAdapter List
                        setMenuChecked(bottomNavigationView, currentMenuItem, R.id.gallery);
                        viewPager.setCurrentItem(viewPagerAdapter.findFragmentIndexByMenuItemId(R.id.gallery));
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Called when the currently selected item in the bottom navigation menu is selected again.
        bottomNavigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                Log.v(TAG,"bottomNavigationView: onNavigationItemReselected:");
                // ignore reselected
            }
        });

        //Callback interface for responding to changing state of the selected page.
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG,"viewPager: onPageScrolled:");
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"viewPager: onPageSelected:");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG,"viewPager: onPageScrollStateChanged:");
            }
        });

        //bind the view pager with the adapter
        viewPager.setAdapter(viewPagerAdapter);
    }

    /**
     * set as checked the item
     * @param item_id the id of the item in the BottomNavigationView
     */
    private static void setMenuChecked(BottomNavigationView bottomNavigationView, MenuItem currentMenuItem, int item_id) {
        if (currentMenuItem != null) {
            currentMenuItem.setChecked(false);
        }
        Menu menu = bottomNavigationView.getMenu();
        //logItem(menu);
        currentMenuItem = menu.findItem(item_id);
        currentMenuItem.setChecked(true);
        Log.i(TAG, "setMenuChecked: " + item_id);
    }

    /**
     * Debug all the id of all the menu items in the passed menu
     * @param menu
     */
    private static void logItem(Menu menu) {
        int menu_size = menu.size();
        if (menu_size > 0) {
            Log.d(TAG,"logItem: menu_size=" + menu_size);
            for (int i = 0; i < menu_size; i++) {
                Log.d(TAG,"logItem: item=" + menu.getItem(i) +
                        "; item_index=" + i +
                        "; item_id=" + menu.getItem(i).getItemId());
            }
        } else {
            Log.d(TAG, "logItem: no item in this menu: size=" + menu_size);
        }
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
