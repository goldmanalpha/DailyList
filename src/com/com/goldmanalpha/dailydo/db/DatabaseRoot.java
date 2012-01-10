package com.com.goldmanalpha.dailydo.db;

import android.content.Context;

public abstract class DatabaseRoot {
    protected static Context context;

    public static void setContext(Context contextIn) {
        context = contextIn;

    }
}
