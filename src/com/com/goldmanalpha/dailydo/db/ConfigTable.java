package com.com.goldmanalpha.dailydo.db;

public class ConfigTable extends TableBase {

    public static final String TableName = "DailyDoConfig";

    public ConfigTable() {
        super(TableName);
    }

    // Database creation SQL statement
    @Override
    protected String databaseCreateSql() {
        return super.databaseCreateSql()
                + "key text not null UNIQUE, "
                + "value text "
                + ");";
    }

    @Override
    protected String[] databaseUpgradeSql(int newVersion) {

        if (newVersion == 17) {
            return new String[]{databaseCreateSql()};
        }

        return null;
    }


}


