package com.suda.datetimewallpaper;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.suda.datetimewallpaper.util.AlipayDonate;
import com.suda.datetimewallpaper.util.Glide4Engine;
import com.suda.datetimewallpaper.util.SharedPreferencesUtil;
import com.suda.datetimewallpaper.util.WallpaperUtil;
import com.suda.datetimewallpaper.view.DateTimeView;
import com.umeng.analytics.MobclickAgent;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.suda.datetimewallpaper.util.FileUtil.copyFile;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_COLOR;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_IMAGE;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_HIDE_ACT;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_NUM_FORMAT;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_TEXT_COLOR;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_VERTICAL_POS;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private final int REQUEST_CODE_SET_WALLPAPER = 0x001;
    private final int REQUEST_CODE_CHOOSE = 0x002;

    private final int REQUEST_CODE_PERMISSION = 0x004;

    DateTimeView dateTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTimeView = findViewById(R.id.dtv);
        setProgress(R.id.vertical_margin, SP_VERTICAL_POS, 0.5f);
        setProgress(R.id.horizontal_margin, SP_VERTICAL_POS, 0.5f);
        setProgress(R.id.scale, SP_VERTICAL_POS, 0.25f);
        setProgress(R.id.rotate, SP_VERTICAL_POS, 0f);

        CheckBox checkBox = findViewById(R.id.cb_num_format);
        checkBox.setChecked((boolean) SharedPreferencesUtil.getData(SP_NUM_FORMAT, true));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.putData(SP_NUM_FORMAT, isChecked);
                dateTimeView.resetConf();
            }
        });


        CheckBox cbHideAct = findViewById(R.id.cb_hide_act);
        cbHideAct.setChecked((boolean) SharedPreferencesUtil.getData(SP_HIDE_ACT, false));
        cbHideAct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.putData(SP_HIDE_ACT, isChecked);
                setExcludeFromRecents(isChecked);
                dateTimeView.resetConf();
            }
        });

        setExcludeFromRecents(cbHideAct.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void setProgress(int id, String sp, float defaultValue) {
        SeekBar seekBar = findViewById(id);
        seekBar.setProgress((int) (100 * (float) SharedPreferencesUtil.getData(sp, defaultValue)));
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.vertical_margin:
                SharedPreferencesUtil.putData(SP_VERTICAL_POS, progress * 1.0f / 100);
                break;
            case R.id.horizontal_margin:
                SharedPreferencesUtil.putData(SharedPreferencesUtil.SP_HORIZONTAL_POS, progress * 1.0f / 100);
                break;
            case R.id.rotate:
                if (Math.abs(progress % 25 - 1) < 1) {
                    progress = progress / 25 * 25;
                }
                SharedPreferencesUtil.putData(SharedPreferencesUtil.SP_ROTATE, progress * 1.0f / 100);
                break;
            case R.id.scale:
                SharedPreferencesUtil.putData(SharedPreferencesUtil.SP_SCALE, progress * 1.0f / 100);
                break;
        }
        dateTimeView.resetConf();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setTextColor(View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("文字颜色")
                .initialColor((int) SharedPreferencesUtil.getData(SP_TEXT_COLOR, Color.WHITE))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(13)
                .lightnessSliderOnly()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        SharedPreferencesUtil.putData(SP_TEXT_COLOR, selectedColor);
                        dateTimeView.resetConf();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void setBackImage(View view) {
        setBackImage();
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    private void setBackImage() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Set<MimeType> mimeTypes = new HashSet<>();
            mimeTypes.add(MimeType.PNG);
            mimeTypes.add(MimeType.JPEG);
            Matisse.from(MainActivity.this)
                    .choose(mimeTypes)
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new Glide4Engine())
                    .forResult(REQUEST_CODE_CHOOSE);

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission),
                    REQUEST_CODE_PERMISSION, perms);
        }
    }

    public void setBackColor(View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("背景颜色")
                .initialColor((int) SharedPreferencesUtil.getData(SP_BG_COLOR, Color.BLACK))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(13)
                .lightnessSliderOnly()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        SharedPreferencesUtil.putData(SP_BG_COLOR, selectedColor);
                        SharedPreferencesUtil.putData(SP_BG_IMAGE, "");
                        dateTimeView.resetConf();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void setWallPaper(View view) {
        WallpaperUtil.setLiveWallpaper(this, REQUEST_CODE_SET_WALLPAPER);
//        android.provider.Settings.System.putString(getContentResolver(), "lock_wallpaper_provider_authority", "com.android.thememanager.theme_lock_live_wallpaper");
//        if (!WallpaperUtil.wallpaperIsUsed(this)) {
//            WallpaperUtil.setLiveWallpaper(this, REQUEST_CODE_SET_WALLPAPER);
//        } else {
//            Toast.makeText(this, "壁纸已经设置", Toast.LENGTH_SHORT).show();
//        }
    }

    public void donateZFB(View view) {
        boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(this);
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(this, "apqiqql0hgh5pmv54d");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "设置动态壁纸成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "取消设置动态壁纸", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            File org = new File(Matisse.obtainPathResult(data).get(0));
            File dst = new File(getFilesDir(), org.getName());
            try {
                copyFile(org, dst);
                SharedPreferencesUtil.putData(SP_BG_IMAGE, dst.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setExcludeFromRecents(boolean exclude) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                ActivityManager service = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.AppTask appTask : service.getAppTasks()) {
                    if (appTask.getTaskInfo().id == getTaskId()) {
                        appTask.setExcludeFromRecents(exclude);
                    }
                }
            } catch (Exception e) {
            }
        } else {
            Toast.makeText(this, "您的系统版本过低，暂不支持本功能~", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
