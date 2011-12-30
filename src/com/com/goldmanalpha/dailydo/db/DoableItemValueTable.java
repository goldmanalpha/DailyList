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
                + "teaspoons text null," //tsp, tsp32 etc.
                + "appliesToDate TIMESTAMP NOT NULL, "
                + "itemId integer NOT NULL, "
                + "previousValueId integer "
                + ");";
    }

    @Override
    protected String databaseUpgradeSql(int newVersion) {

        if (newVersion == 4) {

            String sql4 = "CREATE INDEX idx_applies_to_date ON DoableItemValue (appliesToDate);"
                    + "CREATE INDEX idx_item_date ON DoableItemValue (itemId, appliesToDate desc, dateCreated desc, id);";

            return sql4;
        }
        return null;
    }

}
