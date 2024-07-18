package com.adi121.statussaverupdate.utils
import android.content.Context
import android.content.SharedPreferences


class SharedPrefs private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor

    init {
        editor = sharedPreferences.edit()
    }
    companion object {
        private var sharedPrefs: SharedPrefs? = null
        fun getInstance(context: Context): SharedPrefs? {
            if (sharedPrefs == null) {
                sharedPrefs = SharedPrefs(context)
            }
            return sharedPrefs
        }
    }

    var businessUri:String?
    get() = sharedPreferences.getString("businessUri","")
    set(value) {
        editor.putString("businessUri",value).apply()
    }

    var uri: String?
        get() = sharedPreferences.getString("uri", "")
    set(value) {
        editor.putString("uri",value).apply()
    }

    var isWhatsAppMode:Boolean
    get() = sharedPreferences.getBoolean("isWhatsAppMode",true)
    set(value){
        editor.putBoolean("isWhatsAppMode",value).apply()
    }

    var isDarkTheme :Boolean
        get() = sharedPreferences.getBoolean("isDarkTheme",false)
        set(value) {
            editor.putBoolean("isDarkTheme",value).apply()
        }


    var lastAdShownTime:Long
    get()=sharedPreferences.getLong("lastAdShownTime",0L)
    set(value) {
        editor.putLong("lastAdShownTime",value).apply()
    }


    var isAppRated :Boolean
        get() = sharedPreferences.getBoolean("isAppRated",false)
        set(value) {
            editor.putBoolean("isAppRated",value).apply()
        }


    var appOpenedCounter:Int
    get()= sharedPreferences.getInt("appOpenedCounter",0)
    set(value) {
        editor.putInt("appOpenedCounter",value).apply()
    }

    var isFirstTime:Boolean
        get()=sharedPreferences.getBoolean("isFirstTime",true)
        set(value) {
            editor.putBoolean("isFirstTime",value).apply()
        }


}