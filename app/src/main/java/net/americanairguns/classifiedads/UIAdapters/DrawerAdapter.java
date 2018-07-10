package net.americanairguns.classifiedads.UIAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.americanairguns.classifiedads.Activities.MainActivity;
import net.americanairguns.classifiedads.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DrawerAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> drawerHeaders;
    private Map<String, List<Object>> drawerItems;

    private Integer currentFilter, fontSize;
    private MainActivity.mainInterface mainInterface;

    public DrawerAdapter(Context context, List<String> drawerHeaders, LinkedHashMap<String, List<Object>> drawerItems, Integer currentFilter, MainActivity.mainInterface mainInterface){
        this.context = context;
        this.drawerHeaders = drawerHeaders;
        this.drawerItems = drawerItems;
        this.currentFilter = currentFilter;
        this.mainInterface = mainInterface;
        fontSize = (context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE).getBoolean("fontSize", false) ? context.getResources().getInteger(R.integer.TextSize_Large) : context.getResources().getInteger(R.integer.TextSize_Small));
    }

    @Override
    public int getGroupCount() {
        return drawerHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (drawerHeaders.get(groupPosition).equals("Filters")) {
            return drawerItems.get(drawerHeaders.get(groupPosition)).size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return drawerHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int position) {
        return drawerItems.get(drawerHeaders.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_header, null);
        }

        ((TextView) convertView.findViewById(R.id.title)).setTextSize(fontSize);
        ((TextView) convertView.findViewById(R.id.title)).setText((String) getGroup(groupPosition));
        ((TextView) convertView.findViewById(R.id.title)).setTextSize(fontSize+2);

        ImageView indicator = (ImageView) convertView.findViewById(R.id.drawer_indicator);
        if (getChildrenCount(groupPosition) == 0) {
            indicator.setVisibility( View.INVISIBLE );
        } else {
            indicator.setVisibility( View.VISIBLE );
            indicator.setImageResource(isExpanded ? R.drawable.ic_action_expand : R.drawable.ic_action_previous_item);
        }

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        List<Object> drawerItem = drawerItems.get(drawerHeaders.get(groupPosition));

        ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(((DrawerItem) drawerItem.get(childPosition)).getIcon());
        ((TextView) convertView.findViewById(R.id.title)).setText(((DrawerItem)drawerItem.get(childPosition)).getTitle());
        ((TextView) convertView.findViewById(R.id.title)).setTextSize(fontSize+2);

        if (groupPosition == 0 && childPosition == currentFilter) {
            mainInterface.changeItemTint(convertView);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int parent, int position) {
        return true;
    }
}