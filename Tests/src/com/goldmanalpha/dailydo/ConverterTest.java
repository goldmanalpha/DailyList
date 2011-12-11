package com.goldmanalpha.dailydo;

import android.test.ActivityTestCase;
import android.test.mock.MockContext;
import com.com.goldmanalpha.dailydo.db.Converter;
import com.goldmanalpha.dailydo.model.UnitType;
import junit.framework.TestCase;

public class ConverterTest extends ActivityTestCase{

    public void testCanConvertToUnitType()
    {
        assertEquals(UnitType.drops, Converter.stringToUnitType(getInstrumentation().getTargetContext(), "drops"));
    }

    public void testCanConvertToString()
    {
        MockContext context = new MockContext();

        assertEquals("drops", Converter.unitTypeToString(getInstrumentation().getTargetContext(), UnitType.drops));

    }
}
