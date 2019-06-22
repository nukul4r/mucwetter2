package net.nukular.mucwetter2.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;

public class DownloadGifTask extends AsyncTask<String, Void, GifDrawable> {
    protected GifDrawable doInBackground(String... urls) {
        String url = urls[0];
        GifDrawable gif = null;
        try {
            InputStream in = new URL(url).openStream();
            BufferedInputStream bin = new BufferedInputStream(in);
            gif = new GifDrawableBuilder().from(bin).build();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return gif;
    }
}