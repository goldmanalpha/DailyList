package com.goldmanalpha.dailydo.tests;

import android.content.Context;
import android.test.ActivityTestCase;
import com.com.goldmanalpha.dailydo.db.TableAdapterBase;
import com.goldmanalpha.dailydo.model.DoableItem;
import junit.framework.Test;
import junit.framework.TestCase;

import java.sql.Time;

public class TableAdapterBaseTest extends TestCase {


    public class TableAdapterBaseInstance extends TableAdapterBase<DoableItem>
    {

        public TableAdapterBaseInstance() {
            super( "any table name");
        }

        @Override
        public DoableItem get(int id) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    
    public void  testTimeConvertsToInt()
    {
        Time t = new Time(13, 25, 16);

        TableAdapterBase target = new TableAdapterBaseInstance();

        Integer result = target.TimeToInt(t);

        assertEquals((int) 132516,(int)  result );        
    }
    
    public void testIntConvertsToTime()
    {
        TableAdapterBase target = new TableAdapterBaseInstance();

        Time result = target.IntToTime(82515);

        assertEquals((int) 8, (int) result.getHours());
        assertEquals((int)25, (int)result.getMinutes());
        assertEquals((int)15, (int)result.getSeconds());
    }

    public void testTotalHours()
    {
        totalHourTest2(new Time(23,0,0), new Time(1, 0,0), 2.0f);
        totalHourTest2(new Time(23,30, 30), new Time(1, 0, 0), 1.5f );
    }
    
        void totalHourTest2(Time t1, Time t2, float  expected)
        {
            TableAdapterBase target = new TableAdapterBaseInstance();
            float  result = target.totalHours(target.TimeToInt(t1), target.TimeToInt(t2));

            assertEquals( expected, result);

            
        }
}
