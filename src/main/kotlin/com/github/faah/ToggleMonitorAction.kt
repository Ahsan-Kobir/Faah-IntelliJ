package com.github.faah

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class ToggleMonitorAction : ToggleAction(
    "Enable FAAH Alert",
    "Play a sound alert when exceptions are detected in any console output",
    null
) {

    override fun isSelected(e: AnActionEvent): Boolean =
        ExceptionDetectorService.getInstance().isEnabled

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        ExceptionDetectorService.getInstance().isEnabled = state
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
