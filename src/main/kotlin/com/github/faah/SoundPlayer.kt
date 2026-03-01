package com.github.faah

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import javazoom.jl.player.Player
import java.awt.Toolkit

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
                Toolkit.getDefaultToolkit().beep()
            }
        }
    }

    private fun playFromFile() {
        val stream = SoundPlayer::class.java.getResourceAsStream("/sounds/fahhh.mp3")
            ?: throw IllegalStateException("fahhh.mp3 not found in resources")

        val player = Player(stream)
        player.play()
        player.close()
    }

    companion object {
        fun getInstance(): SoundPlayer =
            ApplicationManager.getApplication().getService(SoundPlayer::class.java)
    }
}
