package net.americanairguns.classifiedads;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutionException;

// TODO: Remove from final release
public class DevelopmentFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Boolean DOWNLOAD = true;
    private static final Boolean UPLOAD = false;

    private static final String[] adNames = {"Ricky%20Bobby", "Jackie%20Moon", "Brennan%20Huff", "Ron%20Burgundy", "Steve%20Butabi"};
    private static final String[] adImgs = {"mobileAd1", "mobileAd2"};

    public DevelopmentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().findViewById(R.id.action_sort).setEnabled(false);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.INVISIBLE);

        getPreferenceManager().setSharedPreferencesName("appPreferences");
        addPreferencesFromResource(R.xml.fragment_development);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Preference updateWebDB = findPreference("updateWebDB");
        updateWebDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to update the web database?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                try {
                                    Toast.makeText(getActivity().getApplicationContext(), new WebDBAdapter().execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Password=" + getActivity().getResources().getString(R.string.mobilePassword) + "&Command=UpdateDB").get(), Toast.LENGTH_LONG).show();
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
                return false;
            }
        });

        Preference refreshDB = findPreference("deleteDB");
        refreshDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete the database?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                DBAdapter dbAdapter = new DBAdapter(getActivity());
                                dbAdapter.open();
                                dbAdapter.performExec("DELETE FROM classified_ads");
                                dbAdapter.performExec("DELETE FROM virtual");
                                dbAdapter.close();
                                Toast.makeText(getActivity().getApplicationContext(), "Database Deleted", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        Preference resetFirstRun = findPreference("firstRunBtn");
        resetFirstRun.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getPreferenceManager().getSharedPreferences().edit().putBoolean("firstRun", true).apply();
                Toast.makeText(getActivity().getApplicationContext(), "Status Reset", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        Preference ftpTest = findPreference("ftpTest");
        ftpTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to test the FTP?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                try {
                                    new FtpAdapter(getActivity(), "/public_html/classifieds/ftpTestFileDownload.txt", new File(Environment.getExternalStorageDirectory(), "ftpTestFileDownload.txt")).execute("ftp.airguns.net", "airgunsn", "hammerHead1!", DOWNLOAD).get();
                                    new FtpAdapter(getActivity(), "/public_html/classifieds/ftpTestFileUpload.txt", new File(Environment.getExternalStorageDirectory(), "ftpTestFileUpload.txt")).execute("ftp.airguns.net", "airgunsn", "hammerHead1!", UPLOAD).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        Preference uploadAd = findPreference("uploadAd");
        uploadAd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to upload a dummy ad?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                try {
                                    Toast.makeText(getActivity().getApplicationContext(), new WebDBAdapter().execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Password=" + getActivity().getResources().getString(R.string.mobilePassword) + "&Command=CreateAd&Name=" + adNames[(int) (System.currentTimeMillis() % 5)] + "&Image1=" + adImgs[0] + "&Image2=" + adImgs[1]).get(), Toast.LENGTH_LONG).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                dialog.dismiss();
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        Preference deleteAd = findPreference("deleteAd");
        deleteAd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                final EditText adNumber = new EditText(getActivity());
                adNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Insert the ad number of the ad to delete.")
                        .setView(adNumber)
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                try {
                                    Toast.makeText(getActivity().getApplicationContext(), new WebDBAdapter().execute("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Password=" + getActivity().getResources().getString(R.string.mobilePassword) + "&Command=DeleteAd&AdId=" + adNumber.getText().toString()).get(), Toast.LENGTH_LONG).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                dialog.dismiss();
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        ((ActivityCallback)getActivity()).CloseDrawer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.action_sort).setEnabled(true);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {}

    public interface ActivityCallback {
        public void CloseDrawer();
    }
}