package org.philosophism.openmhealth;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Metric {
    public String name;
    public String permission;
    public Uri uri;
    public String[] fields;
    public String remoteurl = "https://example.com";
    public String usage = "This permission is used to collect engagement metrics please see docs for more details";
    public Metric(String name, String permission, String uri, String[] fields) {
        this.name = name;
        this.permission = permission;
        this.uri = Uri.parse(uri);
        this.fields = fields;
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
        return object;
    }

}
