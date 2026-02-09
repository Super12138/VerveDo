package cn.super12138.todo.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import cn.super12138.todo.ui.pages.editor.TaskAddPage
import cn.super12138.todo.ui.pages.editor.TaskEditPage
import cn.super12138.todo.ui.pages.overview.OverviewPage
import cn.super12138.todo.ui.pages.settings.SettingsAbout
import cn.super12138.todo.ui.pages.settings.SettingsAboutLicence
import cn.super12138.todo.ui.pages.settings.SettingsAppearance
import cn.super12138.todo.ui.pages.settings.SettingsData
import cn.super12138.todo.ui.pages.settings.SettingsDataCategory
import cn.super12138.todo.ui.pages.settings.SettingsDeveloperOptions
import cn.super12138.todo.ui.pages.settings.SettingsDeveloperOptionsPadding
import cn.super12138.todo.ui.pages.settings.SettingsInterface
import cn.super12138.todo.ui.pages.settings.SettingsMain
import cn.super12138.todo.ui.pages.tasks.TasksPage
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.ui.theme.materialSharedAxisX
import cn.super12138.todo.ui.theme.veilFade
import cn.super12138.todo.ui.viewmodels.MainViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopNavigation(
    backStack: TopLevelBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    fun onBack() {
        backStack.removeLast()
    }

    val veilColor = MaterialTheme.colorScheme.surfaceDim
    fun editorTransition() = NavDisplay.transitionSpec {
        veilFade(veilColor)
    } + NavDisplay.popTransitionSpec {
        veilFade(veilColor)
    } + NavDisplay.predictivePopTransitionSpec {
        veilFade(veilColor)
    }

    val initialOffestFactor = 0.10f
    fun settingsTransition() = NavDisplay.transitionSpec {
        materialSharedAxisX(
            initialOffsetX = { (it * initialOffestFactor).toInt() },
            targetOffsetX = { -(it * initialOffestFactor).toInt() }
        )
    } + NavDisplay.popTransitionSpec {
        materialSharedAxisX(
            initialOffsetX = { -(it * initialOffestFactor).toInt() },
            targetOffsetX = { (it * initialOffestFactor).toInt() }
        )
    } + NavDisplay.predictivePopTransitionSpec {
        materialSharedAxisX(
            initialOffsetX = { -(it * initialOffestFactor).toInt() },
            targetOffsetX = { (it * initialOffestFactor).toInt() }
        )
    }

    val defaultTransition = fadeScale(
        effectSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack.backStack,
            onBack = ::onBack,
            transitionSpec = { defaultTransition },
            popTransitionSpec = { defaultTransition },
            predictivePopTransitionSpec = { defaultTransition },
            entryProvider = entryProvider {
                entry<VerveDoScreen.Overview> {
                    OverviewPage(viewModel = viewModel)
                }

                entry<VerveDoScreen.Tasks> {
                    TasksPage(
                        viewModel = viewModel,
                        toTodoAddPage = { backStack.add(VerveDoScreen.Editor.Add) },
                        toTodoEditPage = { backStack.add(VerveDoScreen.Editor.Edit(it)) }
                    )
                }

                entry<VerveDoScreen.Editor.Add>(metadata = editorTransition()) {
                    TaskAddPage(
                        onSave = {
                            viewModel.addTodo(it)
                            onBack()
                        },
                        onNavigateUp = ::onBack
                    )
                }

                entry<VerveDoScreen.Editor.Edit>(metadata = editorTransition()) { editorArgs ->
                    TaskEditPage(
                        toDo = editorArgs.toDo,
                        onSave = {
                            viewModel.addTodo(it)
                            // 如果原来的待办状态为未完成并且修改后状态为完成
                            if (!editorArgs.toDo.isCompleted && it.isCompleted) {
                                viewModel.playConfetti()
                            }
                            onBack()
                        },
                        onDelete = {
                            viewModel.deleteTodo(editorArgs.toDo)
                            onBack()
                        },
                        onNavigateUp = ::onBack
                    )
                }

                entry<VerveDoScreen.Settings.Main> {
                    SettingsMain(
                        toAppearancePage = { backStack.add(VerveDoScreen.Settings.Appearance) },
                        toAboutPage = { backStack.add(VerveDoScreen.Settings.About) },
                        toInterfacePage = { backStack.add(VerveDoScreen.Settings.Interface) },
                        toDataPage = { backStack.add(VerveDoScreen.Settings.Data) },
                    )
                }

                entry<VerveDoScreen.Settings.Appearance>(metadata = settingsTransition()) {
                    SettingsAppearance(onNavigateUp = ::onBack)
                }

                entry<VerveDoScreen.Settings.Interface>(metadata = settingsTransition()) {
                    SettingsInterface(onNavigateUp = ::onBack)
                }

                entry<VerveDoScreen.Settings.Data>(metadata = settingsTransition()) {
                    SettingsData(
                        viewModel = viewModel,
                        toCategoryManager = { backStack.add(VerveDoScreen.Settings.DataCategory) },
                        onNavigateUp = ::onBack
                    )
                }

                entry<VerveDoScreen.Settings.DataCategory>(metadata = settingsTransition()) {
                    SettingsDataCategory(onNavigateUp = ::onBack)
                }

                entry<VerveDoScreen.Settings.About>(metadata = settingsTransition()) {
                    SettingsAbout(
                        //toSpecialPage = { backStack.add(TodoScreen.Settings.AboutSpecial) },
                        toLicencePage = { backStack.add(VerveDoScreen.Settings.AboutLicence) },
                        toDevPage = { backStack.add(VerveDoScreen.Settings.DeveloperOptions) },
                        onNavigateUp = ::onBack,
                    )
                }

                /*entry<TodoScreen.Settings.AboutSpecial>(metadata = settingsTransition()) {
                    SettingsAboutSpecial(viewModel = viewModel)
                }*/

                entry<VerveDoScreen.Settings.AboutLicence>(metadata = settingsTransition()) {
                    SettingsAboutLicence(onNavigateUp = ::onBack)
                }

                entry<VerveDoScreen.Settings.DeveloperOptions>(metadata = settingsTransition()) {
                    SettingsDeveloperOptions(
                        toPaddingPage = { backStack.add(VerveDoScreen.Settings.DeveloperOptionsPadding) },
                        onNavigateUp = ::onBack
                    )
                }
                entry<VerveDoScreen.Settings.DeveloperOptionsPadding>(metadata = settingsTransition()) {
                    SettingsDeveloperOptionsPadding(onNavigateUp = ::onBack)
                }
            },
            modifier = modifier,
        )
    }
}