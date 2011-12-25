package com.com.goldmanalpha.dailydo.db;

public class DoableItemValueTable extends TableBase
{
    public static final String TableName="DoableItemValue";

    @Override
    protected String databaseCreateSql() {
        return super.databaseCreateSql().replace("?", TableName)
                + "unitType text not null, "
                + "description text null, "
                + "fromTime integer"
                + "toTime integer"
                + "amount integer"
                + ");";
    }

    @Override
    protected String databaseUpgradeSql(int newVersion)
    {
             if (newVersion == 3)
             {
                 return databaseCreateSql();
             }

            return null;
    }

}
