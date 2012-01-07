package com.com.goldmanalpha.dailydo.db;

public class DoableItemValueTable extends TableBase {
    public static final String TableName = "DoableItemValue";

    public DoableItemValueTable() {
        super(TableName);
    }

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

        if (newVersion == 7)
        {
            //sad assumption that max(id) will be last id holds 99% of the time
            String sql6= "CREATE VIEW ViewItemValueMax AS "
                    + " Select itemId, max(id) as valueId from " + TableName
                    + " group by itemId; "  ;
            return sql6;
        }

        return null;
    }

}
