package com.suda.datetimewallpaper.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.adapter.CusAdapter
import com.suda.datetimewallpaper.adapter.WallPaperModelAdapter
import com.suda.datetimewallpaper.base.BaseAct
import com.suda.datetimewallpaper.bean.WallPaperModel
import com.suda.datetimewallpaper.ui.about.AboutActivity
import com.suda.datetimewallpaper.util.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_content.*
import me.drakeet.materialdialog.MaterialDialog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


const val REQ_NET_CONF = 1
const val CONF_PATH = "conf_path"

class HomeActivity : BaseAct(), NavigationView.OnNavigationItemSelectedListener {

    val modelList = mutableListOf<WallPaperModel>()
    val sp by lazy {
        SharedPreferencesUtil(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        setContentView(R.layout.activity_home)

        rcy_model.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rcy_model.adapter = WallPaperModelAdapter(modelList)

        AlipayDonate.donateTip("gomain", 2, this)
        CheckUpdateUtil.checkUpdate(this, false)

        initView()
        initConfs()

        menu_add_default.setOnClickListener {
            modelList.add(sp.addNewDefaultModel())
            rcy_model.adapter?.notifyDataSetChanged()
            AlipayDonate.donateTip("addConf", 1, this)
        }

        menu_add_from_net.setOnClickListener {
            addFromNet()
        }

        menu_add_from_local.setOnClickListener {
            addFromLocal()
        }

        setExcludeFromRecents(
            SharedPreferencesUtil.getAppDefault(this).getData(
                SharedPreferencesUtil.SP_HIDE_ACT,
                false
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_NET_CONF && resultCode == Activity.RESULT_OK && data != null) {
            addFromLocal(File(data.getStringExtra(CONF_PATH)))
        }
    }

    private fun initConfs() {
        modelList.addAll(sp.wallpapermodels)
    }

    private fun initView() {
        //init toolbar
        setSupportActionBar(toolbar_main)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar_main,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION2)
    private fun addFromLocal() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val outDialog = MaterialDialog(this)
            outDialog.setTitle(R.string.select_conf)
            val viewGroup = LayoutInflater.from(this).inflate(R.layout.cus_conf_layout, null) as ViewGroup
            val listView = viewGroup.findViewById<ListView>(R.id.conf_list)
            val restoreAdapter = CusAdapter()
            listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
                restoreAdapter.getItem(position)?.run {
                    val file = this as File
                    val outDialog = MaterialDialog(this@HomeActivity)
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
                val innerDialog = MaterialDialog(this@HomeActivity)
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
                    addFromLocal(file)
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

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    private fun addFromNet() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val intent = Intent(this, WebActivity::class.java)
            startActivityForResult(intent, REQ_NET_CONF)
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.storage_permission),
                REQUEST_CODE_PERMISSION, *perms
            )
        }
    }

    private fun addFromLocal(file: File?) {
        AlipayDonate.donateTip("usecus", 2, this@HomeActivity)
        val wallPaperModel = sp.addNewDefaultModel()
        modelList.add(wallPaperModel)
        val sharedPreferencesUtil = SharedPreferencesUtil(this@HomeActivity, wallPaperModel.paperId)
        if (file == null) {
            wallPaperModel.modelName = getString(R.string.select_conf_default)
            sharedPreferencesUtil.putData(SharedPreferencesUtil.SP_CUS_CONF, "")
        } else {
            var name = file.name
            if (name.lastIndexOf(".") > 0) {
                name = name.substring(0, name.lastIndexOf("."))
            }
            wallPaperModel.modelName = name
            sharedPreferencesUtil.putData(SharedPreferencesUtil.SP_CUS_CONF, file.absolutePath)
        }
        sp.editName(wallPaperModel.paperId, wallPaperModel.modelName)
        rcy_model.adapter?.notifyDataSetChanged()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event); }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_donate -> {
                Toast.makeText(this, "感谢支持,时间轮盘将越来越好", Toast.LENGTH_SHORT).show()
                AlipayDonate.startAlipayClient(this, "apqiqql0hgh5pmv54d")
            }
            R.id.nav_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_group -> {
                try {
                    val intent = Intent()
                    val KEY = "osvzvvRXXf5TvffW39CgXbxlmHtCscgK"
                    intent.data =
                        Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$KEY")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show()
                }

            }
        }
        return true
    }

}
