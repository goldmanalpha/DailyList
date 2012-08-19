package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableBase;

import java.util.Date;

public class ConfigAdapter extends DatabaseRoot {

    public static final ConfigAdapter Config = new ConfigAdapter();

    public String getValue(String key, String defaultValue) {
        GetConfig cfg = new GetConfig(key).invoke();
        if (!cfg.getHasValue()) return defaultValue;
        return cfg.getCursor().getString(0);
    }

    public Boolean getBool(String key, Boolean defaultValue) {
        GetConfig cfg = new GetConfig(key).invoke();
        if (!cfg.getHasValue()) return defaultValue;
        return cfg.getCursor().getInt(0) == 1;
    }

    public Boolean getHasValue(String key) {
        GetConfig cfg = new GetConfig(key).invoke();
        return cfg.getHasValue();
    }

    //true for success
    public Boolean saveBool(String key, Boolean value) {
        ContentValues cv = createContentValues();
        cv.put("value", value ? 1 : 0);
        return save(key, cv);
    }

    private Boolean save(String key, ContentValues cv) {
        open();
        if (!getHasValue(key)) {
            return db.insert(ConfigTable.TableName, null, cv) != -1;
        } else {
            int rows = db.update(ConfigTable.TableName, cv, "key = '" + key + "'", null);
            return rows == 1;
        }
    }

    protected ContentValues createContentValues() {
            ContentValues values = new ContentValues();
            values.put("dateModified", DateToTimeStamp(new Date()));
            return values;
        }



    private class GetConfig {
        private boolean hasValue;
        private String key;
        private Cursor cursor;

        public GetConfig(String key) {
            this.key = key;
        }

        boolean getHasValue() {
            return hasValue;
        }

        public Cursor getCursor() {
            return cursor;
        }

        public GetConfig invoke() {
            open();

            cursor = db.rawQuery("select key from " + ConfigTable.TableName
                    + " where key = ?",
                    new String[]{key});

            if (!cursor.moveToFirst()) {
                hasValue = false;
                return this;
            }
            hasValue = true;
            return this;
        }
    }
}
