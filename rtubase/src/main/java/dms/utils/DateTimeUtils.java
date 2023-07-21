package dms.utils;

import java.sql.Date;

public class DateTimeUtils {
    public static Date strToDate(String str) {
        if (str.trim().length() != 8) return null;
        try {
            return Date.valueOf(str.substring(4) + "-"
                    + str.substring(2, 4) + "-" + str.substring(0, 2));
        } catch (Exception e) {
            return null;
        }
    }
}
