package com.com.goldmanalpha.dailydo.db;

public class ItemSortingTableAdapter extends DatabaseRoot{


    public void clear(int instanceId)
    {
        String sql = "delete from ItemSortingTemp where instanceId = ? ";
        getDb().execSQL(sql, new Integer[]{instanceId});
    }

    public void setupValueSort(int itemId, int instanceId, boolean ascending)
    {
        clear(instanceId);

        String sql = "insert into ItemSortingTemp(instanceId, appliesToDate) "
        + "SELECT ?, appliesToDate FROM DoableItemValue "
                + "where itemId = ? "
                + "order by amount " + (ascending ? "ASC" : "DESC") + ", appliesToDate desc;";

        getDb().execSQL(sql, new Integer[]{instanceId, itemId});
    }






}
