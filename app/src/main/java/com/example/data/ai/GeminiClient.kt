package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    @Volatile
    var customApiKey: String? = null

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getEffectiveApiKey(): String {
        val custom = customApiKey?.trim()
        if (!custom.isNullOrBlank()) return custom

        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }

    suspend fun generateContent(prompt: String, systemInstruction: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured or is placeholder. Using smart local fallback.")
            return@withContext Result.failure(IllegalStateException("API_KEY_NOT_CONFIGURED"))
        }

        try {
            val rootJson = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()

            val textPart = JSONObject()
            textPart.put("text", prompt)
            partsArray.put(textPart)

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            if (!systemInstruction.isNullOrBlank()) {
                val sysInstObj = JSONObject()
                val sysParts = JSONArray()
                val sysTextPart = JSONObject()
                sysTextPart.put("text", systemInstruction)
                sysParts.put(sysTextPart)
                sysInstObj.put("parts", sysParts)
                rootJson.put("systemInstruction", sysInstObj)
            }

            val genConfig = JSONObject()
            genConfig.put("temperature", 0.3)
            genConfig.put("topP", 0.95)
            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e(TAG, "Gemini API failed code: ${response.code}, msg: $responseBody")
                return@withContext Result.failure(Exception("Gemini API Error: ${response.code}"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    return@withContext Result.success(text)
                }
            }

            Result.failure(Exception("No content returned from Gemini API"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini API", e)
            Result.failure(e)
        }
    }
}
