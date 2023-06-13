package org.philosophism.openmhealth.api;

import java.sql.Date;

public class SMSRecord implements DataRecord {
    public long date;
    public int thread_id;
    public long date_sent;
    public String number;
    public String recipient_id;
    public long duration;
    public String metadata;
    public String id;

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
