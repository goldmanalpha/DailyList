package com.com.goldmanalpha.dailydo.db;

public class DoableItemValueTable extends TableBase {
    public static final String TableName = "DoableItemValue";

    @Override
    protected String databaseCreateSql() {
        return super.databaseCreateSql().replace("?", TableName)
                + "description text null, "
                + "fromTime integer, "
                + "toTime integer, "
                + "amount integer, "   //int affinity but can be float
                + "teaspoons text null" //tsp, tsp32 etc.
                + "appliesToDate TIMESTAMP NOT NULL, "
                + "itemId integer NOT NULL"
                + ");";
    }

    @Override
    protected String databaseUpgradeSql(int newVersion) {

        if (newVersion == 4) {

            String sql4 = "CREATE INDEX idx_item_date ON DoableItemValue (appliesToDate);";

            return sql4;
        }
        return null;
    }

}
