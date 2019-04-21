package com.suda.datetimewallpaper.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guhaibo
 * @date 2019/4/21
 */
public class TextUtil {

    private final static Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(.*?).json");

    /**
     * 判断字符串是否为JSON的url
     *
     * @param urls
     * @return
     */
    public static boolean isJsonUrl(String urls) {
        boolean isurl = false;
        Matcher mat = pattern.matcher(urls.trim());
        //判断是否匹配
        isurl = mat.matches();
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }


}
