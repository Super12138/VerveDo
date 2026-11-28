package cn.super12138.todo

import android.app.Application
import cn.super12138.todo.di.VerveDoDI
import cn.super12138.todo.ui.pages.crash.CrashHandler
import cn.super12138.todo.utils.ReleaseTree
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class VerveDoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        startKoin {
            androidContext(this@VerveDoApp)
            modules(VerveDoDI.allModules)
        }

        val crashHandler = CrashHandler(applicationContext)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
    }
}