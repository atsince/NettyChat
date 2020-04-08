package com.freddy.im;

/**
 * Create by liyong on 2020-04-07
 * desc:
 */
public class StringUtil {
    public static boolean isNullOrEmpty(String text){
        if(text==null || "".equals(text)){
            return true;
        }
        return false;
    }
}
