package net.americanairguns.classifiedads;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonAdapter extends AsyncTask<String, Void, String>{

    private Context context;
    private ClassifiedsFragment.FragmentCallback fragmentCallback;
    private Boolean jsonSuccessful;
    private Integer count = 0;

    public JsonAdapter(Context context, ClassifiedsFragment.FragmentCallback fragmentCallback){
        this.context = context;
        this.fragmentCallback = fragmentCallback;
        this.jsonSuccessful = false;
    }

    public JsonAdapter(Context context) {
        this(context, null);
    }

    protected String doInBackground(String... strings) {
        return readJSONFeed(strings[0]);
    }

    protected void onPostExecute(String result) {
        if (fragmentCallback != null) {
            if (jsonSuccessful)
                fragmentCallback.onTaskDone();
            else
                fragmentCallback.onTaskFailed("Connection to internet failed.\nCould not download stream.");
        }
    }

    public String readJSONFeed(String URL) {
        return this.readJSONFeed(URL, true);
    }

    public String readJSONFeed(String URL, Boolean add2DB) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } else Log.e("JSON1", "Failed to download file : " + statusCode);
        } catch (ClientProtocolException cpe) {
            Log.e("JSON2", cpe.getLocalizedMessage());
            cpe.printStackTrace();
        } catch (IOException ioe) {
            Log.e("JSON3", ioe.getLocalizedMessage());
            Log.e("JSON3", ioe.getMessage());
            ioe.printStackTrace();
        }

        if (add2DB) {
            try {
                addJSONtoDB(Html.fromHtml(stringBuilder.toString().substring(4, stringBuilder.toString().length() - 3)).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
//        } else return stringBuilder.toString();
        } else return Html.fromHtml(stringBuilder.toString().substring(4, stringBuilder.toString().length() - 3)).toString();
        return stringBuilder.toString();
    }

    public void addJSONtoDB(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            Log.i("JSON4", "Number of surveys in feed: " + jsonArray.length());

            String logOutput = "";

            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();

            List<Integer> dbAdIds = Arrays.asList(dbAdapter.getAdIds());
            List<Integer> jsonAdIds = new ArrayList<Integer>();

            // get adId list from web
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonAdIds.add(jsonArray.getJSONObject(i).getInt("AdId"));
            }

            // check for new ads
            boolean newAds = jsonAdIds.isEmpty() || (dbAdIds.isEmpty() && !jsonAdIds.isEmpty()) || (jsonAdIds.size()>0 && jsonAdIds.get(0) > dbAdIds.get(0));

            // removes ads from the database that are not contained in the new set
            if (dbAdIds.size() > 0) {
                for (int adID : dbAdIds) {
                    if (!jsonAdIds.contains(adID)) {
                        dbAdapter.removeAdById(adID);
                        logOutput += String.valueOf(adID) + ",";
                    }
                }
            }

            if (!logOutput.equals("")) {
                Log.i("AD_REMOVED", logOutput);
                logOutput = "";
            }

            // add the new ads to the db
            if (newAds) {
                jsonArray = new JSONArray(readJSONFeed("http://www.airguns.net/classifieds/mobileAdapterDEV.php?Command=List&AdId=" + (dbAdIds.isEmpty() ? 0 : dbAdIds.get(0)), false));

                for (int adID : jsonAdIds) { // creates ads contained in the set that are not contained in the database
                    if (dbAdIds.size() == 0 || !dbAdIds.contains(adID)) {
                        dbAdapter.JSON2ROW(jsonArray.getJSONObject(jsonAdIds.indexOf(adID)));
                        logOutput += String.valueOf(adID) + ",";
                    }
                }
            }

            if (!logOutput.equals(""))
                Log.i("AD_CREATED", logOutput);

            dbAdapter.close();
            jsonSuccessful = true;
            System.gc();
        } catch (Exception e) {
            Log.e("JSON5", e.getMessage());
            e.printStackTrace();
        }
    }

    public interface JsonCallback {
        public void onTaskDone();
    }
}
