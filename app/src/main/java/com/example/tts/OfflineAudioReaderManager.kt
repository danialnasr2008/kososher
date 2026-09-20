package com.example.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class OfflineAudioReaderManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isTtsReady = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSentenceIndex = MutableStateFlow(0)
    val currentSentenceIndex: StateFlow<Int> = _currentSentenceIndex.asStateFlow()

    private val _speed = MutableStateFlow(1.0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private var currentSentences: List<String> = emptyList()

    init {
        // Will be configured in onInit
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            // Attempt setting Persian / Arabic locale or default
            val faLocale = Locale("fa", "IR")
            val available = tts?.isLanguageAvailable(faLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
            if (available >= TextToSpeech.LANG_AVAILABLE) {
                tts?.language = faLocale
            } else {
                tts?.language = Locale.getDefault()
            }
            tts?.setSpeechRate(_speed.value)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                }

                override fun onDone(utteranceId: String?) {
                    val next = _currentSentenceIndex.value + 1
                    if (next < currentSentences.size && _isPlaying.value) {
                        _currentSentenceIndex.value = next
                        speakSentence(next)
                    } else {
                        _isPlaying.value = false
                    }
                }

                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                }
            })
        }
    }

    fun startReading(sentences: List<String>, startIndex: Int = 0) {
        if (sentences.isEmpty()) return
        currentSentences = sentences
        val clampedIndex = startIndex.coerceIn(0, sentences.size - 1)
        _currentSentenceIndex.value = clampedIndex
        _isPlaying.value = true
        speakSentence(clampedIndex)
    }

    private fun speakSentence(index: Int) {
        if (index !in currentSentences.indices) {
            _isPlaying.value = false
            return
        }
        val text = currentSentences[index].trim()
        if (text.isEmpty()) {
            val next = index + 1
            if (next < currentSentences.size) {
                _currentSentenceIndex.value = next
                speakSentence(next)
            } else {
                _isPlaying.value = false
            }
            return
        }

        if (isTtsReady && tts != null) {
            tts?.setSpeechRate(_speed.value)
            val utteranceId = "sentence_$index"
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        _isPlaying.value = false
        tts?.stop()
    }

    fun resume() {
        if (currentSentences.isNotEmpty()) {
            _isPlaying.value = true
            speakSentence(_currentSentenceIndex.value)
        }
    }

    fun skipNext() {
        if (_currentSentenceIndex.value < currentSentences.size - 1) {
            _currentSentenceIndex.value++
            if (_isPlaying.value) {
                speakSentence(_currentSentenceIndex.value)
            }
        }
    }

    fun skipPrevious() {
        if (_currentSentenceIndex.value > 0) {
            _currentSentenceIndex.value--
            if (_isPlaying.value) {
                speakSentence(_currentSentenceIndex.value)
            }
        }
    }

    fun jumpToSentence(index: Int) {
        if (index in currentSentences.indices) {
            _currentSentenceIndex.value = index
            _isPlaying.value = true
            speakSentence(index)
        }
    }

    fun setSpeed(newSpeed: Float) {
        _speed.value = newSpeed
        tts?.setSpeechRate(newSpeed)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
