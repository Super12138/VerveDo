package cn.super12138.todo.utils

import android.util.Log
import cn.super12138.todo.constants.Constants
import timber.log.Timber

class ReleaseTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }
        super.log(priority, "${Constants.TAG}$tag", message, t)
    }
}
