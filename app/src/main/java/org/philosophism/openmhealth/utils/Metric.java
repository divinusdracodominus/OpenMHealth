package org.philosophism.openmhealth.utils;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.util.Hex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.philosophism.openmhealth.DataManager;
import org.philosophism.openmhealth.MetaData;
import org.philosophism.openmhealth.api.DataRecord;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Metric {
    public String name;
    public String permission;
    public Uri uri;
    public String[] fields;
    public String[] required = new String[]{"date"};
    public String remoteurl = "https://example.com";
    public String usage = "This permission is used to collect engagement metrics please see docs for more details";
    public Metric(String name, String permission, String uri, String[] fields) {
        this.name = name;
        this.permission = permission;
        this.uri = Uri.parse(uri);
        this.fields = fields;
    }
    public Metric(String name, String permission, Uri uri, String[] fields, String[] required) {
        this.name = name;
        this.permission = permission;
        this.uri = uri;
        this.fields = fields;
        this.required = required;
    }

    public Metric(String name, String permission, String uri, String[] fields, String[] required) {
        this.name = name;
        this.permission = permission;
        this.uri = Uri.parse(uri);
        this.fields = fields;
        this.required = required;
    }

    public Metric(String name, String permission, Uri uri, String[] fields) {
        this.name = name;
        this.permission = permission;
        this.uri = uri;
        this.fields = fields;
    }

    public Metric(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.permission = object.getString("permission");
        String uristr = object.getString("uri");
        String required = object.getString("required");
        if(uristr == null) {
            uri = null;
        }else{
            uri = Uri.parse(uristr);
        }
        usage = object.getString("usage");
        JSONArray array = object.getJSONArray("fields");
        fields = new String[array.length()];
        for(int i = 0; i < fields.length; i++) {
            fields[i] = array.getString(i);
        }
    }
    JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", this.name);
        object.put("permission", this.permission);
        object.put("uri", this.uri);
        object.put("usage", this.usage);
        object.put("fields", this.fields);
        object.put("required", this.required);
        return object;
    }

    public static ArrayList<JSONObject> read_data(Cursor cursor, UUID metadata) throws JSONException {
        Log.i("OpenMHealth", "permission granted about to read messages");

        ArrayList<JSONObject> messages = new ArrayList();
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                String msgData = "";

                HashMap<String, String> msgMap = new HashMap();
                JSONObject json =new JSONObject();
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    String field = cursor.getColumnName(idx);
                    msgMap.put(field, cursor.getString(idx));
                    int type = cursor.getType(idx);
                    switch(type) {
                        case FIELD_TYPE_STRING:
                            json.put(cursor.getColumnName(idx), cursor.getString(idx));
                            break;
                        case FIELD_TYPE_INTEGER:
                            json.put(cursor.getColumnName(idx), cursor.getLong(idx));
                            break;
                        case FIELD_TYPE_FLOAT:
                            json.put(cursor.getColumnName(idx), cursor.getFloat(idx));
                            break;
                        case FIELD_TYPE_BLOB:
                            json.put(cursor.getColumnName(idx), cursor.getBlob(idx));
                            break;
                        case FIELD_TYPE_NULL:

                            break;
                    }
                    String fieldname = cursor.getColumnName(idx);
                    try {
                        if (fieldname.equals("number") || fieldname.equals("address")) {
                            MessageDigest digest = MessageDigest.getInstance("SHA-256");
                            byte[] hash = digest.digest(cursor.getString(idx).getBytes("UTF-8"));
                            json.put("recipient_id", Hex.bytesToStringLowercase(hash));
                        }
                    }catch(NoSuchAlgorithmException e) {
                        Log.i("MyDataManager", "error: " + e.getMessage());
                        e.printStackTrace();
                    }catch(UnsupportedEncodingException e) {
                        Log.i("MyDataManager", "error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                json.put("metadata", metadata.toString());
                json.put("id", UUID.randomUUID().toString());
                messages.add(json);
                // use msgData

            } while (cursor.moveToNext());


        } else {
            Log.i("OpenMHealth", "no sms to be found");
        }
        return messages;
    }
}
