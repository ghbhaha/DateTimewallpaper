package com.suda.datetimewallpaper.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.base.BaseAct
import com.suda.datetimewallpaper.util.setExcludeFromRecents

class SettingsActivity : BaseAct() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        this.fragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingFragment())
            .commit()
    }

}

class SettingFragment : androidx.preference.PreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.addPreferencesFromResource(R.xml.pref_general);
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "hide_from_recent") {
            if (preference is SwitchPreference) {
                activity.setExcludeFromRecents(preference.isChecked)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}
