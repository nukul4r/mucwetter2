package net.nukular.mucwetter2.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        try (InputStream in = new URL(url).openStream()) {
            BitmapFactory.Options options =new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(in, null, options);
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