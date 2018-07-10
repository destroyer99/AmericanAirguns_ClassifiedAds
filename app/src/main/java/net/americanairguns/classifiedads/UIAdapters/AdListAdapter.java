package net.americanairguns.classifiedads.UIAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.americanairguns.classifiedads.R;

public class AdListAdapter extends ArrayAdapter<Ad> {

    Context context;
    int layoutResourceId, imageSize;
    Ad data[] = null;
    Bitmap icon[] = null;

    public AdListAdapter(Context context, int layoutResourceId, Ad[] data, Bitmap[] icon) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.icon = icon;
        this.imageSize = (int)(context.getResources().getDisplayMetrics().density/6 * context.getResources().getDisplayMetrics().widthPixels/context.getResources().getDisplayMetrics().density);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        Ad ad = data[position];

        SharedPreferences appPrefs = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        Boolean showIcons = appPrefs.getBoolean("showIcons", true);
        Integer fontSize = (appPrefs.getBoolean("fontSize", false) ? context.getResources().getInteger(R.integer.TextSize_Large) : context.getResources().getInteger(R.integer.TextSize_Small));

        try {
            if (showIcons) {
                ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(icon[position]);
                (convertView.findViewById(R.id.icon)).getLayoutParams().width = imageSize;
                (convertView.findViewById(R.id.icon)).getLayoutParams().height = imageSize;
            } else {
                convertView.findViewById(R.id.icon).setVisibility(View.GONE);
            }
            ((TextView) convertView.findViewById(R.id.subject)).setTextSize(fontSize);
            ((TextView) convertView.findViewById(R.id.primary)).setTextSize(fontSize-4);
            ((TextView) convertView.findViewById(R.id.secondary)).setTextSize(fontSize-4);

            ((TextView) convertView.findViewById(R.id.subject)).setText(ad.subject);
            ((TextView) convertView.findViewById(R.id.primary)).setText(ad.primary);
            ((TextView) convertView.findViewById(R.id.secondary)).setText(ad.secondary);
            convertView.setTag(ad.adId);
        } catch (NullPointerException npe) { Log.e("AdListAdapter", String.valueOf(position)); }

        return convertView;
    }
}


