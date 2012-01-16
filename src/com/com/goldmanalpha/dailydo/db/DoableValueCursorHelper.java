package com.com.goldmanalpha.dailydo.db;

import android.database.Cursor;
import com.goldmanalpha.dailydo.model.TeaSpoons;
import com.goldmanalpha.dailydo.model.UnitType;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/15/12
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoableValueCursorHelper {

    final String usesTeaspoonsType = UnitType.tsp.toString();
    int unitTypeColIdx;
    int teaspoonColIdx;

    /**
     * @param cursor: only used to get column indices
     */
    public DoableValueCursorHelper(Cursor cursor) {
        unitTypeColIdx = cursor.getColumnIndex(DoableItemValueTableAdapter.ColUnitType);
        teaspoonColIdx = cursor.getColumnIndex(DoableItemValueTableAdapter.ColTeaspoons);

    }

    /**
     * 0, 1, or 2 times will show depending on unit type
     *
     * @param cursor
     * @return
     */
    public int timesToShowDate(Cursor cursor) {
        UnitType unitType = unitType(cursor);
        return unitType == UnitType.timeSpan ? 2 : (unitType == UnitType.time ? 1 : 0);
    }

    public UnitType unitType(Cursor cursor) {
        String unitType = cursor.getString(unitTypeColIdx);
        return UnitType.valueOf(unitType);
    }


    public boolean isTeaspoons(Cursor cursor) {

        return cursor.getString(unitTypeColIdx).equals(usesTeaspoonsType);
    }

    public String getTeaspoons(Cursor c) {
        return c.getString(this.teaspoonColIdx);
    }

}
