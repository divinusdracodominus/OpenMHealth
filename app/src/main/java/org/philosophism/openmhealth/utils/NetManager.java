package org.philosophism.openmhealth.utils;

import static java.lang.System.out;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetManager {
    public static String post(Context context, URL url, String data) throws IOException{
        String result = new String();
        if(NetManager.internet_enabled(context)) {

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();

            result = FileManager.readFromInputStream(urlConnection.getInputStream());
        }
        return result;
    }

    public static String get(Context context, URL url) throws IOException{
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.connect();
        return FileManager.readFromInputStream(connect.getInputStream());
    }

    public static LinkProperties getLinkProperties(Context context) {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        LinkProperties linkProperties = connectivityManager.getLinkProperties(currentNetwork);
        return linkProperties;
    }

    public static NetworkCapabilities getNetworkCapabilities(Context context) {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        return caps;
    }

    public static boolean internet_enabled(Context context) {
        NetworkCapabilities caps = getNetworkCapabilities(context);
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
