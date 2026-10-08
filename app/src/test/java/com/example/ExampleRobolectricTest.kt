package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Letter
import com.example.data.model.LetterFormatOptions
import com.example.data.model.LetterTone
import com.example.data.model.LetterType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VoiceLetter AI", appName)
    }

    @Test
    fun `letter placeholders extraction and replacement`() {
        val letter = Letter(
            title = "Leave Request",
            letterType = LetterType.LEAVE_APPLICATION,
            tone = LetterTone.FORMAL,
            senderDetails = "[NAME]\n[CLASS]",
            subject = "Leave for [STUDENT NAME]",
            bodyParagraphs = listOf("Please grant leave to [NAME] on [DATE].")
        )

        val placeholders = letter.extractPlaceholders()
        assertTrue(placeholders.contains("[NAME]"))
        assertTrue(placeholders.contains("[CLASS]"))
        assertTrue(placeholders.contains("[STUDENT NAME]"))
        assertTrue(placeholders.contains("[DATE]"))

        val replaced = letter.replacePlaceholder("[NAME]", "Rahul Sharma")
        assertEquals("Rahul Sharma\n[CLASS]", replaced.senderDetails)
        assertEquals("Please grant leave to Rahul Sharma on [DATE].", replaced.bodyParagraphs[0])
    }

    @Test
    fun `letter full text assembly includes all sections`() {
        val letter = Letter(
            title = "Test Letter",
            senderDetails = "Sender Info",
            dateString = "06 October 2026",
            recipientDetails = "Principal Office",
            subject = "Leave Application",
            salutation = "Dear Sir,",
            bodyParagraphs = listOf("Paragraph 1 content.", "Paragraph 2 content."),
            complimentaryClose = "Yours faithfully,",
            signerName = "Test Signer"
        )

        val text = letter.assembleFullText()
        assertTrue(text.contains("Sender Info"))
        assertTrue(text.contains("Date: 06 October 2026"))
        assertTrue(text.contains("To,\nPrincipal Office"))
        assertTrue(text.contains("Subject: Leave Application"))
        assertTrue(text.contains("Paragraph 1 content."))
        assertTrue(text.contains("Test Signer"))
    }
}
