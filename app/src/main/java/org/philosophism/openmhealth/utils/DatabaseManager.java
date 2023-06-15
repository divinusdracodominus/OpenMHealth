package org.philosophism.openmhealth.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.philosophism.openmhealth.db.ImagesDBHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private AtomicInteger imagesCounter = new AtomicInteger();
    private AtomicInteger sleepCounter = new AtomicInteger();

    private static DatabaseManager instance;
    private static ImagesDBHelper imagesHelper;
    private SQLiteDatabase imagesDatabase;

    public static synchronized void initializeAll(Context context) {
        if(instance == null) {
            instance = new DatabaseManager();
            imagesHelper = new ImagesDBHelper(context);
        }
    }

    /*public static synchronized void initializeInstance(ImagesDBHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            databaseHelper = helper;
        }
    }*/

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openImagesDatabase() {
        if(imagesCounter.incrementAndGet() == 1) {
            imagesDatabase = imagesHelper.getWritableDatabase();
        }
        return imagesDatabase;
    }

    public synchronized void closeImagesDatabase() {
        if(imagesCounter.decrementAndGet() == 0) {
            imagesDatabase.close();
        }
    }
}
