package net.americanairguns.classifiedads.Activities;

import android.annotation.SuppressLint;
 import android.app.Activity;
 import android.app.AlertDialog;
 import android.app.SearchManager;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.content.res.Configuration;
 import android.content.res.TypedArray;
 import android.database.Cursor;
 import android.graphics.Color;
 import android.os.Build;
 import android.os.Bundle;
 import android.os.Handler;
 import android.support.annotation.NonNull;
 import android.support.v4.view.GravityCompat;
 import android.support.v4.widget.DrawerLayout;
 import android.util.DisplayMetrics;
 import android.util.Log;
 import android.view.KeyEvent;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.ArrayAdapter;
 import android.widget.ExpandableListView;
 import android.widget.Spinner;
 import android.widget.TableRow;
 import android.widget.TextView;
 import android.widget.Toast;

import net.americanairguns.classifiedads.Fragments.AboutFragment;
import net.americanairguns.classifiedads.Fragments.ClassifiedsFragment;
import net.americanairguns.classifiedads.Database.DBAdapter;
import net.americanairguns.classifiedads.Fragments.DevelopmentFragment;
import net.americanairguns.classifiedads.UIAdapters.DrawerAdapter;
import net.americanairguns.classifiedads.UIAdapters.DrawerItem;
import net.americanairguns.classifiedads.R;
import net.americanairguns.classifiedads.UIAdapters.RangeSeekBar;
import net.americanairguns.classifiedads.Fragments.SettingsFragment;

import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.LinkedHashMap;
 import java.util.List;

 import static android.R.color.background_dark;
 import static android.R.color.background_light;
 import static net.americanairguns.classifiedads.R.array.itemTypes;


