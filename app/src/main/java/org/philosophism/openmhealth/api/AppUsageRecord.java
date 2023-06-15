package org.philosophism.openmhealth.api;

import android.app.usage.UsageEvents;

import org.json.JSONException;
import org.json.JSONObject;

public class AppUsageRecord {
    public long date;
    public String package_name;
    public String kind;
    public int platform_type;

    public static String android_type_to_string(int event_type) {
        String kind = new String();
        switch(event_type) {
            case UsageEvents.Event.ACTIVITY_PAUSED:
                kind = "PAUSED";
                break;
            case UsageEvents.Event.ACTIVITY_RESUMED:
                kind = "RESUMED";
                break;
            case UsageEvents.Event.ACTIVITY_STOPPED:
                kind = "STOPPED";
                break;
            case UsageEvents.Event.DEVICE_SHUTDOWN:
                kind = "SHUTDOWN";
                break;
            case UsageEvents.Event.DEVICE_STARTUP:
                kind = "STARTUP";
                break;
            case UsageEvents.Event.FOREGROUND_SERVICE_START:
                kind = "FOREGROUND_START";
                break;
            case UsageEvents.Event.FOREGROUND_SERVICE_STOP:
                kind = "FOREGROUND_STOP";
                break;
            case UsageEvents.Event.KEYGUARD_HIDDEN:
                kind = "KEYBOARD_HIDEEN";
                break;
            case UsageEvents.Event.KEYGUARD_SHOWN:
                kind = "KEYBOARD_SHOWN";
                break;
            case UsageEvents.Event.SCREEN_INTERACTIVE:
                kind = "SCREEN_INTERACTIVE";
                break;
            case UsageEvents.Event.SCREEN_NON_INTERACTIVE:
                kind = "SCREEN_NON_INTERACTIVE";
                break;
            case UsageEvents.Event.NONE:
                kind = "NONE";
                break;
            case UsageEvents.Event.USER_INTERACTION:
                kind = "USER_INTERACTION";
                break;
            case UsageEvents.Event.SHORTCUT_INVOCATION:
                kind = "SHORTCUT_INTERACTION";
                break;
            case UsageEvents.Event.STANDBY_BUCKET_CHANGED:
                kind = "STANDBY_BUCKET_CHANGED";
                break;
            default:
                kind = "UNRECOGNIZED";
        }
        return kind;
    }

    public AppUsageRecord(UsageEvents.Event currentEvent) {
        this.kind = android_type_to_string(currentEvent.getEventType());
        this.platform_type = currentEvent.getEventType();
        this.package_name = currentEvent.getPackageName();
        this.date = currentEvent.getTimeStamp();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject newobj = new JSONObject();
        newobj.put("package_name", this.package_name);
        newobj.put("kind", this.kind);
        newobj.put("platform_type", platform_type);
        newobj.put("date", this.date);
        return newobj;
    }
}
