package com.suda.datetimewallpaper.util;

import android.content.Context;

import java.io.*;

/**
 * Created by ghbha on 2016/4/27.
 */
public class AssetsCopyUtil {
    /**
     * 将asseet当中的数据库文件拷到程序目录
     */
    public static void copyEmbassy2Databases(Context activity, File file) {
        if (file.exists()) {
            return;
        }
        file.getParentFile().mkdirs();

        InputStream in = null;
        OutputStream out = null;

        try {

            out = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len = 0;
            in = activity.getAssets().open(file.getName());
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            out.flush();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
