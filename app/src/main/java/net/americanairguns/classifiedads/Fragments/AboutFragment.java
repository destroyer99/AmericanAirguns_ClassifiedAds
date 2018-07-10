package net.americanairguns.classifiedads.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.americanairguns.classifiedads.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_about, container, false);

        getActivity().findViewById(R.id.action_sort).setEnabled(false);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.INVISIBLE);

        view.findViewById(R.id.emailBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent email = new Intent(android.content.Intent.ACTION_SEND);
                email.setType("plain/text");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"atleytroyer@gmail.com", "brad@airguns.net"});
                email.putExtra(Intent.EXTRA_SUBJECT, "[ANDROID APP] Classified Ads App Customer Email");
                startActivity(email);
            }
        });
        // TODO: Explain how to use the search function. Explain use of commas and spaces.
        // TODO: Explain how to navigate the side panel. Buttons to open/close, how to show/hide expanded list.
        TextView aboutText = (TextView) view.findViewById(R.id.aboutTextView);
        aboutText.setText("\t\t\tStuff to mention:" +
                "\n\n\t1.  How to search for keywords. Using commas to seperate phrases." +
                "\n\n\t2.  How to open/close the navigation pane. Buttons involved." +
                "\n\n\t3.  ");
        aboutText.setTextSize((getActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE).getBoolean("fontSize", false) ? 20 : 16));

        ((ActivityCallback)getActivity()).CloseDrawer();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.action_sort).setEnabled(true);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.VISIBLE);
    }

    public interface ActivityCallback {
        void CloseDrawer();
    }
}
