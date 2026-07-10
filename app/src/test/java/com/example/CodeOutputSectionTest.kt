package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CodeOutputSectionTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun test_code_output_section_editable_and_interactive() {
        var textState by mutableStateOf("initial code")
        var hasResetBeenClicked = false
        var hasExportBeenClicked = false

        composeTestRule.setContent {
            CodeOutputSection(
                activeTab = "css",
                activeOutputText = textState,
                onValueChange = { textState = it },
                onTabSelect = {},
                onCopyClick = {},
                onResetClick = { hasResetBeenClicked = true },
                onExportClick = { hasExportBeenClicked = true },
                hasCustom = true
            )
        }

        // Verify that the code editor displays the initial code
        composeTestRule.onNodeWithTag("code_editor_text_field").assertTextEquals("initial code")

        // Click to focus the text field first to establish input connection under Robolectric
        composeTestRule.onNodeWithTag("code_editor_text_field").performClick()
        composeTestRule.waitForIdle()

        // Input new text into the editable code area
        composeTestRule.onNodeWithTag("code_editor_text_field").performTextInput(" and additions")

        // Wait for Compose to finish state propagation and recompose
        composeTestRule.waitForIdle()

        // Verify that the code editor displays the updated code
        composeTestRule.onNodeWithTag("code_editor_text_field").assertTextEquals("initial code and additions")

        // Perform click on the Reset Button
        composeTestRule.onNodeWithTag("reset_code_button").performClick()
        composeTestRule.waitForIdle()
        assert(hasResetBeenClicked)

        // Perform click on the Export Button
        composeTestRule.onNodeWithTag("export_code_button").performClick()
        composeTestRule.waitForIdle()
        assert(hasExportBeenClicked)
    }
}
