package com.suda.datetimewallpaper.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author guhaibo
 * @date 2019/4/15
 */
public class DownUtil {


    public interface ReqCallBack {
        /**
         * 显示结果
         *
         * @param code
         */
        void showResult(int code);
    }

    public static void downLoadFile(String fileUrl, final File file, final ReqCallBack callBack) {
        if (file.exists()) {
            callBack.showResult(1);
            return;
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.showResult(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                    }
                    fos.flush();

                    callBack.showResult(1);
                } catch (IOException e) {

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
