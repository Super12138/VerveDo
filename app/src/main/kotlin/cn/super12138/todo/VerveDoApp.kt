package cn.super12138.todo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.super12138.todo.logic.database.TaskDatabase
import cn.super12138.todo.ui.pages.crash.CrashHandler

class VerveDoApp : Application() {
    private val database by lazy { TaskDatabase.getDatabase(this) }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var taskDatabase: TaskDatabase
    }

    override fun onCreate() {
        super.onCreate()

        taskDatabase = database
        context = applicationContext

        val crashHandler = CrashHandler(applicationContext)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
    }
}