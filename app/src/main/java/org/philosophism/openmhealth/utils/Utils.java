package org.philosophism.openmhealth.utils;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

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
}
