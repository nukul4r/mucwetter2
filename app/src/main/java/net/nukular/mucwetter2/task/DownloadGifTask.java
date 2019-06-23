package net.nukular.mucwetter2.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.GifImageView;

public class DownloadGifTask extends AsyncTask<String, Void, GifDrawable> {
    private final GifImageView view;
    private final View spinner;
    private Exception exception;

    public DownloadGifTask(GifImageView view, View spinner) {
        this.view = view;
        this.spinner = spinner;
    }

    protected GifDrawable doInBackground(String... urls) {
        exception = null;
        String url = urls[0];
        GifDrawable gif = null;
        try {
            InputStream in = new URL(url).openStream();
            BufferedInputStream bin = new BufferedInputStream(in);
            gif = new GifDrawableBuilder().from(bin).build();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            exception = e;
        }
        return gif;
    }

    @Override
    protected void onPostExecute(GifDrawable gifDrawable) {
        spinner.setVisibility(View.INVISIBLE);
        if (exception != null) {
            Toast.makeText(view.getContext(), "Download failed", Toast.LENGTH_SHORT).show();
            return;
        }
        view.setImageDrawable(gifDrawable);
        view.setVisibility(View.VISIBLE);
    }
}