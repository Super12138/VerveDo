package cn.super12138.todo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.super12138.todo.logic.database.TodoDatabase
import cn.super12138.todo.ui.pages.crash.CrashHandler

class VerveDoApp : Application() {
    private val database by lazy { TodoDatabase.getDatabase(this) }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var db: TodoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = database
        context = applicationContext

        val crashHandler = CrashHandler(applicationContext)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
    }
}