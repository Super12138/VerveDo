package cn.super12138.todo

import android.app.Application
import cn.super12138.todo.di.VerveDoDI
import cn.super12138.todo.ui.pages.crash.CrashHandler
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VerveDoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VerveDoApp)
            modules(VerveDoDI.allModules)
        }

        val crashHandler = CrashHandler(applicationContext)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
    }
}