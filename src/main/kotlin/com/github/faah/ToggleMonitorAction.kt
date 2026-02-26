package com.github.faah

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class ToggleMonitorAction : ToggleAction(
    "Monitor Exceptions",
    "Play a sound alert when exceptions are detected in any console output",
    AllIcons.General.Error
) {

    override fun isSelected(e: AnActionEvent): Boolean =
        ExceptionDetectorService.getInstance().isEnabled

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        ExceptionDetectorService.getInstance().isEnabled = state
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
