package com.euro16.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Guillaume on 22/04/2016.
 */
public class ImageFromUrl extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ImageFromUrl(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {

        String url = urls[0];
        Bitmap mIcon = null;

        try {
            mIcon = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            InputStream in = new java.net.URL(url).openStream();
//            mIcon = BitmapFactory.decodeStream(in);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
