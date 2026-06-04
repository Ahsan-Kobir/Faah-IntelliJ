package com.github.faah

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.messages.Topic

@State(name = "FaahSettings", storages = [Storage("faah.xml")])
@Service(Service.Level.APP)
class ExceptionDetectorService : PersistentStateComponent<ExceptionDetectorService.State> {

    fun interface EnabledListener {
        fun onEnabledChanged(enabled: Boolean)
    }

    class State {
        var enabled: Boolean = false
        var volume: Int = 100
        var enabledErrorTypes: MutableList<String> = ErrorType.entries.map { it.name }.toMutableList()
    }

    @Volatile private var _enabled: Boolean = false
    @Volatile var volume: Int = 100
        set(value) { field = value.coerceIn(0, 100) }
    @Volatile private var enabledTypes: Set<ErrorType> = ErrorType.entries.toSet()
    @Volatile private var pattern: Regex = buildPattern(enabledTypes)

    var isEnabled: Boolean
        get() = _enabled
        set(value) {
            _enabled = value
            ApplicationManager.getApplication().messageBus
                .syncPublisher(ENABLED_TOPIC)
                .onEnabledChanged(value)
        }

    fun isTypeEnabled(type: ErrorType): Boolean = type in enabledTypes

    fun setTypeEnabled(type: ErrorType, enabled: Boolean) {
        enabledTypes = if (enabled) enabledTypes + type else enabledTypes - type
        pattern = buildPattern(enabledTypes)
    }

    fun isExceptionLine(line: String): Boolean =
        isEnabled && pattern.containsMatchIn(line)

    private fun buildPattern(types: Set<ErrorType>): Regex {
        if (types.isEmpty()) return Regex("(?!)")
        val combined = types.joinToString("|") { "(${it.pattern})" }
        return Regex(combined, setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
    }

    override fun getState() = State().also {
        it.enabled = _enabled
        it.volume = volume
        it.enabledErrorTypes = enabledTypes.map { t -> t.name }.toMutableList()
    }

    override fun loadState(state: State) {
        _enabled = state.enabled
        volume = state.volume
        enabledTypes = state.enabledErrorTypes
            .mapNotNull { name -> ErrorType.entries.find { it.name == name } }
            .toSet()
            .ifEmpty { ErrorType.entries.toSet() }
        pattern = buildPattern(enabledTypes)
    }

    companion object {
        val ENABLED_TOPIC: Topic<EnabledListener> =
            Topic.create("FaahEnabledChanged", EnabledListener::class.java)

        fun getInstance(): ExceptionDetectorService =
            ApplicationManager.getApplication().getService(ExceptionDetectorService::class.java)
    }
}
