package com.com.goldmanalpha.dailydo.db;

public class ItemSortingTable extends TableBase{

    public ItemSortingTable() {
        super("unused");
    }

    @Override
    protected String databaseCreateSql() {
        return null;
    }

    private String[] createTableSql()
    {

        return new String[]{ "create table if not exists ItemSortingTemp "
                + "(orderId integer primary key, "
                + "instanceId integer, "
                + "appliesToDate  TIMESTAMP NOT NULL);"
                ,
                "create unique index if not exists uqItemSorting on ItemSortingTemp "
                + "(instanceId, orderId);"};
    }
    @Override
    protected String[] databaseUpgradeSql(int newVersion) {

        if (newVersion == 18) {
            return createTableSql();
        }

        return null;
    }
}
