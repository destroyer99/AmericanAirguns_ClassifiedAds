package net.americanairguns.classifiedads;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TrajectoryCalculatorFragment extends Fragment {

    public TrajectoryCalculatorFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_trajectory_calculator, container, false);

        if (getActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE).getBoolean("calcDefVals", true)) {
            ((EditText) view.findViewById(R.id.pelletWeight)).setText("7.9");
            ((EditText) view.findViewById(R.id.pelletBallisticCoef)).setText("0.021");
            ((EditText) view.findViewById(R.id.pelletDiameter)).setText("0.18");
            ((EditText) view.findViewById(R.id.pelletMuzzleVel)).setText("825");
            ((EditText) view.findViewById(R.id.scopeHAB)).setText("1.875");
            ((EditText) view.findViewById(R.id.scopeMOA)).setText("8");
            ((EditText) view.findViewById(R.id.scopeClicks)).setText("48");
            ((EditText) view.findViewById(R.id.rangeZero)).setText("25");
            ((EditText) view.findViewById(R.id.rangeStart)).setText("10");
            ((EditText) view.findViewById(R.id.rangeEnd)).setText("55");
            ((EditText) view.findViewById(R.id.rangeIncrement)).setText("1");
            ((EditText) view.findViewById(R.id.windSpeed)).setText("5");
            ((EditText) view.findViewById(R.id.windDirection)).setText("90");
        }

        view.findViewById(R.id.calcTrajectory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                try {
                    ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(button.getWindowToken(), 0);
                    ((DisplayCalcData) ((ActivityCallback) getActivity()).getTableFragment()).updateTable(
                            Double.valueOf(((EditText) view.findViewById(R.id.pelletWeight)).getText().toString()),
                            Double.valueOf(((EditText) view.findViewById(R.id.pelletBallisticCoef)).getText().toString()),
                            Double.valueOf(((EditText) view.findViewById(R.id.pelletMuzzleVel)).getText().toString()),
                            Double.valueOf(((EditText) view.findViewById(R.id.scopeHAB)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.scopeMOA)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.scopeClicks)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.rangeZero)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.rangeStart)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.rangeEnd)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.rangeIncrement)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.windSpeed)).getText().toString()),
                            Integer.valueOf(((EditText) view.findViewById(R.id.windDirection)).getText().toString()),
                            ((CheckBox) view.findViewById(R.id.useOldBushnellTurret)).isChecked());
                    ((ActivityCallback) getActivity()).changeTab(-1);
                    ((ActivityCallback) getActivity()).tableCreated(true);
                } catch (NumberFormatException nfe) {
                    ((ActivityCallback) getActivity()).removeTab(-1);
                    Toast.makeText(getActivity(), "Not all fields are correctly filled in", Toast.LENGTH_LONG).show();
                }
                try {
                    ((LinearLayout) button.getParent()).findFocus().clearFocus();
                } catch (NullPointerException npe) { Log.i("FOCUS", npe.getMessage()==null ? "No previous view has focus" : npe.getMessage() ); }
            }
        });
        return view;
    }

    public interface ActivityCallback {
        public Fragment getTableFragment();
        public void tableCreated(boolean tableCreated);
        public void changeTab(int position);
        public void removeTab(int position);
    }
}
