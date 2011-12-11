package com.goldmanalpha.dailydo;

import com.com.goldmanalpha.dailydo.db.Converter;
import com.goldmanalpha.dailydo.model.UnitType;
import junit.framework.TestCase;

public class ConverterTest extends TestCase{

    public void testCanConvertToUnitType()
    {
        assertEquals(UnitType.drops, Converter.stringToUnitType("drops"));
    }

    public void testCanConvertToString()
    {
        assertEquals("drops", Converter.unitTypeToString(UnitType.drops));

    }
}
