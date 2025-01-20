package com.joaomgcd.taskerpluginsample

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.joaomgcd.taskerpluginsample.tasker.basic.triggerBasicTaskerEvent

class AccessibilityServiceTrigger : AccessibilityService() {

    private var mAccessibilityButtonController: AccessibilityButtonController? = null
    private var accessibilityButtonCallback: AccessibilityButtonController.AccessibilityButtonCallback? = null
    private var mIsAccessibilityButtonAvailable: Boolean = false

    override fun onServiceConnected() {
        Log.d(TAG, "Service connected")

        mAccessibilityButtonController = accessibilityButtonController
        mIsAccessibilityButtonAvailable = mAccessibilityButtonController?.isAccessibilityButtonAvailable ?: false

        /*if (!mIsAccessibilityButtonAvailable) {
            Log.d(TAG, "Accessibility button not available")
            return
        }*/

        // Update service info to request the accessibility button
        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
        }

        accessibilityButtonCallback =
            object : AccessibilityButtonController.AccessibilityButtonCallback() {
                override fun onClicked(controller: AccessibilityButtonController) {
                    Log.d(TAG, "Accessibility button pressed!")

                    // Send an intent to Tasker
                    triggerTaskerAction()
                }

                override fun onAvailabilityChanged(controller: AccessibilityButtonController, available: Boolean) {
                    if (controller == mAccessibilityButtonController) {
                        mIsAccessibilityButtonAvailable = available
                        Log.d(TAG, "Accessibility button availability changed: $available")
                    }
                }
            }

        // Use a Handler for the callback
        accessibilityButtonCallback?.also {
            mAccessibilityButtonController?.registerAccessibilityButtonCallback(it, Handler(Looper.getMainLooper()))
        }
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the callback when the service is destroyed
        accessibilityButtonCallback?.let {
            mAccessibilityButtonController?.unregisterAccessibilityButtonCallback(it)
        }
        Log.d(TAG, "Service destroyed")
    }

    private fun triggerTaskerAction() {
        triggerBasicTaskerEvent()
    }

    companion object {
        private const val TAG = "AccessibilityServiceTrigger"
    }
}