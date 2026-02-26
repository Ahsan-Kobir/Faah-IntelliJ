package com.github.faah

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

@Service(Service.Level.APP)
class ExceptionDetectorService {

    @Volatile
    var isEnabled: Boolean = false

    // Matches common exception/error indicators in console output
    private val exceptionPattern: Regex = Regex(
        """(Exception|Error|FATAL|Caused by:|^\s+at\s+[\w.${'$'}]+\()""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    fun isExceptionLine(line: String): Boolean {
        return isEnabled && exceptionPattern.containsMatchIn(line)
    }

    companion object {
        fun getInstance(): ExceptionDetectorService =
            ApplicationManager.getApplication().getService(ExceptionDetectorService::class.java)
    }
}
