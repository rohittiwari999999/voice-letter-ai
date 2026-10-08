package com.example

import com.example.data.ai.AiLetterGenerator
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCleanUserConversationalFluff_bareIntent() {
    val result = AiLetterGenerator.cleanUserConversationalFluff("ek letter banana hai")
    assertEquals("", result)
  }

  @Test
  fun testCleanUserConversationalFluff_withTopic() {
    val result = AiLetterGenerator.cleanUserConversationalFluff("ek letter banana hai principal ko chhutti ke liye")
    assertEquals("principal ko chhutti ke liye", result)
  }

  @Test
  fun testCleanUserConversationalFluff_hindiPrefix() {
    val result = AiLetterGenerator.cleanUserConversationalFluff("एक पत्र बनाना है कि मैं बीमार हूँ")
    assertEquals("मैं बीमार हूँ", result)
  }

  @Test
  fun testLetterRemoveWord() {
    val letter = com.example.data.model.Letter(
      subject = "Application for viral fever leave",
      bodyParagraphs = listOf("I am suffering from viral fever since yesterday.")
    )
    val updated = letter.removeWord("viral fever")
    assertFalse(updated.bodyParagraphs[0].contains("viral fever"))
  }

  @Test
  fun testLetterRemoveLastSentence() {
    val letter = com.example.data.model.Letter(
      bodyParagraphs = listOf("First sentence. Second sentence. Third sentence.")
    )
    val updated = letter.removeLastSentence()
    assertEquals("First sentence. Second sentence.", updated.bodyParagraphs[0])
  }
}
