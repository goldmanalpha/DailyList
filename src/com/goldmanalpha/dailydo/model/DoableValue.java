package com.goldmanalpha.dailydo.model;

import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;

import java.sql.Time;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import static com.goldmanalpha.androidutility.DateHelper.short24TimeFormat;

@Getter
@Setter
public class DoableValue extends DoableBase {
    private String description;

    //used as start or only time
    private Time fromTime;
    private Time toTime;

    private Float amount = 0f;

    private Date appliesToDate;
    private int doableItemId = 0;

    /* if there's more than one instance in the same day, show the time to differentiate
     *  sibling categories will reduce but not eliminate the use of this
     * */
    private boolean hasAnotherDayInstance = false;
    private Integer potency = 0;

    private TeaSpoons teaspoons = TeaSpoons.unset;
    private DoableItem item;

    private int category = 0;

    /**
     * can be null,
     * in which case caller might want to default to
     * crDate
     * <p>
     * when showing duplicates show time
     * TODO: rename - dupShowTime
     *
     * @return
     */
    private Time appliesToTime;

    public DoableValue(DoableValue copyItem) {
        super();

        this.setAppliesToDate(copyItem.getAppliesToDate());
        this.setTeaspoons(copyItem.teaspoons);
        this.setPotency(copyItem.potency);
        this.setDoableItemId(copyItem.getItem().getId());
    }

    public String valueDisplayString() {
        UnitType unitType = this.getItem().unitType;

        switch (unitType) {

            case unset:
                return "unset unit!";

            case drops:
                return String.format("%d @ p%d", this.amount, this.potency.intValue());

            case tsp:
                return String.format("%d %s tsp", this.amount.intValue(), TeaspoonHelper.shortName(this.teaspoons));

            case time:
                return String.format("%s", short24TimeFormat.format(this.fromTime));

            case timeSpan:
                return String.format("%s - %s", short24TimeFormat.format(this.fromTime), short24TimeFormat.format(this.toTime));

            case check:
                return String.format("%b", this.amount.intValue() != 0);

            case count:
            case relativeAmount:
                return String.format("%d @ ", this.amount.intValue());
        }

        return "unhandled unit!";
    }

    public DoableValue(int id) {
        super(id);
    }

    public DoableValue() {
    }

    public DoableItem getItem() {
        if (item == null) {
            item = new DoableItemTableAdapter().get(doableItemId);
        }

        return item;
    }
}