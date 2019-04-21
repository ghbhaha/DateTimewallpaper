package com.suda.datetimewallpaper

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.suda.datetimewallpaper.about.AboutActivity
import com.suda.datetimewallpaper.adapter.CusAdapter
import com.suda.datetimewallpaper.util.*
import com.suda.datetimewallpaper.util.FileUtil.copyFile
import com.suda.datetimewallpaper.util.SharedPreferencesUtil.*
import com.umeng.analytics.MobclickAgent
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_main.*
import me.drakeet.materialdialog.MaterialDialog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @author guhaibo
 * @date 2019/4/9
 */
const val REQUEST_CODE_SET_WALLPAPER = 0x001
const val REQUEST_CODE_CHOOSE = 0x002
const val REQUEST_CODE_PERMISSION = 0x003
const val REQUEST_CODE_PERMISSION2 = 0x004
const val REQUEST_CODE_PERMISSION3 = 0x005

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, ColorPickerDialogListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dtv.setOnClickListener {
            panel.visibility = if (panel.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            bt_set_wallpaper.visibility = if (bt_set_wallpaper.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        panel.setOnTouchListener { _, _ -> true }

        setProgress(R.id.vertical_margin, SP_VERTICAL_POS, 0.5f)
        setProgress(R.id.horizontal_margin, SP_VERTICAL_POS, 0.5f)
        setProgress(R.id.scale, SP_VERTICAL_POS, 0.25f)
        setProgress(R.id.rotate, SP_VERTICAL_POS, 0f)

        cb_num_format.isChecked = SharedPreferencesUtil.getData(SP_NUM_FORMAT, true) as Boolean
        cb_num_format.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferencesUtil.putData(SP_NUM_FORMAT, isChecked)
            dtv.resetConf(true)
        }

        cb_hide_act.isChecked = SharedPreferencesUtil.getData(SP_HIDE_ACT, false) as Boolean
        cb_hide_act.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferencesUtil.putData(SP_HIDE_ACT, isChecked)
            setExcludeFromRecents(isChecked)
            dtv.resetConf(false)
        }
        showHideNumFormat()
        setExcludeFromRecents(cb_hide_act.isChecked)

        AlipayDonate.donateTip("gomain", 5, this)
        CheckUpdateUtil.checkUpdate(this, false)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    private fun setProgress(id: Int, sp: String, defaultValue: Float) {
        val seekBar = findViewById<SeekBar>(id)
        seekBar.progress = (100 * SharedPreferencesUtil.getData(sp, defaultValue) as Float).toInt()
        seekBar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        var progress = progress
        when (seekBar.id) {
            R.id.vertical_margin -> SharedPreferencesUtil.putData(SP_VERTICAL_POS, progress * 1.0f / 100)
            R.id.horizontal_margin -> SharedPreferencesUtil.putData(
                SharedPreferencesUtil.SP_HORIZONTAL_POS,
                progress * 1.0f / 100
            )
            R.id.rotate -> {
                if (Math.abs(progress % 25 - 1) < 1) {
                    progress = progress / 25 * 25
                }
                SharedPreferencesUtil.putData(SharedPreferencesUtil.SP_ROTATE, progress * 1.0f / 100)
            }
            R.id.scale -> SharedPreferencesUtil.putData(SharedPreferencesUtil.SP_SCALE, progress * 1.0f / 100)
        }
        dtv.resetConf(false)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
    }

    fun setTextColor(view: View) {
        ColorPickerDialog.newBuilder()
            .setDialogId(1)
            .setShowAlphaSlider(true)
            .setColor(SharedPreferencesUtil.getData(SP_TEXT_COLOR, Color.BLACK) as Int)
            .show(this)
    }

    fun setTextColorDark(view: View) {
        ColorPickerDialog.newBuilder()
            .setDialogId(2)
            .setShowAlphaSlider(true)
            .setColor(SharedPreferencesUtil.getData(SP_TEXT_COLOR_DARK, Color.BLACK.dark()) as Int)
            .show(this)
    }

    fun setBackImage(view: View) {
        setBackImage()
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    private fun setBackImage() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val mimeTypes = HashSet<MimeType>()
            mimeTypes.add(MimeType.PNG)
            mimeTypes.add(MimeType.JPEG)
            Matisse.from(this@MainActivity)
                .choose(mimeTypes)
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE)

        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.storage_permission),
                REQUEST_CODE_PERMISSION, *perms
            )
        }
    }

    fun setBackColor(view: View) {
        ColorPickerDialog.newBuilder()
            .setDialogId(3)
            .setColor(SharedPreferencesUtil.getData(SP_BG_COLOR, Color.BLACK) as Int)
            .show(this)
    }

    override fun onColorSelected(p0: Int, p1: Int) {
        if (p0 == 1) {
            SharedPreferencesUtil.putData(SP_TEXT_COLOR, p1)
        } else if (p0 == 2) {
            SharedPreferencesUtil.putData(SP_TEXT_COLOR_DARK, p1)
        } else if (p0 == 3) {
            SharedPreferencesUtil.putData(SP_BG_COLOR, p1)
            SharedPreferencesUtil.putData(SP_BG_IMAGE, "")
        }
        dtv.resetConf(false)
    }

    override fun onDialogDismissed(p0: Int) {

    }

    fun setWallPaper(view: View) {
        WallpaperUtil.setLiveWallpaper(this, REQUEST_CODE_SET_WALLPAPER)
        //        android.provider.Settings.System.putString(getContentResolver(), "lock_wallpaper_provider_authority", "com.android.thememanager.theme_lock_live_wallpaper");
        //        if (!WallpaperUtil.wallpaperIsUsed(this)) {
        //            WallpaperUtil.setLiveWallpaper(this, REQUEST_CODE_SET_WALLPAPER);
        //        } else {
        //            Toast.makeText(this, "壁纸已经设置", Toast.LENGTH_SHORT).show();
        //        }
    }

    fun about(view: View) {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    fun donateZFB(view: View) {
        Toast.makeText(this, "感谢支持,时间轮盘将越来越好", Toast.LENGTH_SHORT).show()
        AlipayDonate.startAlipayClient(this@MainActivity, "apqiqql0hgh5pmv54d")
    }

    private fun showHideNumFormat() {
        if ("" == SharedPreferencesUtil.getData(SP_CUS_CONF, "")) {
            findViewById<View>(R.id.cb_num_format).visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.cb_num_format).visibility = View.GONE
        }
    }

    fun setCusConfFromNet(view: View) {
        setCusConfFromNet()
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION3)
    fun setCusConfFromNet() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val outDialog = MaterialDialog(this)
            outDialog.setTitle(R.string.plz_enter_conf_url)
            val editText = EditText(this)
            outDialog.setContentView(editText)
            editText.setBackgroundColor(resources.getColor(R.color.dracula_page_bg))
            editText.isFocusable = true
            editText.setHint(R.string.plz_enter_conf_url)
            outDialog.setCanceledOnTouchOutside(true)
            outDialog.setPositiveButton(R.string.import_s, View.OnClickListener {
                val url = editText.text.toString()
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(this@MainActivity, R.string.url_null_tip, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                } else if (url.lastIndexOf(".json") < 0) {
                    Toast.makeText(this@MainActivity, R.string.url_not_json, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                } else if (!TextUtil.isJsonUrl(url)) {
                    Toast.makeText(this@MainActivity, R.string.url_not_correct, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val fileName = url.substring(url.lastIndexOf("/") + 1, url.length)
                val file = File(FileUtil.getBaseFile(), fileName)

                if (file.exists()) {
                    Toast.makeText(
                        this@MainActivity,
                        String.format(getString(R.string.exist_conf), file.absolutePath),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }

                val loadDialog = MaterialDialog(this@MainActivity)
                loadDialog.setContentView(ProgressBar(this@MainActivity))
                loadDialog.setTitle(R.string.downloading)
                loadDialog.show()
                try {
                    DownUtil.downLoadFile(url, file) { code ->
                        runOnUiThread {
                            loadDialog.dismiss()
                            if (code == 1) {
                                if (file.exists()) {
                                    SharedPreferencesUtil.putData(SP_CUS_CONF, file.absolutePath)
                                    dtv.resetConf(true)

                                    AlipayDonate.donateTip("usecus", 2, this@MainActivity)

                                    Toast.makeText(this@MainActivity, R.string.import_success, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(this@MainActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@MainActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
                }

                outDialog.dismiss()
            })
            outDialog.setNegativeButton(R.string.cancel) { outDialog.dismiss() }
            outDialog.show()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.storage_permission),
                REQUEST_CODE_PERMISSION3, *perms
            )
        }
    }

    fun setCusConf(view: View) {
        setCusConf()
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION2)
    private fun setCusConf() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val outDialog = MaterialDialog(this)
            outDialog.setTitle(R.string.select_conf)
            val viewGroup = LayoutInflater.from(this).inflate(R.layout.cus_conf_layout, null) as ViewGroup
            val listView = viewGroup.findViewById<ListView>(R.id.conf_list)
            val restoreAdapter = CusAdapter()
            listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
                val file = restoreAdapter.getItem(position) as File
                val outDialog = MaterialDialog(this@MainActivity)
                outDialog.setTitle(R.string.select_conf)
                outDialog.setMessage(String.format(getString(R.string.delete_conf), file.name))
                outDialog.setCanceledOnTouchOutside(true)
                outDialog.setNegativeButton(R.string.cancel) { outDialog.dismiss() }

                outDialog.setPositiveButton(R.string.yes) {
                    file.delete()
                    restoreAdapter.refresh()
                    outDialog.dismiss()
                }
                outDialog.show()
                true
            }
            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                outDialog.dismiss()
                val file = restoreAdapter.getItem(position) as File?
                val innerDialog = MaterialDialog(this@MainActivity)
                innerDialog.setTitle(R.string.select_conf_confirm)
                if (file == null) {
                    innerDialog.setMessage(
                        String.format(
                            getString(R.string.select_conf_1),
                            getString(R.string.select_conf_default)
                        )
                    )
                } else {
                    innerDialog.setMessage(String.format(getString(R.string.select_conf_1), file.name))
                }
                innerDialog.setNegativeButton(R.string.no) { innerDialog.dismiss() }

                innerDialog.setPositiveButton(R.string.yes) {
                    AlipayDonate.donateTip("usecus", 2, this@MainActivity)
                    if (file == null) {
                        SharedPreferencesUtil.putData(SP_CUS_CONF, "")
                    } else {
                        SharedPreferencesUtil.putData(SP_CUS_CONF, file.absolutePath)
                    }
                    showHideNumFormat()
                    dtv.resetConf(true)
                    innerDialog.dismiss()
                }
                innerDialog.show()
            }
            listView.adapter = restoreAdapter
            outDialog.setContentView(viewGroup)
            outDialog.setCanceledOnTouchOutside(true)
            outDialog.show()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.storage_permission),
                REQUEST_CODE_PERMISSION2, *perms
            )
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.set_wallpaper_cancel, Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val org = File(Matisse.obtainPathResult(data!!)[0])
            val dst = File(filesDir, org.name)
            try {
                copyFile(org, dst)
                SharedPreferencesUtil.putData(SP_BG_IMAGE, dst.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun setExcludeFromRecents(exclude: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val service = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                for (appTask in service.appTasks) {
                    if (appTask.taskInfo.id == taskId) {
                        appTask.setExcludeFromRecents(exclude)
                    }
                }
            } catch (e: Exception) {
            }

        } else {
            Toast.makeText(this, R.string.version_low, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
