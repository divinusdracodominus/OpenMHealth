package org.philosophism.openmhealth.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileManager {
    public interface FileCallback {
        void run(Uri uri);
    }

    public static OutputStream getOutputStream(Context context, Uri filename) throws IOException {
        OutputStream file = context.getContentResolver().openOutputStream(filename);
        return file;
    }
    public static InputStream getInputStream(Context context, Uri filename) throws  IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(filename);
        return inputStream;
    }
    public static void writeToOutputStream(OutputStream file, String text) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);
        outputStreamWriter.write(text);
        outputStreamWriter.flush();

        outputStreamWriter.close();
    }

    public static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, bytesRead));
        }
        return stringBuilder.toString();
    }
    public static Bitmap readBitmapFromInputStream(InputStream inputStream) {
        Bitmap retval = BitmapFactory.decodeStream(inputStream);
        return retval;
    }
    public static void writeBitmapToOutputStream(OutputStream outputStream, Bitmap bitmap) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        outputStream.write(bitmapdata);
        outputStream.flush();
        outputStream.close();
    }
}
