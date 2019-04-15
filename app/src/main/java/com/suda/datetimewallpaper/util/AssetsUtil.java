package com.suda.datetimewallpaper.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author guhaibo
 * @date 2019/4/14
 */
public class AssetsUtil {
    public static String getFromAssets(String fileName, Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null){
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
