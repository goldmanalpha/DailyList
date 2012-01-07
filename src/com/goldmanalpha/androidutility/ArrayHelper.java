package com.goldmanalpha.androidutility;

import com.android.internal.util.Predicate;
import com.com.goldmanalpha.dailydo.db.TableAdapterBase;

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
            if (isMatch.apply(items[i])) {
                return i;
            }
        }

        return -1;
    }

}
