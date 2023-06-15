package org.philosophism.openmhealth.api;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Event {
    public UUID id;
    public String title;
    public String description;
    public Uri image_url;
    public String image_description;
    public Event(UUID id, String title, String description, Uri image_url, String image_description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.image_description = image_description;
    }

    public Event(String title, String description) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.image_url = null;
        this.image_description = null;
    }
    public Event(JSONObject obj) throws JSONException {
        this.id = UUID.fromString(obj.getString("id"));
        this.title = obj.getString("title");
        this.description = obj.getString("description");
        this.image_url = Uri.parse(obj.getString("image_url"));
        this.image_description = obj.getString("image_description");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject newobj = new JSONObject();
        newobj.put("id", id.toString());
        newobj.put("title", title);
        newobj.put("description", description);
        newobj.put("image_url", image_url.toString());
        newobj.put("image_description", image_description);
        return newobj;
    }

    public Event(CalendarRecord calendar, Uri image_url, String image_description) {
        this.title = calendar.title;
        this.description = calendar.description;
        this.id = UUID.randomUUID();
        this.image_url = image_url;
        this.image_description = image_description;
    }
}
