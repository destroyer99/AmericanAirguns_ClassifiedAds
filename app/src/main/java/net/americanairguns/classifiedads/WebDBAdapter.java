package net.americanairguns.classifiedads;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebDBAdapter extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        return updateWebDB(strings[0]);
    }

    public WebDBAdapter() {}

    protected void onPostExecute(String result) {}

    public String updateWebDB(String URL) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        String webUpdateStatus;
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                webUpdateStatus = Html.fromHtml(reader.readLine()).toString();
            } else {
                webUpdateStatus = "Error: " + statusCode;
            }
        } catch (ClientProtocolException cpe) {
            webUpdateStatus = "ERROR:" + cpe.getLocalizedMessage();
            cpe.printStackTrace();
        } catch (IOException ioe) {
            webUpdateStatus = "ERROR:" + ioe.getLocalizedMessage();
            ioe.printStackTrace();
        }
        Log.i("webDbUpdate", webUpdateStatus);
        return webUpdateStatus.replaceAll("(\\r|\\n)", "");
    }
}
