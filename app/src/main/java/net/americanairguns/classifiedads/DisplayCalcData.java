package net.americanairguns.classifiedads;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static java.lang.Math.E;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class DisplayCalcData extends Fragment {

    private final static String[] headers = new String[] {"Range\n(yds)", "Turret\nVal", "Clicks\nU/D", "Path\n(in)", "Drift\n(in)", "Velocity\n(fps)", "Energy\n(ft.lbs)", "Time\n(sec)", "Drop\n(in)"};
    private final static DecimalFormat df1 = new DecimalFormat("#.#");
    private final static DecimalFormat df2 = new DecimalFormat("#.##");

    private TableLayout tableLayout;
    private LayoutInflater inflater;

    public DisplayCalcData() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_calc_data, container, false);
        this.inflater = inflater;
        this.tableLayout = (TableLayout)view.findViewById(R.id.tableView);
        df1.setRoundingMode(RoundingMode.HALF_UP);
        df2.setRoundingMode(RoundingMode.HALF_UP);
        return view;
    }

    public void updateTable(double pW, double pBC, double pMV, double sHAB,
                         int sMOA, int sC, int rZ, int rS, int rE, int rI,
                         int wS, int wA, boolean useOldBushnellTurret) {

        tableLayout.removeAllViewsInLayout();
        View tableRow = inflater.inflate(R.layout.trajectory_table_row, null, false);

        ((TextView) tableRow.findViewById(R.id.tv1)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv2)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv3)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv4)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv5)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv6)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv7)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv8)).setTypeface(null, Typeface.BOLD);
        ((TextView) tableRow.findViewById(R.id.tv9)).setTypeface(null, Typeface.BOLD);

        ((TextView) tableRow.findViewById(R.id.tv1)).setText(headers[0]);
        ((TextView) tableRow.findViewById(R.id.tv2)).setText(headers[1]);
        ((TextView) tableRow.findViewById(R.id.tv3)).setText(headers[2]);
        ((TextView) tableRow.findViewById(R.id.tv4)).setText(headers[3]);
        ((TextView) tableRow.findViewById(R.id.tv5)).setText(headers[4]);
        ((TextView) tableRow.findViewById(R.id.tv6)).setText(headers[5]);
        ((TextView) tableRow.findViewById(R.id.tv7)).setText(headers[6]);
        ((TextView) tableRow.findViewById(R.id.tv8)).setText(headers[7]);
        ((TextView) tableRow.findViewById(R.id.tv9)).setText(headers[8]);

        tableLayout.addView(tableRow);

        Double Qz = 34500 * pBC * (pow(Math.E, (rZ / (11500 * pBC))) - 1) / pMV;
        Double X = ((193 * pow(Qz, 2)) + sHAB) / rZ;
        Double wA_radians = wA * Math.PI / 180.0;

        Double velocity, time, drop, Qt, clicksValue;
        Float path, energy, drift;
        Integer clicks, turretValWhole, turretValRemainder;
        String oneRevolution;

        for (int rY = rS; rY <= rE; rY += rI) {
            velocity = pMV / pow(E, (rY / (8000 * pBC)));
            time = 24000 * pBC * (pow(E, (rY / (8000 * pBC))) - 1) / pMV;
            drop = (1 + (2 * sqrt(velocity / pMV))) * 64.32 * pow(time, 2);
            Qt = 34500 * pBC * (pow(E, (rY / (11500 * pBC))) - 1) / pMV;
            path = (float) ((rY * X) - (193 * pow(Qt, 2)) - sHAB);
            energy = (float) ((pW * velocity * velocity) / 450240);

            if (wA == 0)
                drift = (float) 0.0;
            else if (wA == 90)
                drift = (float) (17.6 * wS * (time - ((double) (rY * 3) / pMV)));
            else
                drift = (float) (17.6 * wS * (time - ((double) (rY * 3) / pMV)) * sin(wA_radians));

            clicksValue = 1.0472 / sMOA;
            clicks = (int) Math.round((100 * -1 * path) / (rY * clicksValue));

            oneRevolution = " ";
            if (useOldBushnellTurret) {
                if (clicks >= sC) {
                    turretValWhole = (int) ((float) (clicks - sC) / (float) 2.0);
                    turretValRemainder = (clicks - sC) % 2;
                    oneRevolution = "+";
                } else if (clicks < 0) {
                    turretValWhole = abs((int) ((float) (clicks - sC) / (float) 2.0));
                    turretValRemainder = abs((clicks - sC) % 2);
                    oneRevolution = "-";
                } else {
                    turretValWhole = (int) ((float) clicks / (float) 2.0);
                    turretValRemainder = clicks % 2;
                }
            } else {
                if (clicks >= sC) {
                    turretValWhole = abs((int) ((float) (clicks - sC) / (float) sMOA));
                    turretValRemainder = abs((clicks - sC) % sMOA);
                    oneRevolution = "+1,";
                } else if (clicks < 0) {
                    turretValWhole = abs((int) ((float) (clicks + sC) / (float) sMOA));
                    turretValRemainder = abs((clicks + sC) % sMOA);
                    oneRevolution = "-1,";
                } else {
                    turretValWhole = abs((int) ((float) clicks / (float) sMOA));
                    turretValRemainder = abs(clicks % sMOA);
                    oneRevolution = "";
                }
            }

            tableRow = inflater.inflate(R.layout.trajectory_table_row, null, false);

            if (rY % 5 == 0) {
                ((TextView) tableRow.findViewById(R.id.tv1)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv2)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv3)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv4)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv5)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv6)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv7)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv8)).setTypeface(null, Typeface.BOLD);
                ((TextView) tableRow.findViewById(R.id.tv9)).setTypeface(null, Typeface.BOLD);

                ((TextView) tableRow.findViewById(R.id.tv1)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv2)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv3)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv4)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv5)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv6)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv7)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv8)).setTextColor(Color.WHITE);
                ((TextView) tableRow.findViewById(R.id.tv9)).setTextColor(Color.WHITE);
            }

            ((TextView) tableRow.findViewById(R.id.tv1)).setText(String.valueOf(rY));
            ((TextView) tableRow.findViewById(R.id.tv2)).setText(oneRevolution + String.valueOf(turretValWhole) + ":" + String.valueOf(turretValRemainder));
            ((TextView) tableRow.findViewById(R.id.tv3)).setText(String.valueOf(clicks));
            ((TextView) tableRow.findViewById(R.id.tv4)).setText(df2.format(path));
            ((TextView) tableRow.findViewById(R.id.tv5)).setText(df2.format(drift));
            ((TextView) tableRow.findViewById(R.id.tv6)).setText(df1.format(velocity));
            ((TextView) tableRow.findViewById(R.id.tv7)).setText(df2.format(energy));
            ((TextView) tableRow.findViewById(R.id.tv8)).setText(df2.format(time));
            ((TextView) tableRow.findViewById(R.id.tv9)).setText(df2.format(drop));

            tableLayout.addView(tableRow);
        }
    }
 }
