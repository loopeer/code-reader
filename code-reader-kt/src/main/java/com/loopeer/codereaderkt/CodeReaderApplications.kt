package com.loopeer.codereaderkt

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import com.loopeer.codereaderkt.utils.ThemeUtils


class CodeReaderApplications : Application() {
    private var mInstance: CodeReaderApplications? = null
    private var sAppContext: Context? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        sAppContext = applicationContext
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build())
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getCurrentNightMode(this))
    }

    fun getAppContext(): Context? {
        return sAppContext
    }

    fun  getInstance(): CodeReaderApplications? {
        return mInstance
    }
}