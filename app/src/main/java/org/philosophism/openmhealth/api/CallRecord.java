package org.philosophism.openmhealth.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.philosophism.openmhealth.api.DataRecord;

import java.sql.Date;

public class CallRecord implements DataRecord{
    public String id;
    public long date;
    public String metadata;
    public long duration;
    public String recipient_id;

    public CallRecord() {}
    public long getDate() {
        return this.date;
    }
    public String getId() {
        return this.id;
    }
    public Date getFormatedDate() {
        Date retval = new Date(this.date);
        return retval;
    }
    public String getMetaDataId() {
        return this.metadata;
    }
}
