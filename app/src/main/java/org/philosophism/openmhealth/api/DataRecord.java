package org.philosophism.openmhealth.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;

public interface DataRecord {
    String getId();
    String getMetaDataId();
    long getDate();
    Date getFormatedDate();
    //JSONObject anonymize() throws JSONException;
    //JSONObject toJson() throws  JSONException;
}
