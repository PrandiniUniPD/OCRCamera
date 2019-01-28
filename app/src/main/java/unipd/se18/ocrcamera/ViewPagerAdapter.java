package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that holds all the Fragments and methods to acquire them
 * @author Leonardo Pratesi
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    // This is the list holding all the fragments
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private Map<Integer,Integer> idMap;

    /**
     * Fragment manager constructor
     * @param manager FragmentManager from the java class
     */
    ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        idMap = new HashMap<>();
    }

    /**
     *
     * @param position selects the particular fragment in that position
     * @return the fragment
     */
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * Return the index of the Fragment of the associated ItemMenuId.
     * If no fragment index are associated, return null.
     * @param menuItemId is the integer R.id.menu_item
     * @return
     */
    int findFragmentIndexByMenuItemId(int menuItemId) {
        return idMap.get(menuItemId);
    }

    /**
     *
     * @return int number of fragment in the List
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * This method adds the fragments in the list, the position in the fragment is important because
     * is needed for setting the right fragment to show
     * @param fragment the fragment you want to add to the list
     */
    int addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
        return mFragmentList.indexOf(fragment);
    }

    int addFragment(int menuItemId, Fragment fragment) {
        mFragmentList.add(fragment);
        idMap.put(menuItemId, mFragmentList.indexOf(fragment));
        return mFragmentList.indexOf(fragment);
    }
}
