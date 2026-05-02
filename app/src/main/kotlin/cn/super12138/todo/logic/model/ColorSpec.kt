package cn.super12138.todo.logic.model

import com.kyant.m3color.dynamiccolor.ColorSpec
import com.kyant.m3color.dynamiccolor.DynamicScheme

enum class ColorSpecVersion(val id: Int) {
    Spec2021(1),
    Spec2025(2),
    Spec2026(3);

    companion object {
        fun fromId(id: Int) = entries.firstOrNull { it.id == id } ?: Spec2021
    }
}

enum class DynamicSchemePlatform(val id: Int) {
    Phone(1),
    Watch(2);

    companion object {
        fun fromId(id: Int) = entries.firstOrNull { it.id == id } ?: Phone
    }
}

fun ColorSpecVersion.toSpecVersion(): ColorSpec.SpecVersion {
    return when (this) {
        ColorSpecVersion.Spec2021 -> ColorSpec.SpecVersion.SPEC_2021
        ColorSpecVersion.Spec2025 -> ColorSpec.SpecVersion.SPEC_2025
        ColorSpecVersion.Spec2026 -> ColorSpec.SpecVersion.SPEC_2026
    }
}

fun DynamicSchemePlatform.toPlatform(): DynamicScheme.Platform {
    return when (this) {
        DynamicSchemePlatform.Phone -> DynamicScheme.Platform.PHONE
        DynamicSchemePlatform.Watch -> DynamicScheme.Platform.WATCH
    }
}