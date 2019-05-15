package com.suda.datetimewallpaper.util;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author guhaibo
 * @date 2019/4/13
 */
public class FileUtil {

    public static File getBaseFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "时间轮盘");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String getFromFile(File file) {
        try {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {

        if (targetFile.exists()){
            targetFile.delete();
        }
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }
}
