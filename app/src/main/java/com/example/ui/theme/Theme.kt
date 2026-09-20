package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getDarkColorScheme(palette: ColorPaletteOption): ColorScheme {
    return when (palette) {
        ColorPaletteOption.EMERALD -> darkColorScheme(
            primary = EmeraldPrimaryDark,
            onPrimary = EmeraldOnPrimaryDark,
            primaryContainer = EmeraldPrimaryContainerDark,
            onPrimaryContainer = EmeraldOnPrimaryContainerDark,
            secondary = Color(0xFFB3CCBE),
            onSecondary = Color(0xFF1F352B),
            secondaryContainer = Color(0xFF354B40),
            onSecondaryContainer = Color(0xFFCFE8D9),
            tertiary = Color(0xFFA5CDDE),
            onTertiary = Color(0xFF063543),
            tertiaryContainer = Color(0xFF234C5A),
            onTertiaryContainer = Color(0xFFC1E8FB),
            background = Color(0xFF0F1512),
            onBackground = Color(0xFFDEE4DF),
            surface = Color(0xFF131B16),
            onSurface = Color(0xFFDEE4DF),
            surfaceVariant = Color(0xFF202C24),
            onSurfaceVariant = Color(0xFFBFC9C2),
            outline = Color(0xFF89938C),
            outlineVariant = Color(0xFF3F4943),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.OCEAN -> darkColorScheme(
            primary = OceanPrimaryDark,
            onPrimary = OceanOnPrimaryDark,
            primaryContainer = OceanPrimaryContainerDark,
            onPrimaryContainer = OceanOnPrimaryContainerDark,
            secondary = Color(0xFFB9C8DA),
            onSecondary = Color(0xFF243240),
            secondaryContainer = Color(0xFF3A4857),
            onSecondaryContainer = Color(0xFFD5E4F7),
            tertiary = Color(0xFFD4BEE6),
            onTertiary = Color(0xFF392A4A),
            tertiaryContainer = Color(0xFF514061),
            onTertiaryContainer = Color(0xFFEFDAFF),
            background = Color(0xFF0E141B),
            onBackground = Color(0xFFDEE3EA),
            surface = Color(0xFF121B24),
            onSurface = Color(0xFFDEE3EA),
            surfaceVariant = Color(0xFF202C3A),
            onSurfaceVariant = Color(0xFFBFC7D2),
            outline = Color(0xFF8A929C),
            outlineVariant = Color(0xFF414751),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.PURPLE -> darkColorScheme(
            primary = PurplePrimaryDark,
            onPrimary = PurpleOnPrimaryDark,
            primaryContainer = PurplePrimaryContainerDark,
            onPrimaryContainer = PurpleOnPrimaryContainerDark,
            secondary = Color(0xFFD0C1DA),
            onSecondary = Color(0xFF362C3F),
            secondaryContainer = Color(0xFF4D4257),
            onSecondaryContainer = Color(0xFFECDCF7),
            tertiary = Color(0xFFF2B7C1),
            onTertiary = Color(0xFF4B252E),
            tertiaryContainer = Color(0xFF653B44),
            onTertiaryContainer = Color(0xFFFFD9DF),
            background = Color(0xFF140F18),
            onBackground = Color(0xFFE8E0E9),
            surface = Color(0xFF1A1420),
            onSurface = Color(0xFFE8E0E9),
            surfaceVariant = Color(0xFF292132),
            onSurfaceVariant = Color(0xFFCCC3D0),
            outline = Color(0xFF958E9A),
            outlineVariant = Color(0xFF4A444E),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.AMBER -> darkColorScheme(
            primary = AmberPrimaryDark,
            onPrimary = AmberOnPrimaryDark,
            primaryContainer = AmberPrimaryContainerDark,
            onPrimaryContainer = AmberOnPrimaryContainerDark,
            secondary = Color(0xFFDDC2A2),
            onSecondary = Color(0xFF3E2D16),
            secondaryContainer = Color(0xFF56432B),
            onSecondaryContainer = Color(0xFFFADEBD),
            tertiary = Color(0xFFB8CEA2),
            onTertiary = Color(0xFF243616),
            tertiaryContainer = Color(0xFF3A4D2B),
            onTertiaryContainer = Color(0xFFD4EABB),
            background = Color(0xFF16120E),
            onBackground = Color(0xFFECE0D7),
            surface = Color(0xFF1E1914),
            onSurface = Color(0xFFECE0D7),
            surfaceVariant = Color(0xFF302821),
            onSurfaceVariant = Color(0xFFD2C4B7),
            outline = Color(0xFF9B8E82),
            outlineVariant = Color(0xFF4E443B),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.CRIMSON -> darkColorScheme(
            primary = CrimsonPrimaryDark,
            onPrimary = CrimsonOnPrimaryDark,
            primaryContainer = CrimsonPrimaryContainerDark,
            onPrimaryContainer = CrimsonOnPrimaryContainerDark,
            secondary = Color(0xFFE7BDBB),
            onSecondary = Color(0xFF442929),
            secondaryContainer = Color(0xFF5D3F3F),
            onSecondaryContainer = Color(0xFFFFDAD8),
            tertiary = Color(0xFFE6C38C),
            onTertiary = Color(0xFF422C05),
            tertiaryContainer = Color(0xFF5C4219),
            onTertiaryContainer = Color(0xFFFFDFAE),
            background = Color(0xFF180F10),
            onBackground = Color(0xFFF0DFDE),
            surface = Color(0xFF221516),
            onSurface = Color(0xFFF0DFDE),
            surfaceVariant = Color(0xFF372325),
            onSurfaceVariant = Color(0xFFD7C1C1),
            outline = Color(0xFFA08C8B),
            outlineVariant = Color(0xFF524343),
            error = ExpenseRed,
            onError = Color.White
        )
    }
}

fun getLightColorScheme(palette: ColorPaletteOption): ColorScheme {
    return when (palette) {
        ColorPaletteOption.EMERALD -> lightColorScheme(
            primary = EmeraldPrimaryLight,
            onPrimary = EmeraldOnPrimaryLight,
            primaryContainer = EmeraldPrimaryContainerLight,
            onPrimaryContainer = EmeraldOnPrimaryContainerLight,
            secondary = Color(0xFF4C6357),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFCFE9D9),
            onSecondaryContainer = Color(0xFF092016),
            tertiary = Color(0xFF3D6373),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFC1E8FB),
            onTertiaryContainer = Color(0xFF001F29),
            background = Color(0xFFF6FBF7),
            onBackground = Color(0xFF171D1A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF171D1A),
            surfaceVariant = Color(0xFFDCE5DF),
            onSurfaceVariant = Color(0xFF404944),
            outline = Color(0xFF707974),
            outlineVariant = Color(0xFFC0C9C3),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.OCEAN -> lightColorScheme(
            primary = OceanPrimaryLight,
            onPrimary = OceanOnPrimaryLight,
            primaryContainer = OceanPrimaryContainerLight,
            onPrimaryContainer = OceanOnPrimaryContainerLight,
            secondary = Color(0xFF526070),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFD5E4F7),
            onSecondaryContainer = Color(0xFF0F1D2A),
            tertiary = Color(0xFF69577D),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFEFDAFF),
            onTertiaryContainer = Color(0xFF241537),
            background = Color(0xFFF8FAFD),
            onBackground = Color(0xFF181C20),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF181C20),
            surfaceVariant = Color(0xFFDFE3EB),
            onSurfaceVariant = Color(0xFF42474E),
            outline = Color(0xFF72777F),
            outlineVariant = Color(0xFFC2C7CF),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.PURPLE -> lightColorScheme(
            primary = PurplePrimaryLight,
            onPrimary = PurpleOnPrimaryLight,
            primaryContainer = PurplePrimaryContainerLight,
            onPrimaryContainer = PurpleOnPrimaryContainerLight,
            secondary = Color(0xFF655A6F),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFECDCF7),
            onSecondaryContainer = Color(0xFF21172A),
            tertiary = Color(0xFF7E525C),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFD9DF),
            onTertiaryContainer = Color(0xFF32101A),
            background = Color(0xFFFDF7FF),
            onBackground = Color(0xFF1D1A20),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1D1A20),
            surfaceVariant = Color(0xFFE9DFEB),
            onSurfaceVariant = Color(0xFF4A444F),
            outline = Color(0xFF7B7480),
            outlineVariant = Color(0xFFCCC3D0),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.AMBER -> lightColorScheme(
            primary = AmberPrimaryLight,
            onPrimary = AmberOnPrimaryLight,
            primaryContainer = AmberPrimaryContainerLight,
            onPrimaryContainer = AmberOnPrimaryContainerLight,
            secondary = Color(0xFF705B41),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFADEBD),
            onSecondaryContainer = Color(0xFF271905),
            tertiary = Color(0xFF52643E),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFD5EABA),
            onTertiaryContainer = Color(0xFF111F03),
            background = Color(0xFFFFF8F3),
            onBackground = Color(0xFF201A14),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF201A14),
            surfaceVariant = Color(0xFFEFE0D3),
            onSurfaceVariant = Color(0xFF4F453B),
            outline = Color(0xFF81756A),
            outlineVariant = Color(0xFFD2C4B7),
            error = ExpenseRed,
            onError = Color.White
        )
        ColorPaletteOption.CRIMSON -> lightColorScheme(
            primary = CrimsonPrimaryLight,
            onPrimary = CrimsonOnPrimaryLight,
            primaryContainer = CrimsonPrimaryContainerLight,
            onPrimaryContainer = CrimsonOnPrimaryContainerLight,
            secondary = Color(0xFF775656),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFDAD8),
            onSecondaryContainer = Color(0xFF2C1515),
            tertiary = Color(0xFF765A2E),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFDFA7),
            onTertiaryContainer = Color(0xFF291A00),
            background = Color(0xFFFFF8F7),
            onBackground = Color(0xFF221919),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF221919),
            surfaceVariant = Color(0xFFF4DDDC),
            onSurfaceVariant = Color(0xFF534343),
            outline = Color(0xFF857372),
            outlineVariant = Color(0xFFD8C2C1),
            error = ExpenseRed,
            onError = Color.White
        )
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    colorPalette: ColorPaletteOption = ColorPaletteOption.EMERALD,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemDark
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> getDarkColorScheme(colorPalette)
        else -> getLightColorScheme(colorPalette)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = ExpressiveShapes,
        typography = Typography,
        content = content
    )
}
