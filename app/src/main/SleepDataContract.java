public class SleepDataContract {
    public static final String CONTENT_AUTHORITY = "org.philosophism.openmhealth.sleepdata";
    public static final Uri BASE_CONTENT_URI = "content://openmhealth";

    public static final class SleepEntry implements BaseColumns {
        public static final String TABLE_NAME = "sleepdata";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TIME = "duration";
        public static final String COLUMN_TIME = "end_time";
        public static final String COLUMN_TIME = "time";
        public static final Uri CONTENT_URI = Uri.parse("content://openmhealth/sleepdata");

    }
}
