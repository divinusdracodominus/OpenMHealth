package org.philosophism.openmhealth.api;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.philosophism.openmhealth.utils.Utils;

import java.util.UUID;

public class Event {
    public UUID id;
    public String title;
    public String description;
    public String image_url;
    public String image_description;
    public Event(UUID id, String title, String description, String image_url, String image_description) {
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
    public static Event fromJSON(JSONObject obj) throws JSONException {
        //this.id = UUID.fromString(obj.getString("id"));
        Event retval = null;
        String image_url = Utils.getJSONString(obj, "image_url");
        UUID id = Utils.getJSONUUIDOrNew(obj, "id");
        String image_description = Utils.getJSONString(obj, "image_description");
        if(obj.has("title") && obj.has("description")) {
            String title = obj.getString("title");
            String description = obj.getString("description");
            return new Event(id, title, description, image_url, image_description);
        }
        return null;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject newobj = new JSONObject();
        newobj.put("id", id.toString());
        newobj.put("title", title);
        newobj.put("description", description);
        newobj.put("image_url", image_url);
        newobj.put("image_description", image_description);
        return newobj;
    }

    public Event(CalendarRecord calendar, String image_url, String image_description) {
        this.title = calendar.title;
        this.description = calendar.description;
        this.id = UUID.randomUUID();
        this.image_url = image_url;
        this.image_description = image_description;
    }
}
