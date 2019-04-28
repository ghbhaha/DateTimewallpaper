package com.suda.datetimewallpaper.base

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.suda.datetimewallpaper.R
import com.umeng.analytics.MobclickAgent
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author guhaibo
 * @date 2019/4/23
 */
abstract class BaseAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        supportActionBar?.setHomeAsUpIndicator(upArrow);
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}