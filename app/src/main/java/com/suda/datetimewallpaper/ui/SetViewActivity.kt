package com.suda.datetimewallpaper.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.adapter.CusAdapter
import com.suda.datetimewallpaper.base.BaseAct
import com.suda.datetimewallpaper.util.*
import com.suda.datetimewallpaper.util.FileUtil.copyFile
import com.suda.datetimewallpaper.util.SharedPreferencesUtil.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_set_view.*
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

class SetViewActivity : BaseAct(), SeekBar.OnSeekBarChangeListener, ColorPickerDialogListener {

    val paperId by lazy {
        intent.getLongExtra("paperId", 0L)
    }

    val sharedPreferencesUtil by lazy {
        SharedPreferencesUtil(this, paperId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_view)

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

        dtv.post {
            dtv.resetPaperId(paperId)
        }

        panel.setOnTouchListener { _, _ -> true }

        setProgress(R.id.vertical_margin, SP_VERTICAL_POS, 0.5f)
        setProgress(R.id.horizontal_margin, SP_HORIZONTAL_POS, 0.5f)
        setProgress(R.id.scale, SP_SCALE, 0.25f)
        setProgress(R.id.rotate, SP_ROTATE, 0f)
    }

    private fun setProgress(id: Int, sp: String, defaultValue: Float) {
        val seekBar = findViewById<SeekBar>(id)
        seekBar.progress = (seekBar.max * (sharedPreferencesUtil.getData(sp, defaultValue) as Float)).toInt()
        seekBar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        var progress = progress
        when (seekBar.id) {
            R.id.vertical_margin -> sharedPreferencesUtil.putData(SP_VERTICAL_POS, progress * 1.0f / seekBar.max)
            R.id.horizontal_margin -> sharedPreferencesUtil.putData(
                SharedPreferencesUtil.SP_HORIZONTAL_POS,
                progress * 1.0f / seekBar.max
            )
            R.id.rotate -> {
                if (Math.abs(progress % 25 - 1) < 1) {
                    progress = progress / 25 * 25
                }
                sharedPreferencesUtil.putData(SharedPreferencesUtil.SP_ROTATE, progress * 1.0f / seekBar.max)
            }
            R.id.scale -> sharedPreferencesUtil.putData(SharedPreferencesUtil.SP_SCALE, progress * 1.0f / seekBar.max)
        }
        dtv.resetConf(false)
    }

    fun adjustVerticalMargin(view: View) {
        when ((view as TextView).text) {
            "+" -> {
                vertical_margin.progress = vertical_margin.progress + 1
            }
            "-" -> {
                vertical_margin.progress = vertical_margin.progress - 1
            }
        }
    }

    fun adjustHorizontalMargin(view: View) {
        when ((view as TextView).text) {
            "+" -> {
                horizontal_margin.progress = horizontal_margin.progress + 1
            }
            "-" -> {
                horizontal_margin.progress = horizontal_margin.progress - 1
            }
        }
    }

    fun adjustScale(view: View) {
        when ((view as TextView).text) {
            "+" -> {
                scale.progress = scale.progress + 1
            }
            "-" -> {
                scale.progress = scale.progress - 1
            }
        }
    }

    fun adjustRotate(view: View) {
        when ((view as TextView).text) {
            "+" -> {
                rotate.progress = rotate.progress + 1
            }
            "-" -> {
                rotate.progress = rotate.progress - 1
            }
        }
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
            .setColor(sharedPreferencesUtil.getData(SP_TEXT_COLOR, Color.BLACK) as Int)
            .show(this)
    }

    fun setTextColorDark(view: View) {
        ColorPickerDialog.newBuilder()
            .setDialogId(2)
            .setShowAlphaSlider(true)
            .setColor(sharedPreferencesUtil.getData(SP_TEXT_COLOR_DARK, Color.BLACK.dark()) as Int)
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
            Matisse.from(this@SetViewActivity)
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
            .setColor(sharedPreferencesUtil.getData(SP_BG_COLOR, Color.BLACK) as Int)
            .show(this)
    }

    override fun onColorSelected(p0: Int, p1: Int) {
        if (p0 == 1) {
            sharedPreferencesUtil.putData(SP_TEXT_COLOR, p1)
        } else if (p0 == 2) {
            sharedPreferencesUtil.putData(SP_TEXT_COLOR_DARK, p1)
        } else if (p0 == 3) {
            sharedPreferencesUtil.putData(SP_BG_COLOR, p1)
            sharedPreferencesUtil.putData(SP_BG_IMAGE, "")
        }
        dtv.resetConf(false)
    }

    override fun onDialogDismissed(p0: Int) {

    }

    fun setWallPaper(view: View) {
        val sp = SharedPreferencesUtil(this)
        sp.setWallPaperId(paperId)
        WallpaperUtil.setLiveWallpaper(this, REQUEST_CODE_SET_WALLPAPER)
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
                    Toast.makeText(this@SetViewActivity, R.string.url_null_tip, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                } else if (url.lastIndexOf(".json") < 0) {
                    Toast.makeText(this@SetViewActivity, R.string.url_not_json, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                } else if (!TextUtil.isJsonUrl(url)) {
                    Toast.makeText(this@SetViewActivity, R.string.url_not_correct, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val fileName = url.substring(url.lastIndexOf("/") + 1, url.length)
                val file = File(FileUtil.getBaseFile(), fileName)

                if (file.exists()) {
                    Toast.makeText(
                        this@SetViewActivity,
                        String.format(getString(R.string.exist_conf), file.absolutePath),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }

                val loadDialog = MaterialDialog(this@SetViewActivity)
                loadDialog.setContentView(ProgressBar(this@SetViewActivity))
                loadDialog.setTitle(R.string.downloading)
                loadDialog.show()
                try {
                    DownUtil.downLoadFile(url, file) { code ->
                        runOnUiThread {
                            loadDialog.dismiss()
                            if (code == 1) {
                                if (file.exists()) {
                                    sharedPreferencesUtil.putData(SP_CUS_CONF, file.absolutePath)
                                    dtv.resetConf(true)

                                    AlipayDonate.donateTip("usecus", 2, this@SetViewActivity)

                                    Toast.makeText(this@SetViewActivity,
                                        R.string.import_success, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(this@SetViewActivity,
                                        R.string.import_fail, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@SetViewActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SetViewActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
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
            val viewGroup = LayoutInflater.from(this).inflate(R.layout.layout_cus_conf, null) as ViewGroup
            val listView = viewGroup.findViewById<ListView>(R.id.conf_list)
            val restoreAdapter = CusAdapter()
            listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
                restoreAdapter.getItem(position)?.run {
                    val file = this as File
                    val outDialog = MaterialDialog(this@SetViewActivity)
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
                }
                true
            }
            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                outDialog.dismiss()
                val file = restoreAdapter.getItem(position) as File?
                val innerDialog = MaterialDialog(this@SetViewActivity)
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
                    AlipayDonate.donateTip("usecus", 2, this@SetViewActivity)
                    if (file == null) {
                        sharedPreferencesUtil.putData(SP_CUS_CONF, "")
                    } else {
                        sharedPreferencesUtil.putData(SP_CUS_CONF, file.absolutePath)
                    }
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
                sharedPreferencesUtil.putData(SP_BG_IMAGE, dst.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

}
