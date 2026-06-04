package com.github.faah

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import java.awt.Toolkit
import java.io.BufferedInputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent
import kotlin.math.log10

@Service(Service.Level.APP)
class SoundPlayer {

    private val DEBOUNCE_MS = 2000L

    @Volatile
    private var lastPlayTime = 0L

    fun playAlert() {
        val now = System.currentTimeMillis()
        if (now - lastPlayTime < DEBOUNCE_MS) return
        lastPlayTime = now

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                playFromFile()
            } catch (e: Exception) {
                e.printStackTrace()
                Toolkit.getDefaultToolkit().beep()
            }
        }
    }

    private fun playFromFile() {
        val raw = SoundPlayer::class.java.getResourceAsStream("/sounds/fahhh.wav")
            ?: throw IllegalStateException("fahhh.wav not found in resources")

        val audioStream = try {
            AudioSystem.getAudioInputStream(BufferedInputStream(raw))
        } catch (e: Exception) {
            raw.close()
            throw e
        }

        val clip = AudioSystem.getClip()
        try {
            clip.open(audioStream)
            audioStream.close()

            val vol = ExceptionDetectorService.getInstance().volume
            val gain = clip.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl
            if (gain != null) {
                val db = if (vol <= 0) gain.minimum
                         else (20.0 * log10(vol / 100.0)).toFloat().coerceIn(gain.minimum, gain.maximum)
                gain.value = db
            }

            val latch = CountDownLatch(1)
            clip.addLineListener { event ->
                if (event.type == LineEvent.Type.STOP) latch.countDown()
            }
            clip.start()
            latch.await(30, TimeUnit.SECONDS)
        } finally {
            clip.close()
        }
    }

    companion object {
        fun getInstance(): SoundPlayer =
            ApplicationManager.getApplication().getService(SoundPlayer::class.java)
    }
}
