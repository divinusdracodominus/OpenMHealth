package org.philosophism.openmhealth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class ParticipantData {
    UUID device_id;
    UUID participant_id;
    public ParticipantData(UUID device_id, UUID participant_id) {
        this.device_id = device_id;
        this.participant_id = participant_id;
    }
}
