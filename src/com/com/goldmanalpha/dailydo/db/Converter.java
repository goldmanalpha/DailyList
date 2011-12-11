package com.com.goldmanalpha.dailydo.db;


import android.content.res.Resources;
import com.goldmanalpha.dailydo.R;
import com.goldmanalpha.dailydo.model.UnitType;

import java.util.HashMap;
import java.util.Map;

//TODO: convert to reflection for handling enums
public final class Converter {

    private static final Resources r = Resources.getSystem();
    private static final HashMap<String, UnitType> unitTypeMap = new HashMap<String, UnitType>();
    private static String unitTypeUnset;

    static {
        InitUnitType();
        unitTypeUnset = r.getString(R.string.unittypeprompt);
    }

    private static final void InitUnitType() {

        unitTypeMap.put(r.getString(R.string.unittypeprompt), UnitType.unset);
        unitTypeMap.put(r.getString(R.string.unittypeDrops), UnitType.drops);
        unitTypeMap.put(r.getString(R.string.unittypeTsp), UnitType.tsp);
        unitTypeMap.put(r.getString(R.string.unittypeTime), UnitType.time);
        unitTypeMap.put(r.getString(R.string.unittypeTimeSpan2Times), UnitType.timeSpan);
        unitTypeMap.put(r.getString(R.string.unittypeCheck), UnitType.check);
        unitTypeMap.put(r.getString(R.string.unittypeCount), UnitType.count);
        unitTypeMap.put(r.getString(R.string.unittypeRelativeAmount), UnitType.relativeAmount);

    }

    public static final UnitType stringToUnitType(String unitType) {

        if (unitTypeMap.containsKey(unitType)) {
            return unitTypeMap.get(unitType);
        }

        return UnitType.unset;
    }

    public static final String unitTypeToString(UnitType unitType) {
        return MapLookup(unitTypeMap, unitType, unitTypeUnset);
    }

    //given a map, returns the key for the value
    static final <K, V> K MapLookup(HashMap<K, V> map, V value, K unsetValue) {

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue() == value)
                return entry.getKey();
        }

        return unsetValue;
    }


}
