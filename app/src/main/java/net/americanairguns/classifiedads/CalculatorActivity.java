package net.americanairguns.classifiedads;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalculatorActivity extends FragmentActivity implements ActionBar.TabListener,
                                            TrajectoryCalculatorFragment.ActivityCallback {

    private List<String> tabs = new ArrayList<String>(Arrays.asList("Calculators/\nConverters", "Trajectory\nCalculator"));//, "Trajectory\nData Table"};
    private Boolean dataTableCreated = false;

    private ViewPager viewPager;
    private ActionBar actionBar;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_calculators);

             viewPager = (ViewPager) findViewById(R.id.pager);
             actionBar = getActionBar();

             viewPager.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
             viewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager(), tabs));

             actionBar.setHomeButtonEnabled(true);
             actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

             View view;
             for (String tabName : tabs) {
     //             actionBar.addTab(actionBar.newTab().setText(tabName).setTabListener(this));
                 view = getLayoutInflater().inflate(R.layout.calc_tab_headers, null, false);
                 ((TextView)view.findViewById(R.id.calcTabHeader)).setText(tabName.toUpperCase());
                 actionBar.addTab(actionBar.newTab().setCustomView(view).setTabListener(this));
             }

             viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                 @Override
                 public void onPageSelected(int position) {
                     actionBar.setSelectedNavigationItem(position);
                 }

                 @Override
                 public void onPageScrolled(int arg0, float arg1, int arg2) {}

                 @Override
                 public void onPageScrollStateChanged(int arg0) {}
             });
         }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public Fragment getTableFragment() {
        if (!tabs.get(tabs.size()-1).equals("Trajectory\nData Table")) {
            tabs.add("Trajectory\nData Table");
            View view = getLayoutInflater().inflate(R.layout.calc_tab_headers, null, false);
            ((TextView)view.findViewById(R.id.calcTabHeader)).setText(tabs.get(tabs.size() - 1).toUpperCase());
            actionBar.addTab(actionBar.newTab().setCustomView(view).setTabListener(this));
            viewPager.getAdapter().notifyDataSetChanged();
        }
        return ((TabsPagerAdapter) viewPager.getAdapter()).fragment;
    }

    @Override
    public void tableCreated(boolean tableCreated) {
        this.dataTableCreated = tableCreated;
    }

    @Override
    public void changeTab(int position) {
        viewPager.setCurrentItem(position >= 0 ? position : tabs.size() - 1, true);
    }

    @Override
    public void removeTab(int position) {
        if (!dataTableCreated) {
            actionBar.removeTabAt(position >= 0 ? position : tabs.size() - 1);
            tabs.remove(tabs.size() - 1);
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
