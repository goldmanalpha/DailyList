package com.goldmanalpha.androidutility;

import java.util.function.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/2/12
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayHelper {


    public static <T> int IndexOf(T[] items, T match) {
        for (int i = 0; i < items.length; i++) {
            if (match.equals(items[i])) {
                return i;
            }
        }

        return -1;
    }

    public static <T> int IndexOfP(T[] items, Predicate<T> isMatch) {
        for (int i = 0; i < items.length; i++) {
            if (isMatch.test(items[i])) {
                return i;
            }
        }

        return -1;
    }

}
