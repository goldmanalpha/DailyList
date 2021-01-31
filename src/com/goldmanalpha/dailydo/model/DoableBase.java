package com.goldmanalpha.dailydo.model;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public LocalDateTime getLocalDateCreated() {
        return getDateCreated().toInstant()
                .atZone(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime();
    }

    public Time getLocalTimeCreated() {
        LocalDateTime utcCreated = getLocalDateCreated();

        ZonedDateTime localCreated = utcCreated.atZone(ZoneId.systemDefault());

        int hours = localCreated.getHour();
        int minutes = localCreated.getMinute();
        int seconds = localCreated.getSecond();

        return new Time(hours, minutes, seconds);
    }

    public void setDateCreated(Date dateCreated) {
        if (this.dateCreated != null)
            throw new IllegalArgumentException("Created date cannot be reset");

        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {

        return String.format(Locale.getDefault(), "%d", id);
    }
}
