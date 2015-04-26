package net.americanairguns.classifiedads;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class ClassifiedsFragment extends Fragment {

    static protected enum cursorIndex {AD_ID, AD_SUBMIT_DATE, AD_TIME_STAMP,
        AD_TYPE, AD_ITEM, AD_SUBJECT, AD_TEXT, TRADE_TEXT, ASKING_PRICE, PLUS_SHIPPING,
        NAME, REMOTE_ADDRESS, EMAIL_ADDRESS, PHONE_NUMBER, TIME_ZONE,
        IMAGE_1_FILE, IMAGE_2_FILE, IMAGE_3_FILE, IMAGE_4_FILE, IMAGE_5_FILE,
        AD_ICON}

    static protected String[] adFilters = {"All", "ForSale", "Wanted", "Trade", "rifle", "pistol" , "scope", "accessory"};

    private ListView listView;
    private Integer filter, priceStart, priceEnd;
    private Context context;
    private String nameFilter, sortBy, sortOrder, subjectFilter;
    private Boolean swipeIsInRefresh;

    public ClassifiedsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_classifieds, container, false);

        this.context = getActivity();
        this.filter = this.getArguments().getInt("FILTER", 0);
        this.nameFilter = this.getArguments().getString("NAME");
        this.sortBy = this.getArguments().getString("SORTBY");
        this.sortOrder = this.getArguments().getString("SORTORDER");
        this.subjectFilter = this.getArguments().getString("SUBJECT");
        this.priceStart = this.getArguments().getInt("PRICESTART", -1);
        this.priceEnd = this.getArguments().getInt("PRICEEND", -1);
        this.swipeIsInRefresh = false;

        view.findViewById(R.id.swipeRefresh).setVisibility(View.VISIBLE);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        final TextView refreshView = (TextView) view.findViewById(R.id.swipeRefresh);
        refreshView.setTextSize((context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE).getBoolean("fontSize", false) ? context.getResources().getInteger(R.integer.TextSize_Large)-2 : context.getResources().getInteger(R.integer.TextSize_Small)-2));
        swipeView.setEnabled(false);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!swipeIsInRefresh) {
                    swipeIsInRefresh = true;
                    refreshView.setText("Refreshing...");
                    new JsonAdapter(context, new FragmentCallback() {
                        @Override
                        public void onTaskDone() {
                            refreshView.setText("Refreshed");
                            swipeView.setRefreshing(false);
                            syncAds();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipeIsInRefresh = false;
                                    if (refreshView.getText().equals("Refreshed"))
                                        refreshView.setText(getResources().getString(R.string.swipeRefresh));
                                }
                            }, 1500);
                        }

                        @Override
                        public void onTaskFailed(final String errMsg) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeIsInRefresh = false;
                                    swipeView.setRefreshing(false);
                                    refreshView.setText("Refresh Failed");
                                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Command=List");
                } else swipeView.setRefreshing(false);
            }
        });

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment = new DisplayAdFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("ADID", Integer.valueOf(view.getTag().toString()));
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(view.getTag().toString()).commit();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                Log.e("LONG_CLICK_AD_ID", view.getTag().toString());
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete this ad from the web server?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                try {
                                    new DBAdapter(context).open().deleteAdById(Integer.valueOf(view.getTag().toString())).close();
                                    Toast.makeText(getActivity().getApplicationContext(), new WebDBAdapter().execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Password=" + getActivity().getResources().getString(R.string.mobilePassword) + "&Command=DeleteAd&AdId=" + Integer.valueOf(view.getTag().toString())).get(), Toast.LENGTH_LONG).show();
                                    syncAds();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((absListView != null && absListView.getChildCount() > 0 && absListView.getChildAt(0).getTop() == 0) || absListView.getChildCount() == 0) {
                    swipeView.setEnabled(true);
                } else {
                    swipeView.setEnabled(false);
                }
            }
        });

        if (this.getArguments().getBoolean("INIT_JSON", false)) {
            this.getArguments().remove("INIT_JSON");
            swipeIsInRefresh = true;
            swipeView.setRefreshing(true);
            new JsonAdapter(context, new FragmentCallback() {
                @Override
                public void onTaskDone() {
                    swipeIsInRefresh = false;
                    swipeView.setRefreshing(false);
                    syncAds();
                    refreshView.setText(getResources().getString(R.string.swipeRefresh));
                }

                @Override
                public void onTaskFailed(final String errMsg) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            swipeIsInRefresh = false;
                            swipeView.setRefreshing(false);
                            refreshView.setText("Refresh Failed");
                            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Command=List");
            ((TextView) view.findViewById(R.id.swipeRefresh)).setText("Initializing...");
        } else syncAds();
        return view;
    }

    public void populateListView(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            Bitmap[] icons = new Bitmap[cursor.getCount()];
            Ad adListView[] = new Ad[cursor.getCount()];

            if (cursor.moveToFirst()) {
                do {
                    adListView[cursor.getPosition()] = new Ad(cursor.getString(cursorIndex.AD_SUBJECT.ordinal()),
                            cursor.getString(cursorIndex.AD_TYPE.ordinal()) + "  -  " + cursor.getString(cursorIndex.AD_ITEM.ordinal()) +
                                    (((cursor.getString(cursorIndex.AD_TYPE.ordinal()).equals("ForSale")) && !(cursor.getString(cursorIndex.ASKING_PRICE.ordinal()).equals("null")) && (Integer.valueOf(cursor.getString((cursorIndex.ASKING_PRICE.ordinal())))>0)) ?
                                            ("  -  $" + cursor.getInt(cursorIndex.ASKING_PRICE.ordinal())) : ("")),
                            cursor.getString(cursorIndex.AD_ID.ordinal()) + "  -  " + cursor.getString(cursorIndex.AD_SUBMIT_DATE.ordinal()), cursor.getInt(cursorIndex.AD_ID.ordinal()));

                    if (cursor.getBlob(cursorIndex.AD_ICON.ordinal()) != null && !Arrays.toString(cursor.getBlob(cursorIndex.AD_ICON.ordinal())).equals("[]")) {
                        byte[] bytes = cursor.getBlob(cursorIndex.AD_ICON.ordinal());
                        icons[cursor.getPosition()] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    } else {
                        icons[cursor.getPosition()] = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera);
                    }
                } while(cursor.moveToNext());
            } else Log.e("AD_LIST", "EMPTY");
            listView.setAdapter(new AdListAdapter(context, R.layout.ad_list_layout, adListView, icons));
        } else if (cursor != null) {
            listView.setAdapter(new ArrayAdapter<String>(context, R.layout.empty_list_adapter, new String[] {"Search Results Are Empty."}));
        } else {
            listView.setAdapter(new ArrayAdapter<String>(context, R.layout.empty_list_adapter, new String[] {"Query Failed. Please Try Again."}));
        }
        if (subjectFilter == null)
            ((ActivityCallback)getActivity()).CloseDrawer();
        else
            ((ActivityCallback) getActivity()).CloseDrawer(500 - (cursor.getCount() * 2));
    }

    public void syncAds() {
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.open();

        if (nameFilter==null && subjectFilter==null && (priceStart==-1 && priceEnd==-1)) {
            switch (filter) {
                case 0:
                    populateListView(dbAdapter.getAllAds(sortBy, sortOrder));
                    break;
                case 1:case 2:case 3:
                    populateListView(dbAdapter.getAdsByType(adFilters[filter], sortBy, sortOrder));
                    break;
                case 10:case 11:case 12:case 13:
                    filter += -6;
                    populateListView(dbAdapter.getAdsByItem(adFilters[filter], sortBy, sortOrder));
                    break;
                default:
                    populateListView(dbAdapter.getAllAds(sortBy, sortOrder));
                    break;
            }
        } else if (subjectFilter==null && (priceStart==-1 && priceEnd==-1)) {
            populateListView(dbAdapter.getAdsByName(nameFilter, sortBy, sortOrder));
        } else if (priceStart==-1 && priceEnd==-1) {
            populateListView(dbAdapter.getAdsByKeywords(subjectFilter, sortBy, sortOrder));
        } else {
            populateListView(dbAdapter.getAdsByPrice(priceStart, priceEnd, getArguments().getInt("PRICEMAX", 2500), sortBy, sortOrder));
        }
        dbAdapter.close();
        System.gc();
    }

    public interface FragmentCallback {
        public void onTaskDone();
        public void onTaskFailed(String errMsg);
    }

    public interface ActivityCallback {
        public void CloseDrawer();
        public void CloseDrawer(Integer timeout);
    }
}
