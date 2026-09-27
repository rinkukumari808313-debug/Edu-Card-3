package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

class EduTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    var isReady = mutableStateOf(false)
        private set
    var isSpeaking = mutableStateOf(false)
        private set

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isReady.value = true
                tts?.setSpeechRate(0.95f)
                tts?.setPitch(1.0f)
            }
        }
    }

    fun speak(text: String) {
        if (!isReady.value || text.isBlank()) return
        stop()
        isSpeaking.value = true
        // Strip markdown asterisks and hashtags for natural speech
        val cleanedText = text
            .replace("**", "")
            .replace("*", "")
            .replace("###", "")
            .replace("##", "")
            .replace("#", "")
            .replace("`", "")

        tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, "EduTtsUtteranceId")
    }

    fun stop() {
        tts?.stop()
        isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
