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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.suda.datetimewallpaper.about.AboutActivity;
import com.suda.datetimewallpaper.adapter.CusAdapter;
import com.suda.datetimewallpaper.util.AlipayDonate;
import com.suda.datetimewallpaper.util.DownUtil;
import com.suda.datetimewallpaper.util.FileUtil;
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
import me.drakeet.materialdialog.MaterialDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.suda.datetimewallpaper.util.FileUtil.copyFile;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_COLOR;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_IMAGE;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_CUS_CONF;
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
    private final int REQUEST_CODE_PERMISSION2 = 0x005;
    private final int REQUEST_CODE_PERMISSION3 = 0x006;


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
                dateTimeView.resetConf(true);
            }
        });


        CheckBox cbHideAct = findViewById(R.id.cb_hide_act);
        cbHideAct.setChecked((boolean) SharedPreferencesUtil.getData(SP_HIDE_ACT, false));
        cbHideAct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.putData(SP_HIDE_ACT, isChecked);
                setExcludeFromRecents(isChecked);
                dateTimeView.resetConf(false);
            }
        });
        showHideNumFormat();

        setExcludeFromRecents(cbHideAct.isChecked());

        AlipayDonate.donateTip("gomain", 5, this);
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
        dateTimeView.resetConf(false);
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
                .setTitle(R.string.text_color)
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
                        dateTimeView.resetConf(false);
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
                .setTitle(R.string.bg_color)
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
                        dateTimeView.resetConf(false);
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

    public void about(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void donateZFB(View view) {
        Toast.makeText(this, "感谢支持,时间轮盘将越来越好", Toast.LENGTH_SHORT).show();
        AlipayDonate.startAlipayClient(MainActivity.this, "apqiqql0hgh5pmv54d");
    }

    private void showHideNumFormat() {
        if ("".equals(SharedPreferencesUtil.getData(SP_CUS_CONF, ""))) {
            findViewById(R.id.cb_num_format).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.cb_num_format).setVisibility(View.GONE);
        }
    }

    public void setCusConfFromNet(View view) {
        setCusConfFromNet();
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION3)
    public void setCusConfFromNet() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            final MaterialDialog outDialog = new MaterialDialog(this);
            outDialog.setTitle(R.string.plz_enter_conf_url);
            final EditText editText = new EditText(this);
            outDialog.setContentView(editText);
            editText.setBackgroundColor(getResources().getColor(R.color.dracula_page_bg));
            editText.setFocusable(true);
            editText.setHint(R.string.plz_enter_conf_url);
            outDialog.setCanceledOnTouchOutside(true);
            outDialog.setPositiveButton(R.string.import_s, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = editText.getText().toString();
                    if (TextUtils.isEmpty(url)) {
                        Toast.makeText(MainActivity.this, R.string.url_null_tip, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (url.lastIndexOf(".json") < 0) {
                        Toast.makeText(MainActivity.this, R.string.url_not_json, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                    final File file = new File(FileUtil.getBaseFile(), fileName);

                    if (file.exists()) {
                        Toast.makeText(MainActivity.this, String.format(getString(R.string.exist_conf), file.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final MaterialDialog loadDialog = new MaterialDialog(MainActivity.this);
                    loadDialog.setContentView(new ProgressBar(MainActivity.this));
                    loadDialog.setTitle(R.string.downloading);
                    loadDialog.show();

                    DownUtil.downLoadFile(url, file, new DownUtil.ReqCallBack() {
                        @Override
                        public void showResult(final int code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadDialog.dismiss();
                                    if (code == 1) {
                                        if (file.exists()) {
                                            SharedPreferencesUtil.putData(SP_CUS_CONF, file.getAbsolutePath());
                                            dateTimeView.resetConf(true);

                                            AlipayDonate.donateTip("usecus", 2, MainActivity.this);

                                            Toast.makeText(MainActivity.this, R.string.import_success, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, R.string.import_fail, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, R.string.import_fail, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    outDialog.dismiss();
                }
            });
            outDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outDialog.dismiss();
                }
            });
            outDialog.show();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission),
                    REQUEST_CODE_PERMISSION3, perms);
        }
    }

    public void setCusConf(View view) {
        setCusConf();
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION2)
    private void setCusConf() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            final MaterialDialog outDialog = new MaterialDialog(this);
            outDialog.setTitle(R.string.select_conf);
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.cus_conf_layout, null);
            ListView listView = viewGroup.findViewById(R.id.conf_list);
            final CusAdapter restoreAdapter = new CusAdapter();
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final File file = (File) restoreAdapter.getItem(position);
                    final MaterialDialog outDialog = new MaterialDialog(MainActivity.this);
                    outDialog.setTitle(R.string.select_conf);
                    outDialog.setMessage(String.format(getString(R.string.delete_conf), file.getName()));
                    outDialog.setCanceledOnTouchOutside(true);
                    outDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            outDialog.dismiss();
                        }
                    });

                    outDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            file.delete();
                            restoreAdapter.refresh();
                            outDialog.dismiss();
                        }
                    });
                    outDialog.show();
                    return true;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    outDialog.dismiss();
                    final File file = (File) restoreAdapter.getItem(position);
                    final MaterialDialog innerDialog = new MaterialDialog(MainActivity.this);
                    innerDialog.setTitle(R.string.select_conf_confirm);
                    if (file == null) {
                        innerDialog.setMessage(String.format(getString(R.string.select_conf_1), getString(R.string.select_conf_default)));
                    } else {
                        innerDialog.setMessage(String.format(getString(R.string.select_conf_1), file.getName()));
                    }
                    innerDialog.setNegativeButton(R.string.no, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            innerDialog.dismiss();
                        }
                    });

                    innerDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlipayDonate.donateTip("usecus", 2, MainActivity.this);

                            if (file == null) {
                                SharedPreferencesUtil.putData(SP_CUS_CONF, "");
                            } else {
                                SharedPreferencesUtil.putData(SP_CUS_CONF, file.getAbsolutePath());
                            }
                            showHideNumFormat();
                            dateTimeView.resetConf(true);
                            innerDialog.dismiss();
                        }
                    });
                    innerDialog.show();
                }
            });
            listView.setAdapter(restoreAdapter);
            outDialog.setContentView(viewGroup);
            outDialog.setCanceledOnTouchOutside(true);
            outDialog.show();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission),
                    REQUEST_CODE_PERMISSION2, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.set_wallpaper_cancel, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, R.string.version_low, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
