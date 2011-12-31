package com.goldmanalpha.androidutility;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 12/31/11
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnumHelper {

    public static <T extends Enum<T>> String[] EnumNameToStringArray(T[] values) {

        return EnumNameToStringArray(values, 0);
    }

    public static <T extends Enum<T>> String[] EnumNameToStringArray(T[] values,
                          int skipFirst) {
        int i = 0;

        String[] result = new String[values.length - skipFirst];

        for (T value : values) {
            if (i >= skipFirst)
                result[i - skipFirst] = value.name();

            i++;
        }

        return result;
    }
}
