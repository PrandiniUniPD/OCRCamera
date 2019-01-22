package unipd.se18.ocrcamera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds all the Fragments and methods to acquire them
 * @author Leonardo Pratesi
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    /**
     * This is the list holding all the fragments
     */
    private final List<Fragment> mFragmentList = new ArrayList<>();

    /**
     * Fragment manager constructor
     * @param manager FragmentManager from the java class
     */
    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
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
     *
     * @return int number of fragment in the List
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * This method adds the fragments in the list,
     * the position in the fragment is important because is needed for setting the right fragment to show
     * @param fragment the fragment you want to add to the list
     */

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }
}
