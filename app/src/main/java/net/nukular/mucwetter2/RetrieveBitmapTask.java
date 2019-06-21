package net.nukular.mucwetter2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.URL;

class RetrieveBitmapTask extends AsyncTask<String, Void, Bitmap> {


    private Exception exception;

    protected Bitmap doInBackground(String... urls) {
        try {
            URL url = new URL("https://www.dwd.de/DWD/warnungen/agrar/wbx/wbx_stationen.png");
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(Bitmap bitmap) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}