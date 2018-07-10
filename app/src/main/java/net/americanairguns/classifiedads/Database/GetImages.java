package net.americanairguns.classifiedads.Database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GetImages extends AsyncTask<String, Void, Bitmap>{

    ImageView bmImage;
    Integer scaledSizeX;

    public GetImages(ImageView bmImage, Integer scaledSizeX) {
        this.bmImage = bmImage;
        this.scaledSizeX = scaledSizeX;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return getBitmapFromURL(strings[0]);
    }

    protected void onPostExecute(Bitmap result) {
        if (result == null) {
            bmImage.setImageBitmap(null);
        } else if (result.getWidth() > scaledSizeX) {
            bmImage.setImageBitmap(getScaledBitmap(result, scaledSizeX));
        } else bmImage.setImageBitmap(getScaledBitmap(result, scaledSizeX));
    }

    public Bitmap getBitmapFromURL(String src) {
        Bitmap bitmap = null;
        try {
            InputStream input = new URL("http://www.airguns.net/classifieds" +
                    src.replaceFirst(".", "").replaceAll(" ", "%20")).openStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) { Log.e("BITMAP", e.getLocalizedMessage()); }

        return bitmap;
    }

    public static Bitmap getScaledBitmap(Bitmap b, int reqWidth) {
        return Bitmap.createScaledBitmap(b, reqWidth, (int)((float)b.getHeight() * (float)reqWidth / (float)b.getWidth() ), true);
    }
}
