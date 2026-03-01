package com.github.faah

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.util.Key

/**
 * Registered as a project-level ExecutionListener.
 * Attaches a ProcessListener to every process started by a run/debug configuration,
 * providing a secondary capture path alongside ConsoleFilterProvider.
 *
 * This catches output that may not pass through ConsoleView filters
 * (e.g., raw process stderr in some configurations).
 */
class TerminalOutputListener : ExecutionListener {

    override fun processStarted(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler
    ) {
        handler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                if (ExceptionDetectorService.getInstance().isExceptionLine(event.text)) {
                    SoundPlayer.getInstance().playAlert()
                }
            }
        })
    }
}
