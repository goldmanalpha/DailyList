package com.goldmanalpha.dailydo.model;

import android.content.Context;

import java.util.Date;

public abstract class DoableBase {

    int id;
    Date dateCreated;
    Date dateModified;

    static Context context;

    public static void setContext(Context context) {
        DoableBase.context = context;
    }

    public int getId() {
        return id;
    }

    public Date getDateCreated() {

        if (dateCreated == null)
            dateCreated = new Date();

        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        if (this.dateCreated != null)
            throw new IllegalArgumentException("Created date cannot be reset");

        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    DoableBase(int id) {
        this.id = id;
    }

    DoableBase() {
        this(0);
        setDateCreated(new Date());
        setDateModified(new Date());
    }

    @Override
    public String toString() {

        return String.format("%d", id);
    }
}
