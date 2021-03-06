package com.dailydo.db;

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
            super("any table name");
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
}
