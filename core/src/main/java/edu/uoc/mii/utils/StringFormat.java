package edu.uoc.mii.utils;

/**
 *
 * @author Marco Rodriguez
 */
public class StringFormat {
    
    public static String numberZeroLeftPad(int n, int maxPad){
        StringBuilder sb = new StringBuilder(maxPad);
        for(int i=0; i< maxPad; i++){
            sb.append("0");
        }
        
        return sb.append(n).substring(sb.length() - maxPad);        
    }
    
    public static String pad(String pattern, int max, String src){
        String ret = pattern + src;
        return ret.substring(ret.length() - max);
    }

    public static String pad(String pattern, String src){
        return pad(pattern, pattern.length(), src);
    }
    
    public static String intLeftPad(String pattern, int n){
        return pad(pattern, Integer.toString(n));
    }
}
