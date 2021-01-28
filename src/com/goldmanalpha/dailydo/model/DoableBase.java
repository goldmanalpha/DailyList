package com.goldmanalpha.dailydo.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DoableBase {

    int id;
    Date dateCreated;
    Date dateModified;

    DoableBase(int id) {
        this.id = id;
    }

    DoableBase() {
        this(0);
        setDateModified(new Date());
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


    @Override
    public String toString() {

        return String.format("%d", id);
    }
}
