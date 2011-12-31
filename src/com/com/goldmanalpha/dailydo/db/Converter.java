package com.com.goldmanalpha.dailydo.db;


import android.content.Context;
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
    private static boolean unitTypeInitialized;

    private static final void InitUnitType(Context context) {

        if (unitTypeInitialized)
            return;

        unitTypeMap.put(context.getString(R.string.unittypeprompt), UnitType.unset);
        unitTypeMap.put(context.getString(R.string.unittypeDrops), UnitType.drops);
        unitTypeMap.put(context.getString(R.string.unittypeTsp), UnitType.tsp);
        unitTypeMap.put(context.getString(R.string.unittypeTime), UnitType.time);
        unitTypeMap.put(context.getString(R.string.unittypeTimeSpan2Times), UnitType.timeSpan);
        unitTypeMap.put(context.getString(R.string.unittypeCheck), UnitType.check);
        unitTypeMap.put(context.getString(R.string.unittypeCount), UnitType.count);
        unitTypeMap.put(context.getString(R.string.unittypeRelativeAmount), UnitType.relativeAmount);
        unitTypeUnset = context.getString(R.string.unittypeprompt);
    }

    public static final UnitType stringToUnitType(Context context, String unitType) {

        InitUnitType(context);
        
        if (unitTypeMap.containsKey(unitType)) {
            return unitTypeMap.get(unitType);
        }

        return UnitType.unset;
    }

    public static final String unitTypeToString(Context context, UnitType unitType) {
        InitUnitType(context);
        
        return MapLookup(unitTypeMap, unitType, unitTypeUnset);
    }

    //given a map, returns the key for the value
    static final <K, V> K MapLookup(HashMap<K, V> map, V value, K unsetValue) {

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value))
                return entry.getKey();
        }

        return unsetValue;
    }


}
