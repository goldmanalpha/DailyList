package com.goldmanalpha.dailydo;

import com.goldmanalpha.dailydo.model.DoableBase;
import com.goldmanalpha.dailydo.model.DoableItem;
import junit.framework.TestCase;

import java.util.Date;

public class DoableBaseTest extends TestCase{

    public void testCanSetDateCreated()
    {

        DoableBase b = new DoableItem();

        Date d = new Date();

        b.setDateCreated(new Date());

        assertEquals(d.toString(), b.getDateCreated().toString());
    }

    public void testCantSetDateCreatedTwice()
    {
        DoableBase b = new DoableItem();

        b.setDateCreated(new Date());

        try {
            b.setDateCreated(new Date());
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    public void testDateCreatedSetsItself() throws InterruptedException {

        Date start = new Date();

        Thread.sleep(10);

        DoableBase b = new DoableItem();

        Date createdDate = b.getDateCreated();

        assertNotNull(createdDate);

        assertTrue(
                String.format(
                        "createdDate should be set after ctor start %s created %s",
                        start.toString(), createdDate.toString()),
                createdDate.compareTo(start) > 0);


    }

}
