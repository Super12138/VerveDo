package cn.super12138.todo.ui.navigation

import androidx.navigation3.runtime.NavKey
import cn.super12138.todo.logic.database.TodoEntity
import kotlinx.serialization.Serializable

@Serializable
sealed class VerveDoScreen : NavKey {
    @Serializable
    data object Overview : VerveDoScreen()

    @Serializable
    data object Tasks : VerveDoScreen()

    @Serializable
    sealed class Settings : VerveDoScreen() {
        @Serializable
        data object Main : Settings()

        @Serializable
        data object Appearance : Settings()

        @Serializable
        data object Interface : Settings()

        @Serializable
        data object Data : Settings()

        @Serializable
        data object DataCategory : Settings()

        @Serializable
        data object About : Settings()

        // @Serializable
        // data object AboutEasterEgg : Settings()

        @Serializable
        data object AboutLicence : Settings()

        @Serializable
        data object DeveloperOptions : Settings()

        @Serializable
        data object DeveloperOptionsPadding : Settings()
    }

    @Serializable
    sealed class Editor : VerveDoScreen() {
        @Serializable
        data object Add : Editor()

        @Serializable
        data class Edit(val toDo: TodoEntity) : Editor()
    }
}