public class MainActivity extends Activity implements ClassifiedsFragment.ActivityCallback, AboutFragment.ActivityCallback,
        SettingsFragment.ActivityCallback, DevelopmentFragment.ActivityCallback {

     private ExpandableListView drawerList;
     private DrawerLayout drawerLayout;

     private View lastFilter, subjectView;

     private Boolean doubleBackToExitPressedOnce;
     private Boolean moreFilterExpanded;
     private Boolean updateFilterTint;

     private Integer currentFilter, currentItemFilter, priceFilterStart, priceFilterEnd;
     private String currentNameFilter, currentSortBy, currentSortOrder, tempSortBy, subjectSearchBy;

     private ArrayList<String> drawerHeaders;
     private LinkedHashMap<String, List<Object>> drawerMap;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         DisplayMetrics displayMetrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         Log.i("DISPLAY_XY", String.valueOf(getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density) + "x" + String.valueOf(getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().density));
         Log.i("DISPLAY_DP", String.valueOf(getResources().getDisplayMetrics().density));
         Log.i("DEVICE_TYPE", getResources().getString(R.string.screen_type));
         Log.i("SDK_VERSION", String.valueOf(Build.VERSION.SDK_INT));

         doubleBackToExitPressedOnce = false;
         currentFilter = 0;
         currentNameFilter = null;
         currentItemFilter = null;
         currentSortBy = "adId";
         currentSortOrder = "DESC";
         subjectSearchBy = null;
         priceFilterStart = -1;
         priceFilterEnd = -1;
         updateFilterTint = true;
         subjectView = null;

         layoutSetup();
     }

     @Override
     protected void onStart() {
         super.onStart();
     }
     @Override
     protected void onResume() {
         super.onResume();
     }
     @Override
     protected void onRestart() {
         super.onRestart();
     }
     @Override
     protected void onPause() {
         super.onPause();
     }
     @Override
     protected void onStop() {
         super.onStop();
     }
     @Override
     protected void onDestroy() {
         super.onDestroy();
     }

     public void layoutSetup() {
         final SharedPreferences appPrefs = getSharedPreferences("appPreferences", MODE_PRIVATE);
         Boolean currentTheme = appPrefs.getBoolean("themeSwitch", false);

         setTheme((appPrefs.getBoolean("fontSize", false) ? (currentTheme ? R.style.AppTheme_Dark_LargeFont : R.style.AppTheme_Light_LargeFont) : (currentTheme ? R.style.AppTheme_Dark_MediumFont : R.style.AppTheme_Light_MediumFont)));

         Log.i("THEME_CURRENT", getTheme().toString());

         if (appPrefs.getBoolean("firstRun", true)) {
             TextView txt = new TextView(this);
             txt.setText(R.string.firstRunText);
             txt.setTextSize(18);
             txt.setTextColor(Color.WHITE);
             txt.setPadding(20, 20, 20, 20);
             new AlertDialog.Builder(this)
                     .setTitle("Thank You!")
                     .setView(txt)
                     .setCancelable(false)
                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                             appPrefs.edit().putBoolean("firstRun", false).apply();
                             dialog.dismiss();
                         }
                     })
                     .show();
         }

         getActionBar().setHomeButtonEnabled(true);

         setContentView(R.layout.activity_main);

         drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         drawerList = (ExpandableListView) findViewById(R.id.left_drawer);

         drawerLayout.setBackgroundColor(getResources().getColor((currentTheme ? background_light : background_dark)));

         drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
             @Override
             public void onDrawerSlide(View view, float v) {

             }

             @Override
             public void onDrawerOpened(View view) {
                 findViewById(R.id.action_sort).setEnabled(false);
             }

             @Override
             public void onDrawerClosed(View view) {
                 findViewById(R.id.action_sort).setEnabled(true);
             }

             @Override
             public void onDrawerStateChanged(int i) {}
         });

         drawerMap = new LinkedHashMap<String, List<Object>>();

         drawerHeaders = new ArrayList<String>();
         drawerHeaders.add("Filters");
         drawerHeaders.add("Tools");
         drawerHeaders.add("About");
         drawerHeaders.add("Settings");
         drawerHeaders.add("DEVELOPMENT");

         drawerList.setOnGroupClickListener(onDrawerHeaderClickListener);
         drawerList.setOnChildClickListener(onDrawerChildClickListener);
         moreFilterExpanded = false;
         navDrawer(4);
         navDrawer(0, true);
     }

     public void onRadioClick(View view) {
         tempSortBy = String.valueOf(view.getContentDescription());
     }

     private ExpandableListView.OnGroupClickListener onDrawerHeaderClickListener = new ExpandableListView.OnGroupClickListener() {
         @Override
         public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long l) {
             if (groupPosition == 0) {
                 updateFilterTint = !updateFilterTint;
                 if (updateFilterTint) {
                     setItemSelected(lastFilter);
                     updateFilterTint = false;
                     new Handler().postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             updateFilterTint = true;
                         }
                     }, 100);
                 }
             } else navDrawer(groupPosition, -1, null);
             return false;
         }
     };

     private ExpandableListView.OnChildClickListener onDrawerChildClickListener = new ExpandableListView.OnChildClickListener() {
         @Override
         public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
             navDrawer(groupPosition, childPosition, view);
             return false;
         }
     };

     private void navDrawer(int childPosition) {
         navDrawer(0, childPosition, false, false, null);
     }

     private void navDrawer(int childPosition, boolean initJSON) {
         navDrawer(0, childPosition, false, initJSON, null);
     }

     private void navDrawer(int groupPosition, int childPosition, View view) {
         navDrawer(groupPosition, childPosition, false, false, view);
     }

     private void navDrawer(int groupPosition, int childPosition, boolean refresh) {
         navDrawer(groupPosition, childPosition, refresh, false, null);
     }

     private void navDrawer(int groupPosition, final int childPosition, boolean refresh, boolean initJSON, final View view) {
         switch (groupPosition) {
             case 0: {// Filters
                 final Bundle bundle = new Bundle();
                 switch (childPosition) {
                     case 0:// All Ads
                     case 1:// For Sale
                     case 2:// Wanted
                     case 3:// Trade
                         bundle.putInt("FILTER", childPosition);
                         bundle.putString("SORTBY", currentSortBy);
                         bundle.putString("SORTORDER", currentSortOrder);
                         if (initJSON) {
                             bundle.putBoolean("INIT_JSON", true);
                         }
                         startFragment(bundle);
                         currentFilter = childPosition;
                         setItemSelected(view);
                         return;
                     case 4:// More/Less
                         String[] drawerTitles;
                         TypedArray drawerIcons;
                         if (moreFilterExpanded) {
                             drawerTitles = getResources().getStringArray(R.array.drawer_items_filters_all_more);
                             drawerIcons = getResources().obtainTypedArray(R.array.drawer_item_icons_all_more);
                         } else {
                             drawerTitles = getResources().getStringArray(R.array.drawer_items_filters_all);
                             drawerIcons = getResources().obtainTypedArray(R.array.drawer_item_icons_all);
                         }
                         ArrayList<Object> drawerItems = new ArrayList<Object>();
                         int pos = 0;
                         for (String drawerTitle : drawerTitles) {
                             drawerItems.add(new DrawerItem(drawerTitle, drawerIcons.getResourceId(pos++, -1)));
                         }
                         drawerMap.put(drawerHeaders.get(0), drawerItems);
                         drawerList.setAdapter(new DrawerAdapter(getApplicationContext(), drawerHeaders, drawerMap, currentFilter, new mainInterface() {
                             @Override
                             public void changeItemTint(View view) {
                                 setItemSelected(view);
                             }
                         }));
                         drawerList.expandGroup(0);
                         moreFilterExpanded = !moreFilterExpanded;
                         return;
                     case 5:// By Item Type
                         drawerLayout.closeDrawer(GravityCompat.START);
                         if (refresh) {
                             bundle.putInt("FILTER", currentItemFilter);
                             bundle.putString("SORTBY", currentSortBy);
                             bundle.putString("SORTORDER", currentSortOrder);
                             startFragment(bundle);
                             break;
                         } else {
                             final Spinner dropDown = new Spinner(this);
                             dropDown.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(itemTypes))));
                             new AlertDialog.Builder(this)
                                     .setTitle("Item Filter")
                                     .setMessage("Choose the item:")
                                     .setView(dropDown)
                                     .setCancelable(true)
                                     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                         public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                             dialog.cancel();
                                         }
                                     })
                                     .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                                         public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                             currentFilter = childPosition;
                                             currentItemFilter = dropDown.getSelectedItemPosition() + 10;
                                             Bundle bundle = new Bundle();
                                             bundle.putInt("FILTER", currentItemFilter);
                                             bundle.putString("SORTBY", currentSortBy);
                                             bundle.putString("SORTORDER", currentSortOrder);
                                             startFragment(bundle);
                                             setItemSelected(view);
                                             dialog.dismiss();
                                         }
                                     })
                                     .show();
                         }
                         return;
                     case 6:// By Username
                         drawerLayout.closeDrawer(GravityCompat.START);
                         if (refresh) {
                             bundle.putString("NAME", currentNameFilter);
                             bundle.putString("SORTBY", currentSortBy);
                             bundle.putString("SORTORDER", currentSortOrder);
                             startFragment(bundle);
                             break;
                         } else {
                             final Spinner dropDown = new Spinner(this);
                             List<String> dropList = new ArrayList<String>();
                             DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                             dbAdapter.open();
                             Cursor cursor = dbAdapter.getNames();
                             if (cursor.moveToFirst()) {
                                 do {
                                     dropList.add(cursor.getString(0));
                                 } while (cursor.moveToNext());
                             }
                             dbAdapter.close();
                             dropDown.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, dropList));
                             new AlertDialog.Builder(this)
                                     .setTitle("Name Filter")
                                     .setMessage("Choose the name to search:")
                                     .setView(dropDown)
                                     .setCancelable(true)
                                     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                         public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                             dialog.cancel();
                                         }
                                     })
                                     .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                                         public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                             currentFilter = childPosition;
                                             currentNameFilter = dropDown.getSelectedItem().toString();
                                             Bundle bundle = new Bundle();
                                             bundle.putString("NAME", currentNameFilter);
                                             bundle.putString("SORTBY", currentSortBy);
                                             bundle.putString("SORTORDER", currentSortOrder);
                                             startFragment(bundle);
                                             setItemSelected(view);
                                             dialog.dismiss();
                                         }
                                     })
                                     .show();
                         }
                         return;
                     case 7:// By Subject
                         drawerLayout.closeDrawer(GravityCompat.START);
                         if (refresh) {
                             bundle.putString("SUBJECT", subjectSearchBy);
                             bundle.putString("SORTBY", currentSortBy);
                             bundle.putString("SORTORDER", currentSortOrder);
                             startFragment(bundle);
                             break;
                         } else {
                             subjectView = view;
                             updateFilterTint = false;
                             onSearchRequested();
                         }
                         return;
                     case 8:// By Price
                         drawerLayout.closeDrawer(GravityCompat.START);
                         if (refresh) {
                             bundle.putInt("PRICESTART", priceFilterStart);
                             bundle.putInt("PRICEEND", priceFilterEnd);
                             bundle.putString("SORTBY", currentSortBy);
                             bundle.putString("SORTORDER", currentSortOrder);
                             startFragment(bundle);
                             break;
                         } else {
                             int priceMax = Integer.valueOf(this.getSharedPreferences("appPreferences", MODE_PRIVATE).getString("priceFilterMax", "2500"));
                             @SuppressLint("InflateParams") final View convertView = getLayoutInflater().inflate(R.layout.price_filter, null);
                             final TextView priceStartTxt = (TextView) convertView.findViewById(R.id.priceStart);
                             final TextView priceEndTxt = (TextView) convertView.findViewById(R.id.priceEnd);
                             Log.e("werwerwerwer", String.valueOf(priceMax));
                             final RangeSeekBar<Integer> priceFilter = new RangeSeekBar<Integer>(0, priceMax, this, Integer.valueOf(this.getSharedPreferences("appPreferences", MODE_PRIVATE).getString("priceFilterStep", "50")));
                             priceFilter.setNotifyWhileDragging(true);
                             priceFilter.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                                 @Override
                                 public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                                     priceFilterStart = minValue;
                                     priceFilterEnd = maxValue;
                                     priceStartTxt.setText("$" + String.valueOf(minValue));
                                     priceEndTxt.setText((maxValue.equals(bar.getAbsoluteMaxValue()) ? ">$" : "$") + String.valueOf(maxValue));
                                 }
                             });
                             priceFilterStart = (priceFilterStart >= 0 ? priceFilterStart : priceFilter.getAbsoluteMinValue());
                             priceFilterEnd = (priceFilterEnd >= 0 && priceFilterEnd <= priceFilter.getAbsoluteMaxValue() ? priceFilterEnd : priceFilter.getAbsoluteMaxValue());
                             priceFilter.setSelectedMinValue((priceFilterStart >= 0 ? priceFilterStart : 0));
                             priceFilter.setSelectedMaxValue((priceFilterEnd >= 0 ? priceFilterEnd : priceMax));
                             priceStartTxt.setText("$" + priceFilterStart);
                             priceEndTxt.setText((priceFilterEnd.equals(priceFilter.getAbsoluteMaxValue()) ? ">$" : "$") + priceFilterEnd);
                             ((TableRow) convertView.findViewById(R.id.tableRow2)).addView(priceFilter);
                             new AlertDialog.Builder(this)
                                     .setTitle("Price Filter")
                                     .setMessage("Select price range.")
                                     .setView(convertView)
                                     .setCancelable(true)
                                     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                         public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                             dialog.cancel();
                                         }
                                     })
                                     .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                                         public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                             currentFilter = childPosition;
                                             Bundle bundle = new Bundle();
                                             bundle.putInt("PRICESTART", priceFilterStart);
                                             bundle.putInt("PRICEEND", priceFilterEnd);
                                             bundle.putInt("PRICEMAX", priceFilter.getAbsoluteMaxValue());
                                             bundle.putString("SORTBY", currentSortBy);
                                             bundle.putString("SORTORDER", currentSortOrder);
                                             startFragment(bundle);
                                             setItemSelected(view);
                                             dialog.dismiss();
                                         }
                                     })
                                     .show();
                         }
                         return;
                     default:
                         return;
                 }
                 break;
             }
             case 1: {// Tools
                 updateFilterTint = false;
                 drawerLayout.closeDrawer(GravityCompat.START);
                 startActivity(new Intent(MainActivity.this, CalculatorActivity.class));
                 return;
             }
             case 2: {// About
                 drawerLayout.performHapticFeedback(0);
                 updateFilterTint = false;
                 getFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutFragment()).addToBackStack("about").commit();
                 return;
             }
             case 3: {// Settings
                 updateFilterTint = false;
                 getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).addToBackStack("settings").commit();
                 return;
             }
             case 4: {// DEVELOPMENT
                 updateFilterTint = false;

                 getFragmentManager().beginTransaction().replace(R.id.content_frame, new DevelopmentFragment()).addToBackStack("development").commit();
                 return;
             }
             default:
                 Log.e("NAV_DRAWER_CLICK_POSITION", String.valueOf(groupPosition));
                 break;
         }
     }

     public void setItemSelected(View view) {
         if (updateFilterTint && view != null) {
             Log.i("ITEM_TINT", view.toString());
             if (lastFilter != null) {
                 lastFilter.setBackgroundColor(getResources().getColor(android.R.color.transparent));
             }
             lastFilter = view;
             view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
         }
     }

     private void startFragment(Bundle bundle) {
         while (getFragmentManager().getBackStackEntryCount() > 0) {
             getFragmentManager().popBackStack();
         }
         ClassifiedsFragment classifiedsFragment = new ClassifiedsFragment();
         classifiedsFragment.setArguments(bundle);
         getFragmentManager().beginTransaction().replace(R.id.content_frame, classifiedsFragment).commit();
     }

     public interface mainInterface {
         void changeItemTint(View view);
     }

     @Override
     public boolean onSearchRequested() {
 //        Toast.makeText(this, "Use Commas (,) to seperate keys.", Toast.LENGTH_LONG).show();
         return super.onSearchRequested();
     }

     @Override
     protected void onNewIntent(Intent intent) {
         setIntent(intent);
         if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             subjectSearchBy = intent.getStringExtra(SearchManager.QUERY);
             currentFilter = 7;
             Bundle bundle = new Bundle();
             bundle.putString("SUBJECT", subjectSearchBy);
             bundle.putString("SORTBY", currentSortBy);
             bundle.putString("SORTORDER", currentSortOrder);
             startFragment(bundle);
         }
     }

     @Override
     public void CloseDrawer() {
         drawerLayout.closeDrawer(GravityCompat.START);
         updateFilterTint = true;
     }

     @Override
      public void CloseDrawer(Integer timeout) {
         Log.i("SEARCH_TIMEOUT", String.valueOf(timeout));
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         updateFilterTint = true;
                         setItemSelected(subjectView);
                         subjectView = null;
                     }
                 });
             }
         }, timeout);
     }

     @Override
     public void onBackPressed() {
         if (!drawerLayout.isDrawerOpen(drawerList)) {
             if (getFragmentManager().getBackStackEntryCount() == 0) {
                 if (doubleBackToExitPressedOnce) {
                     super.onBackPressed();
                     return;
                 }
                 this.doubleBackToExitPressedOnce = true;
                 Toast.makeText(getApplicationContext(), "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         doubleBackToExitPressedOnce = false;
                     }
                 }, 2000);
             } else {
                 getFragmentManager().popBackStack();
             }
         } else {
             drawerLayout.closeDrawers();
         }
     }

     @Override
     public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_MENU) {
             if (drawerLayout.isDrawerOpen(drawerList)) {
                 drawerLayout.closeDrawers();
             } else {
                 drawerLayout.openDrawer(drawerList);
             }
             return true;
         } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }

     @Override
     public void onConfigurationChanged(Configuration config) {
         super.onConfigurationChanged(config);
         Log.i("CONFIG", "Configuration changed " + config.toString());
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 if (getFragmentManager().getBackStackEntryCount() == 0) {
                     if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                         drawerLayout.closeDrawer(GravityCompat.START);
                     } else  {
                         drawerLayout.openDrawer(GravityCompat.START);
                     }
                 } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                     drawerLayout.closeDrawer(GravityCompat.START);
                 } else {
                     getFragmentManager().popBackStack();
                 }
                 break;

             case R.id.action_sort:
                 @SuppressLint("InflateParams") final View convertView = this.getLayoutInflater().inflate(R.layout.sort_by, null);
                 convertView.findViewById(R.id.rdbtn_date).performClick();

                 new AlertDialog.Builder(this)
                         .setTitle("Sort By")
                         .setView(convertView)
                         .setIcon(android.R.drawable.ic_menu_sort_alphabetically)
                         .setCancelable(true)
                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                 dialog.cancel();
                             }
                         })
                         .setNeutralButton("Descending", new DialogInterface.OnClickListener() {
                             public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                 currentSortOrder = "DESC";
                                 currentSortBy = tempSortBy;
                                 navDrawer(0, currentFilter, true);
                                 Log.i("SORT_BY", currentSortBy);
                                 dialog.dismiss();
                             }
                         })
                         .setPositiveButton("Ascending", new DialogInterface.OnClickListener() {
                             public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                 currentSortOrder = "ASC";
                                 currentSortBy = tempSortBy;
                                 navDrawer(0, currentFilter, true);
                                 Log.i("SORT_BY", currentSortBy);
                                 dialog.dismiss();
                             }
                         })
                         .show();
                 break;

             case R.id.action_settings:
                 if (getFragmentManager().getBackStackEntryCount() == 0 || !getFragmentManager().getBackStackEntryAt(0).getName().equals("settings"))
                     navDrawer(2, 0, false);
                 else Toast.makeText(this, "Cannot open settings", Toast.LENGTH_LONG).show();
                 break;
             default:
                 break;
         }
         return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
     }
 }//((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);