package eu.darken.googleissue470114250

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class DebugAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val eventType = when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "WINDOW_CONTENT_CHANGED"
            else -> "OTHER(${event.eventType})"
        }

        Log.d(TAG, "========== EVENT: $eventType ==========")
        Log.d(TAG, "Package: ${event.packageName}")
        Log.d(TAG, "Class: ${event.className}")

        val root = rootInActiveWindow
        if (root != null) {
            Log.d(TAG, "---------- NODE HIERARCHY ----------")
            dumpNodeHierarchy(root, 0)
            root.recycle()
        } else {
            Log.d(TAG, "Root node is null")
        }
        Log.d(TAG, "====================================")
    }

    private fun dumpNodeHierarchy(node: AccessibilityNodeInfo, depth: Int) {
        val indent = "  ".repeat(depth)
        val bounds = android.graphics.Rect()
        node.getBoundsInScreen(bounds)

        val info = buildString {
            append("${indent}[${node.className}]")
            node.text?.let { append(" text=\"$it\"") }
            node.contentDescription?.let { append(" desc=\"$it\"") }
            node.viewIdResourceName?.let { append(" id=$it") }
            append(" bounds=$bounds")
            if (node.isClickable) append(" CLICKABLE")
            if (node.isFocusable) append(" FOCUSABLE")
            if (node.isVisibleToUser) append(" VISIBLE")
            if (!node.isImportantForAccessibility) append(" NOT_IMPORTANT")
        }
        Log.d(TAG, info)

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                dumpNodeHierarchy(child, depth + 1)
                child.recycle()
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service connected")
    }

    companion object {
        private const val TAG = "DebugA11y"
    }
}
