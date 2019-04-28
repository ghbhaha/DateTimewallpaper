package com.suda.datetimewallpaper.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.base.BaseAct
import com.suda.datetimewallpaper.util.DownUtil
import com.suda.datetimewallpaper.util.FileUtil
import kotlinx.android.synthetic.main.activity_web.*
import me.drakeet.materialdialog.MaterialDialog
import java.io.File

class WebActivity : BaseAct() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        setContentView(R.layout.activity_web)
        web_view.settings.javaScriptEnabled = true
        web_view.settings.userAgentString = "sudajs"
        web_view.settings.cacheMode = WebSettings.LOAD_DEFAULT;

        web_view.loadUrl("https://timeconf.sudamod.site/")
        web_view.addJavascriptInterface(JSInterface(), "sudajs")

        val loadDialog = MaterialDialog(this@WebActivity)
        loadDialog.setContentView(ProgressBar(this@WebActivity))
        loadDialog.setTitle("Loading...")
        loadDialog.show()
        web_view.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    loadDialog.dismiss()
                }
            }
        }
    }

    inner class JSInterface {
        @JavascriptInterface
        fun downConf(url: String?) {
            if (url != null) {
                runOnUiThread {
                    val fileName = url.substring(url.lastIndexOf("/") + 1, url.length)
                    val file = File(FileUtil.getBaseFile(), fileName)
                    val loadDialog = MaterialDialog(this@WebActivity)
                    loadDialog.setContentView(ProgressBar(this@WebActivity))
                    loadDialog.setTitle(R.string.downloading)
                    loadDialog.show()
                    try {
                        DownUtil.downLoadFile(url, file) { code ->
                            runOnUiThread {
                                loadDialog.dismiss()
                                if (code == 1) {
                                    if (file.exists()) {
                                        Toast.makeText(
                                            this@WebActivity,
                                            R.string.import_success, Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        val intent = Intent()
                                        intent.putExtra(CONF_PATH, file.absolutePath)
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@WebActivity,
                                            R.string.import_fail, Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(this@WebActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@WebActivity, R.string.import_fail, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
