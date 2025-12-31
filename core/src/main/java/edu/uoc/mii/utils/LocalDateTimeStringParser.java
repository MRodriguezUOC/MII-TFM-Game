package edu.uoc.mii.utils;

/**
 *
 * @author Marco Rodriguez
 */
public class LocalDateTimeStringParser {

    public static String format(String ldt) {
        // API send something like "2023-11-24T16:55:30.123"
        // Since GWT doesn't have a formatter, we clean it manually.
        String ret = ldt;
        if (ldt != null && ldt.contains("T")) {
            ret = ldt.replace("T", " ");
            if (ret.length() > 16) {
                ret = ret.substring(0, 16); // Result "2023-11-24 16:55"
            }
        }
        
        return ret;
    }
}
