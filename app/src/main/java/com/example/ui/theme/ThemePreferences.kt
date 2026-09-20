package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeMode(val title: String) {
    DARK("تاریک"),
    LIGHT("روشن"),
    SYSTEM("خودکار سیستم")
}

enum class ColorPaletteOption(
    val title: String,
    val primarySample: Color,
    val darkPrimary: Color,
    val lightPrimary: Color
) {
    EMERALD(
        title = "سبز اروند",
        primarySample = Color(0xFF006C4C),
        darkPrimary = EmeraldPrimaryDark,
        lightPrimary = EmeraldPrimaryLight
    ),
    OCEAN(
        title = "آبی اقیانوس",
        primarySample = Color(0xFF00629E),
        darkPrimary = OceanPrimaryDark,
        lightPrimary = OceanPrimaryLight
    ),
    PURPLE(
        title = "ارکیده اکسپنسیو",
        primarySample = Color(0xFF7845AC),
        darkPrimary = PurplePrimaryDark,
        lightPrimary = PurplePrimaryLight
    ),
    AMBER(
        title = "کهربایی لوکس",
        primarySample = Color(0xFF8A5100),
        darkPrimary = AmberPrimaryDark,
        lightPrimary = AmberPrimaryLight
    ),
    CRIMSON(
        title = "یاقوتی مدرن",
        primarySample = Color(0xFFB31B2A),
        darkPrimary = CrimsonPrimaryDark,
        lightPrimary = CrimsonPrimaryLight
    )
}
