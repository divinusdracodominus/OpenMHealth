package org.philosophism.openmhealth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MetaData {
    public UUID id;
    public UUID device_id;
    public UUID participant_id;
    public String source;
    public String type;
    public MetaData(UUID id, UUID device_id, UUID participant_id, String source, String type) {
        this.id = id;
        this.device_id = device_id;
        this.participant_id = participant_id;
        this.source = source;
        this.type = type;
    }

    public MetaData(UUID id, UUID device_id, UUID participant_id, String source) {
        this.id = id;
        this.device_id = device_id;
        this.participant_id = participant_id;
        this.source = source;
    }

    public MetaData(UUID id, UUID device_id, UUID participant_id) {
        this.id = id;
        this.device_id = device_id;
        this.participant_id = participant_id;
    }

    JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id.toString());
        obj.put("device_id", device_id.toString());
        obj.put("participant_id", participant_id.toString());
        obj.put("source", source);
        obj.put("type", type);
        return obj;
    }
}
