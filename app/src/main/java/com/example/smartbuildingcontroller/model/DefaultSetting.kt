package com.example.smartbuildingcontroller.model

import android.app.Activity
import android.content.Context

class EachSetting {
    fun getSetting(name: String, activity: Activity): String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return "null"
        return sharedPref.getString(name, "null")
//        if (defaultValue) {
//            with(sharedPref.edit()) {
//                putBoolean("isFirstLogin", false)
//                commit()
//            }
//        }
    }
    fun setValue(key: String,value:String,activity: Activity):Boolean{
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?:return false
        with(sharedPref.edit()) {
                putString(key,value)
                commit()
            return true
            }
    }
}