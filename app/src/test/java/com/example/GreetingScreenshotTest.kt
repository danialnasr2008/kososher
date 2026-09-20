package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.common.CurrencyDisplayMode
import com.example.ui.screens.TotalNetWorthCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun total_net_worth_card_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                TotalNetWorthCard(
                    combinedTotal = 135_970_000L,
                    isMasked = false,
                    currencyMode = CurrencyDisplayMode.TOMAN,
                    onToggleMask = {},
                    onToggleCurrency = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/balance_header.png")
    }
}
