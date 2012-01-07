package com.com.goldmanalpha.dailydo.db;


public class DoableItemTable extends TableBase {

    public static final String TableName="DoableItem";

    public DoableItemTable() {
        super(TableName);
    }

    // Database creation SQL statement
    @Override
    protected String databaseCreateSql() {
        return super.databaseCreateSql()
                + "name text not null, "
                + "unitType text not null, "
                + "description text null, "
                + "private integer "
                + ");";
    }

    @Override
    protected String databaseUpgradeSql(int newVersion) {

        if (newVersion == 10)
        {
            //on changing days, the order of the columns will be recalculated??
            String sql = "Alter Table " + TableName
                    + " add column displayOrder int;";

            return sql;
        }

        return null;
    }
}


