package net.americanairguns.classifiedads.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.americanairguns.classifiedads.Database.DBAdapter;
import net.americanairguns.classifiedads.Database.GetImages;
import net.americanairguns.classifiedads.R;
import net.americanairguns.classifiedads.Database.WebDBAdapter;

import java.util.concurrent.ExecutionException;

public class DisplayAdFragment extends Fragment {

    protected enum cursorIndex {AD_ID, AD_SUBMIT_DATE, AD_TIME_STAMP,
        AD_TYPE, AD_ITEM, AD_SUBJECT, AD_TEXT, TRADE_TEXT, ASKING_PRICE, PLUS_SHIPPING,
        NAME, REMOTE_ADDRESS, EMAIL_ADDRESS, PHONE_NUMBER, TIME_ZONE,
        IMAGE_1_FILE, IMAGE_2_FILE, IMAGE_3_FILE, IMAGE_4_FILE, IMAGE_5_FILE,
        AD_ICON}

    public DisplayAdFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_ad, container, false);

        getActivity().findViewById(R.id.action_sort).setEnabled(false);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.INVISIBLE);

        final Cursor cursor;
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.open();

        cursor = dbAdapter.getAdByAdId(this.getArguments().getInt("ADID", 0));
        if (cursor == null || cursor.getCount() != 1 || !cursor.moveToFirst()) {
            Log.e("CURSOR_ERR", "Cursor es no bueno");
            return view;
        }

        ((TextView) view.findViewById(R.id.textViewSubject)).setText(cursor.getString(cursorIndex.AD_SUBJECT.ordinal()));

        ((TextView)view.findViewById(R.id.txtViewAdNumber)).setText("Ad#: " + cursor.getString(cursorIndex.AD_ID.ordinal()));
        ((TextView)view.findViewById(R.id.txtViewName)).setText("Name: " + cursor.getString(cursorIndex.NAME.ordinal()));
        ((TextView)view.findViewById(R.id.txtViewEmail)).setText("Email: ");
        ((TextView)view.findViewById(R.id.txtViewEmailLink)).setText(cursor.getString(cursorIndex.EMAIL_ADDRESS.ordinal()));
        ((TextView)view.findViewById(R.id.txtViewPhone)).setText("Phone: ");
        ((TextView)view.findViewById(R.id.txtViewPhoneLink)).setText(cursor.getString(cursorIndex.PHONE_NUMBER.ordinal()));
        ((TextView)view.findViewById(R.id.txtViewDate)).setText("Date: " + cursor.getString(cursorIndex.AD_SUBMIT_DATE.ordinal()));
        if ((cursor.getString(cursorIndex.AD_TYPE.ordinal()).equals("ForSale"))
                && (Integer.valueOf(cursor.getInt(cursorIndex.ASKING_PRICE.ordinal()))!=null)
                && (Integer.valueOf(cursor.getString((cursorIndex.ASKING_PRICE.ordinal())))>0)) {
            ((TextView) view.findViewById(R.id.textViewPrice)).setText("Asking Price: $" + cursor.getInt(cursorIndex.ASKING_PRICE.ordinal()));
            if (cursor.getString(cursorIndex.PLUS_SHIPPING.ordinal()).equals("true")) {
                ((TextView) view.findViewById(R.id.textViewPrice)).setText(((TextView) view.findViewById(R.id.textViewPrice)).getText() + " + ");
                view.findViewById(R.id.imgViewShipping).setVisibility(View.VISIBLE);
            }
        }
        if (!cursor.getString(cursorIndex.AD_TEXT.ordinal()).equals("null") && cursor.getString(cursorIndex.AD_TEXT.ordinal()) != null) {
            ((TextView) view.findViewById(R.id.textViewDescription)).setText(cursor.getString(cursorIndex.AD_TEXT.ordinal()).replaceAll("\n", "").replaceAll("\n", "").replaceAll("  ", " "));
        } else {
            ((TextView) view.findViewById(R.id.textViewDescription)).setText("No Description");
        }

        try {
            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);

            SharedPreferences appPrefs = getActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
            Boolean downloadImages = appPrefs.getBoolean("downloadImages", true);
            if (downloadImages) {
                if (!(cursor.getString(cursorIndex.IMAGE_1_FILE.ordinal())).equals("")) {
                    new GetImages((ImageView) view.findViewById(R.id.imageView1), size.x).execute(cursor.getString(cursorIndex.IMAGE_1_FILE.ordinal())).get();
                }
                if (!(cursor.getString(cursorIndex.IMAGE_2_FILE.ordinal())).equals("")) {
                    new GetImages((ImageView) view.findViewById(R.id.imageView2), size.x).execute(cursor.getString(cursorIndex.IMAGE_2_FILE.ordinal())).get();
                }
                if (!(cursor.getString(cursorIndex.IMAGE_3_FILE.ordinal())).equals("")) {
                    new GetImages((ImageView) view.findViewById(R.id.imageView3), size.x).execute(cursor.getString(cursorIndex.IMAGE_3_FILE.ordinal())).get();
                }
                if (!(cursor.getString(cursorIndex.IMAGE_4_FILE.ordinal())).equals("")) {
                    new GetImages((ImageView) view.findViewById(R.id.imageView4), size.x).execute(cursor.getString(cursorIndex.IMAGE_4_FILE.ordinal())).get();
                }
                if (!(cursor.getString(cursorIndex.IMAGE_5_FILE.ordinal())).equals("")) {
                    new GetImages((ImageView) view.findViewById(R.id.imageView5), size.x).execute(cursor.getString(cursorIndex.IMAGE_5_FILE.ordinal())).get();
                }
            } else {
                Cursor cursorImages = dbAdapter.getImages(cursor.getInt(cursorIndex.AD_ID.ordinal()));
                if (cursorImages.moveToFirst()) {
                    byte[] bytes = cursorImages.getBlob(0);

                    if (bytes != null)
                        ((ImageView) view.findViewById(R.id.imageView1)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    bytes = cursorImages.getBlob(1);
                    if (bytes != null)
                        ((ImageView) view.findViewById(R.id.imageView2)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    bytes = cursorImages.getBlob(2);
                    if (bytes != null)
                        ((ImageView) view.findViewById(R.id.imageView3)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    bytes = cursorImages.getBlob(3);
                    if (bytes != null)
                        ((ImageView) view.findViewById(R.id.imageView4)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    bytes = cursorImages.getBlob(4);
                    if (bytes != null)
                        ((ImageView) view.findViewById(R.id.imageView5)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            }
        }
        catch (InterruptedException e) { e.printStackTrace(); }
        catch (ExecutionException e) { e.printStackTrace(); }
        catch (IllegalStateException e) { e.printStackTrace(); }
        catch (NullPointerException e) { e.printStackTrace(); }

        ((TextView)view.findViewById(R.id.deleteAdText)).setText("Delete Ad");
        ((TextView)view.findViewById(R.id.deleteAdText)).setTextColor(Color.BLUE);
        view.findViewById(R.id.deleteAdText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    new DBAdapter(getActivity()).open().deleteAdById(getArguments().getInt("ADID", 0)).close();
                                    Toast.makeText(getActivity().getApplicationContext(), new WebDBAdapter().execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Password=" + getActivity().getResources().getString(R.string.mobilePassword) + "&Command=DeleteAd&AdId=" + getArguments().getInt("ADID", 0)).get(), Toast.LENGTH_LONG).show();
                                    getFragmentManager().popBackStack();

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
            }
        });

        dbAdapter.close();
        System.gc();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.action_sort).setEnabled(true);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.VISIBLE);
    }
}
