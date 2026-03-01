package com.github.faah

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.util.messages.Topic

@Service(Service.Level.APP)
class ExceptionDetectorService {

    // Listeners are notified whenever the master enabled flag changes
    fun interface EnabledListener {
        fun onEnabledChanged(enabled: Boolean)
    }

    @Volatile
    var isEnabled: Boolean = false
        set(value) {
            field = value
            ApplicationManager.getApplication().messageBus
                .syncPublisher(ENABLED_TOPIC)
                .onEnabledChanged(value)
        }

    // Immutable snapshot — replaced atomically on each change (safe for concurrent reads)
    @Volatile
    private var enabledTypes: Set<ErrorType> = ErrorType.entries.toSet()

    @Volatile
    private var pattern: Regex = buildPattern(enabledTypes)

    fun isTypeEnabled(type: ErrorType): Boolean = type in enabledTypes

    fun setTypeEnabled(type: ErrorType, enabled: Boolean) {
        enabledTypes = if (enabled) enabledTypes + type else enabledTypes - type
        pattern = buildPattern(enabledTypes)
    }

    fun isExceptionLine(line: String): Boolean =
        isEnabled && pattern.containsMatchIn(line)

    private fun buildPattern(types: Set<ErrorType>): Regex {
        if (types.isEmpty()) return Regex("(?!)")   // never matches
        val combined = types.joinToString("|") { "(${it.pattern})" }
        return Regex(combined, setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
    }

    companion object {
        val ENABLED_TOPIC: Topic<EnabledListener> =
            Topic.create("FaahEnabledChanged", EnabledListener::class.java)

        fun getInstance(): ExceptionDetectorService =
            ApplicationManager.getApplication().getService(ExceptionDetectorService::class.java)
    }
}
