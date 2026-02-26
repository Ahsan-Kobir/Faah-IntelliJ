package com.github.faah

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

/**
 * Registered as a consoleFilterProvider extension point.
 * IntelliJ routes every line printed to any ConsoleView through these filters —
 * this covers run configurations, test output, Gradle, and Android Logcat.
 */
class ExceptionConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(project: Project): Array<Filter> =
        arrayOf(ExceptionDetectionFilter())
}

private class ExceptionDetectionFilter : Filter {
    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        if (ExceptionDetectorService.getInstance().isExceptionLine(line)) {
            SoundPlayer.playAlert()
        }
        // Return null to leave the line unmodified — we only want the side-effect
        return null
    }
}
