package org.philosophism.openmhealth.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

public class Utils {
    public static String getJSONString(JSONObject obj, String field) throws JSONException {
        if(!obj.has(field)) {
            return null;
        }
        return obj.getString(field);
    }

    public static Uri getJSONUri(JSONObject obj, String field) throws JSONException {
        if(!obj.has(field)) {
            return null;
        }
        return Uri.parse(obj.getString(field));
    }

    public static UUID getJSONUUIDOrNew(JSONObject obj, String field) throws JSONException {
        if(!obj.has(field)) {
            return UUID.randomUUID();
        }
        return UUID.fromString(obj.getString(field));
    }

    public static String[] getField(String field, String defaultValue, ArrayList<JSONObject> objects) throws JSONException {
        String[] output = new String[objects.size()];
        for(int i = 0; i < objects.size(); i++) {
            String value = getJSONString(objects.get(i), field);
            if(value == null) {
                output[i] = defaultValue;
            }else{
                output[i] = value;
            }
        }
        return output;
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static class Chart {
        public String title;
        public String svg;
        public int width;
        public int height;
        String description;
        Bitmap image;
        Chart(String svg, int width, int height) throws SVGParseException {
            this.svg = svg;
            SVG parser = SVG.getFromString(this.svg);
            this.createChart(parser, width, height);
        }

        private void createChart(SVG parser, int width, int height) {
            this.width = width;
            this.height = height;
            Bitmap map = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas draw = new Canvas(map);
            draw.drawRGB(0,0,0);
            parser.renderToCanvas(draw);
            this.image = map;
            this.title = parser.getDocumentTitle();
            this.description = parser.getDocumentDescription();
        }

        Chart(InputStream is, int width, int height) throws SVGParseException, java.io.IOException {
            String data = FileManager.readFromInputStream(is);
            this.svg = data;
            SVG parser = SVG.getFromString(data);
            createChart(parser, width, height);
        }

        @Override
        public int hashCode() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(this.svg.getBytes("UTF-8"));
                return getLittleEndian(hash);
            }catch (Exception e) {
                return super.hashCode();
            }
        }
    }

    public static int getLittleEndian(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }

    /// use SHA-256 to hash the string returning the UUID representation of the hashed bytes
    /// this gives substance to UUID so the UUID can be determined by content, and is not arbitrary
    public static UUID hashUUID(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(value.getBytes("UTF-8"));

        ByteBuffer byteBuffer = ByteBuffer.wrap(hash);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
