package com.suda.datetimewallpaper.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.base.BaseAct
import com.suda.datetimewallpaper.bean.City
import com.suda.datetimewallpaper.model.CityDao
import com.suda.datetimewallpaper.util.SharedPreferencesUtil
import com.suda.datetimewallpaper.util.SharedPreferencesUtil.*
import com.suda.datetimewallpaper.util.setExcludeFromRecents
import me.drakeet.materialdialog.MaterialDialog


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

    val cityDao by lazy {
        CityDao(activity)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.addPreferencesFromResource(R.xml.pref_general)
        val sp = SharedPreferencesUtil.getAppDefault(activity)
        val areaName = sp.getData(AREA_NAME, "")
        if (!TextUtils.isEmpty(areaName)) {
            findPreference("weather_settings_area").summary = areaName
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == "hide_from_recent") {
            if (preference is SwitchPreference) {
                activity.setExcludeFromRecents(preference.isChecked)
            }
        } else if (preference.key == "weather_settings_area") {
            val dialog = MaterialDialog(activity)
            dialog.setTitle(R.string.weather_settings_area)

            val viewGroup = LayoutInflater.from(activity).inflate(R.layout.area_select, null)
            dialog.setContentView(viewGroup)
            val autoText = viewGroup.findViewById<AutoCompleteTextView>(R.id.at_area)

            val areas = mutableListOf<City>()

            autoText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    areas.clear()
                    areas.addAll(cityDao.getCityByCityOrArea(s.toString()))
                }

            })

            val adapter = MyArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, areas)
            autoText.setAdapter(adapter)

            dialog.setPositiveButton(R.string.ok) {
                val result = autoText.text.toString()
                if (!TextUtils.isEmpty(result)) {
                    try {
                        val areaCode = result.split(",")[2]
                        val cityName = result.split(",")[0]
                        val areaName = result.split(",")[1]

                        val sp = SharedPreferencesUtil.getAppDefault(activity)
                        sp.putData(AREA_NAME, "$cityName,$areaName")
                        sp.putData(AREA_CODE, "$areaCode")
                        sp.putData(AREA_WEATHER, "")
                        preference.summary = "$cityName,$areaName"
                    } catch (e: Exception) {
                        Toast.makeText(activity, "输入有误", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "输入有误", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            dialog.setNegativeButton(R.string.cancel) {
                dialog.dismiss()
            }
            dialog.show()
        }
        return super.onPreferenceTreeClick(preference)
    }


    class MyArrayAdapter(context: Context, resource: Int, val objects: MutableList<City>) :
        ArrayAdapter<City>(context, resource, objects) {


        override fun getFilter(): Filter {
            return MyFilter()
        }

        private inner class MyFilter : Filter() {

            override fun performFiltering(prefix: CharSequence?): Filter.FilterResults {
                val results = Filter.FilterResults()

                val newList = mutableListOf<City>()
                objects.forEach {
                    if (it.toString().contains(prefix!!)) {
                        newList.add(it)
                    }
                }
                results.values = newList
                results.count = newList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
                results?.run {
                    objects.clear()
                    if (this.count > 0) {
                        objects.addAll(results.values as List<City>)
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }

            }
        }
    }

}
