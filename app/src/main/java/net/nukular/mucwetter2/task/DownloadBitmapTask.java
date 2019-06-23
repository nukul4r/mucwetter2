package net.nukular.mucwetter2.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

public class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
    private final ImageView view;
    private final View spinner;
    private Exception exception;

    public DownloadBitmapTask(ImageView view, View spinner) {
        this.view = view;
        this.spinner = spinner;
    }

    protected Bitmap doInBackground(String... urls) {
        exception = null;
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            exception = e;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        spinner.setVisibility(View.INVISIBLE);
        if (exception != null) {
            Toast.makeText(view.getContext(), "Download failed", Toast.LENGTH_SHORT).show();
        }
        view.setImageBitmap(bitmap);
        view.setVisibility(View.VISIBLE);
    }
}