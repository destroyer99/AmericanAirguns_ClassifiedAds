package net.americanairguns.classifiedads.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import net.americanairguns.classifiedads.R;

public class CalculatorsFragment extends Fragment {

    public CalculatorsFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_calculators, container, false);
        final ScrollView scrollView = (ScrollView) layout.findViewById(R.id.scrollView3);

        final EditText energyWeight = (EditText) layout.findViewById(R.id.energyWeight);
        final EditText energyVelocity = (EditText) layout.findViewById(R.id.energyVelocity);
        final EditText energy = (EditText) layout.findViewById(R.id.energy);

        final EditText clicksMOA = (EditText) layout.findViewById(R.id.clicksMOA);
        final EditText poiDistance = (EditText) layout.findViewById(R.id.poiDistance);
        final EditText clicksDelta = (EditText) layout.findViewById(R.id.clicksDelta);
        final EditText poiDelta = (EditText) layout.findViewById(R.id.poiDelta);

        final EditText convYards = (EditText) layout.findViewById(R.id.convYds);
        final EditText convMeters = (EditText) layout.findViewById(R.id.convMeters);
        final EditText convFtSec = (EditText) layout.findViewById(R.id.velocityFPS);
        final EditText convMSec = (EditText) layout.findViewById(R.id.velocityMPS);

        View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (view.getId()==R.id.energyWeight || view.getId()==R.id.energyVelocity) {
                                        scrollView.smoothScrollTo(0, scrollView.getTop());
                                    } else if (view.getId()==R.id.clicksMOA || view.getId()==R.id.poiDistance || view.getId()==R.id.clicksDelta) {
                                        scrollView.smoothScrollTo(0, poiDelta.getBottom() + poiDelta.getMeasuredHeight());
                                    } else scrollView.smoothScrollTo(0, scrollView.getBottom());
                                }
                            });
                        }
                    }, 100);
                }
            }
        };

/////// Energy Calc ////////////////////////////////////////////////////////////////////////////////

        energyWeight.setOnFocusChangeListener(mOnFocusChangeListener);
        energyVelocity.setOnFocusChangeListener(mOnFocusChangeListener);

        energyWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!energyVelocity.getText().toString().isEmpty() && (start > 0 || (start == 0 && count > 0))) {
                    energy.setText(String.valueOf(
                                Double.valueOf(energyWeight.getText().toString()) *
                                Double.valueOf(energyVelocity.getText().toString()) *
                                Double.valueOf(energyVelocity.getText().toString()) / 450240));
                } else if (!energy.getText().toString().isEmpty()) energy.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        energyVelocity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!energyWeight.getText().toString().isEmpty() && (start > 0 || (start == 0 && count > 0))) {
                    energy.setText(String.valueOf(
                                Double.valueOf(energyWeight.getText().toString()) *
                                Double.valueOf(energyVelocity.getText().toString()) *
                                Double.valueOf(energyVelocity.getText().toString()) / 450240));
                } else if (!energy.getText().toString().isEmpty()) energy.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

/////// POI Calc ///////////////////////////////////////////////////////////////////////////////////

        clicksMOA.setOnFocusChangeListener(mOnFocusChangeListener);
        poiDistance.setOnFocusChangeListener(mOnFocusChangeListener);
        clicksDelta.setOnFocusChangeListener(mOnFocusChangeListener);

        clicksMOA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!poiDistance.getText().toString().isEmpty() && !clicksDelta.getText().toString().isEmpty() && (start > 0 || (start == 0 && count > 0))) {
                    poiDelta.setText(String.valueOf(
                                1.09 / Double.valueOf(clicksMOA.getText().toString()) /
                                (100 / Double.valueOf(poiDistance.getText().toString())) *
                                Double.valueOf(clicksDelta.getText().toString())));
                } else if (!poiDelta.getText().toString().isEmpty()) poiDelta.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        poiDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!clicksMOA.getText().toString().isEmpty() && ! clicksDelta.getText().toString().isEmpty() && (start > 0 || (start == 0 && count > 0))) {
                    poiDelta.setText(String.valueOf(
                                1.09 / Double.valueOf(clicksMOA.getText().toString()) /
                                (100 / Double.valueOf(poiDistance.getText().toString())) *
                                Double.valueOf(clicksDelta.getText().toString())));
                } else if (!poiDelta.getText().toString().isEmpty()) poiDelta.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        clicksDelta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!clicksMOA.getText().toString().isEmpty() && ! poiDistance.getText().toString().isEmpty() && (start > 0 || (start == 0 && count > 0))) {
                    poiDelta.setText(String.valueOf(
                                1.09 / Double.valueOf(clicksMOA.getText().toString()) /
                                (100 / Double.valueOf(poiDistance.getText().toString())) *
                                Double.valueOf(clicksDelta.getText().toString())));
                } else if (!poiDelta.getText().toString().isEmpty()) poiDelta.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        /////// Conversions ////////////////////////////////////////////////////////////////////////////////

        convYards.setOnFocusChangeListener(mOnFocusChangeListener);
        convMeters.setOnFocusChangeListener(mOnFocusChangeListener);
        convFtSec.setOnFocusChangeListener(mOnFocusChangeListener);
        convMSec.setOnFocusChangeListener(mOnFocusChangeListener);

        convYards.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (convYards.hasFocus()) {
                    if (start > 0 || (start == 0 && count > 0)) {
                        convMeters.setText(String.valueOf(0.9144 *
                                    Double.valueOf(convYards.getText().toString())));
                    } else convMeters.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        convMeters.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (convMeters.hasFocus()) {
                    if (start > 0 || (start == 0 && count > 0)) {
                        convYards.setText(String.valueOf(1.09361 *
                                    Double.valueOf(convMeters.getText().toString())));
                    } else convYards.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        convFtSec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (convFtSec.hasFocus()) {
                    if (start > 0 || (start == 0 && count > 0)) {
                        convMSec.setText(String.valueOf(0.3048 *
                                    Double.valueOf(convFtSec.getText().toString())));
                    } else convMSec.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        convMSec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (convMSec.hasFocus()) {
                    if (start > 0 || (start == 0 && count > 0)) {
                        convFtSec.setText(String.valueOf(3.28084 *
                                    Double.valueOf(convMSec.getText().toString())));
                    } else convFtSec.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        if (getActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE).getBoolean("calcDefVals", true)) {
            energyWeight.setText("10.5");
            energyVelocity.setText("850");
            clicksMOA.setText("4");
            poiDistance.setText("100");
            clicksDelta.setText("2");
        }
        return layout;
    }
}
