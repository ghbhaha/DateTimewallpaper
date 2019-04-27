package com.suda.datetimewallpaper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.suda.datetimewallpaper.about.AboutActivity
import com.suda.datetimewallpaper.adapter.WallPaperModelAdapter
import com.suda.datetimewallpaper.base.BaseAct
import com.suda.datetimewallpaper.bean.WallPaperModel
import com.suda.datetimewallpaper.util.AlipayDonate
import com.suda.datetimewallpaper.util.CheckUpdateUtil
import com.suda.datetimewallpaper.util.SharedPreferencesUtil
import com.suda.datetimewallpaper.util.WallpaperUtil
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_content.*

class HomeActivity : BaseAct(), NavigationView.OnNavigationItemSelectedListener {

    val modelList = mutableListOf<WallPaperModel>()
    val sp by lazy {
        SharedPreferencesUtil(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rcy_model.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rcy_model.adapter = WallPaperModelAdapter(modelList)

        AlipayDonate.donateTip("gomain", 2, this)
        CheckUpdateUtil.checkUpdate(this, false)

        initView()
        initConfs()

        menu_add.setOnClickListener {
            modelList.add(sp.addNewDefaultModel())
            rcy_model.adapter?.notifyDataSetChanged()
            AlipayDonate.donateTip("addConf", 1, this)
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
            R.id.set_wallpaper -> {
                Toast.makeText(this, R.string.switch_conf_tip, Toast.LENGTH_LONG).show()
                WallpaperUtil.setLiveWallpaper(this, REQUEST_CODE_SET_WALLPAPER)
            }
        }
        return true
    }
}
