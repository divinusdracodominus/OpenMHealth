package org.philosophism.openmhealth.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.widget.ActionBarContextView;

import org.philosophism.openmhealth.db.ImagesDBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {

    SQLiteDatabase db;
    private String remote_url;
    private ImageView imageView;
    private String filename;

    public String resolveFilename(String filePath) {
        String retval;

        //db.beginTransaction();

        Cursor cursor = db.query(ImagesDBHelper.TABLE_NAME,
                new String[]{ImagesDBHelper.REMOTE_URL, ImagesDBHelper.LOCAL_URI, ImagesDBHelper.DATE},
                ImagesDBHelper.SELECT_BY_ID,
                new String[]{this.remote_url},
                null,
                null,
                null,
                null
        );

        int uri_index = cursor.getColumnIndex(ImagesDBHelper.LOCAL_URI);

        if(cursor.moveToFirst() && uri_index > -1) {
            retval = cursor.getString(uri_index);

        }else{
            retval = new File(filePath, UUID.randomUUID().toString() + ".png").getPath();
        }
        //db.setTransactionSuccessful();
        //db.endTransaction();

        return retval;
    }

    public ImageLoader(Context context, String url, ImageView imageView) {
        this.remote_url = url;
        this.imageView = imageView;

        File dir = new File(context.getFilesDir(), "image_cache");
        if(!dir.exists()){
            dir.mkdir();
        }
        String filePath = dir.getPath();

        db = new ImagesDBHelper(context).getWritableDatabase();
        this.filename = resolveFilename(filePath);
        Log.i("MyTag", "retval: " + this.filename);
    }

    private void writeDBEntry() {
        //db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(ImagesDBHelper.REMOTE_URL, this.remote_url);
        values.put(ImagesDBHelper.LOCAL_URI, this.filename);

        db.insert(ImagesDBHelper.TABLE_NAME, null, values);
        //db.setTransactionSuccessful();
        //db.endTransaction();
    }

    public static Bitmap crop(Bitmap bitmap) {
        if(bitmap == null) {
            return bitmap;
        }

        final int value;
        if (bitmap.getHeight() <= bitmap.getWidth()) {
            value = bitmap.getHeight();
        } else {
            value = bitmap.getWidth();
        }

        final Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, value, value);
        //final Bitmap lastBitmap = Bitmap.createScaledBitmap(finalBitmap, 144, 144, true);
        return finalBitmap;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        File source = new File(this.filename);

        try {
           if(source.exists()) {
                FileInputStream filestream = new FileInputStream(source);
                return crop(FileManager.readBitmapFromInputStream(filestream));
            }else{
                URL urlConnection = new URL(remote_url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = crop(BitmapFactory.decodeStream(input));

                if(source.createNewFile()) {
                    FileOutputStream out = new FileOutputStream(source);
                    FileManager.writeBitmapToOutputStream(out, myBitmap);
                    writeDBEntry();
                }else{
                    Log.i("ImageLoader", "failed to create source file for bitmap to be stored");
                }

                return crop(myBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }
}
