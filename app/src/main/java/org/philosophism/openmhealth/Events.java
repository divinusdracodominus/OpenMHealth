package org.philosophism.openmhealth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.UUID;

public class Events {
    public UUID id;
    public String name;
    public String description;
    public String organizer;
    public String organization;
    public EventInstance[] instances;

    class EventInstance {
        UUID id;
        Timestamp datetime;
        UUID eventid;
        public JSONObject toJson() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("datetime", datetime);
            obj.put("eventid", eventid);
            return obj;
        }
        public EventInstance(JSONObject data) throws JSONException {
            this.id = UUID.fromString(data.getString("id"));
            this.eventid = UUID.fromString(data.getString("eventid"));
            this.datetime = new Timestamp(data.getLong("datetime"));
        }
    }

    Events(JSONObject data) throws JSONException {

    }
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("description", description);
        obj.put("id", id);
        obj.put("organizer", organizer);
        obj.put("organization", organization);
        JSONArray instance_list = new JSONArray(this.instances.length);
        for(int i = 0; i < this.instances.length; i++) {
            instance_list.put(this.instances[i].toJson());
        }
        return obj;
    }
}
