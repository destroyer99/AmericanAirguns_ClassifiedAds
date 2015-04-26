package net.americanairguns.classifiedads;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private List<String> tabs;
    public Fragment fragment;

    public TabsPagerAdapter(FragmentManager fm, List<String> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new CalculatorsFragment();
            case 1:
                return new TrajectoryCalculatorFragment();
            case 2:
                return fragment = new DisplayCalcData();
        }
        return null;
    }

    @Override
    public int getCount() {
        return this.tabs.size();
    }
}
