package org.philosophism.openmhealth.api.contracts;

public class ActivityContract {
    public static final String TABLE_NAME = "activities";
    public static final String CONTENT_URI = "org.philosophism.openmeahlth.providers/activities";
    public static final String _ID = "_id";
    public static final String ID = "id";
    /// pulled from elapsed time which is measured in nano seconds for some reason
    public static final String DATE = "date";
    public static final String TRANSITION_TYPE = "transition_type";
    public static final String ACTIVITY = "activity";
    /// the numerical representation of this type, before its converted into a string to be stored in the database
    public static final String PLATFORM_ACTIVITY_TYPE = "platform_activity_type";
}
