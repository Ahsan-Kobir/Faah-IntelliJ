package com.github.faah

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.content.ContentFactory
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingUtilities

class FaahToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = buildPanel(toolWindow)
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun buildPanel(toolWindow: ToolWindow): JPanel {
        val service = ExceptionDetectorService.getInstance()

        val root = JPanel()
        root.layout = BoxLayout(root, BoxLayout.Y_AXIS)
        root.border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

        // ── Master enable toggle ──────────────────────────────────────────────
        val masterToggle = JBCheckBox("Enable FAAH alert", service.isEnabled)
        masterToggle.font = masterToggle.font.deriveFont(masterToggle.font.size2D + 1f)
        masterToggle.addActionListener { service.isEnabled = masterToggle.isSelected }

        // Subscribe to service changes so the checkbox stays live even when the
        // toggle is flipped from the Tools menu action
        val connection = ApplicationManager.getApplication().messageBus.connect()
        Disposer.register(toolWindow.disposable, connection)
        connection.subscribe(
            ExceptionDetectorService.ENABLED_TOPIC,
            ExceptionDetectorService.EnabledListener { enabled ->
                SwingUtilities.invokeLater { masterToggle.isSelected = enabled }
            }
        )

        root.add(leftAlign(masterToggle))

        root.add(Box.createVerticalStrut(10))
        root.add(JSeparator())
        root.add(Box.createVerticalStrut(10))

        // ── Section label ─────────────────────────────────────────────────────
        root.add(leftAlign(JBLabel("Trigger alert on:")))
        root.add(Box.createVerticalStrut(8))

        // ── One checkbox per ErrorType ────────────────────────────────────────
        for (type in ErrorType.entries) {
            val check = JBCheckBox(type.displayName, service.isTypeEnabled(type))
            check.addActionListener { service.setTypeEnabled(type, check.isSelected) }
            root.add(leftAlign(check))
            root.add(Box.createVerticalStrut(4))
        }

        return root
    }

    /** Wraps a component in a left-aligned FlowLayout panel so BoxLayout doesn't stretch it. */
    private fun leftAlign(component: java.awt.Component): JPanel {
        val wrapper = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        wrapper.add(component)
        return wrapper
    }
}
