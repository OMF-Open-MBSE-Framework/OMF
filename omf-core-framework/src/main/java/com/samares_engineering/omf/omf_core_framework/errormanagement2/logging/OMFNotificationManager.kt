package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging

import com.nomagic.magicdraw.ui.notification.Notification
import com.nomagic.magicdraw.ui.notification.NotificationManager
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog

class OMFNotificationManager(
    private val maxNotificationsPerMinute: Int,
) {
    companion object {
        @Volatile // Prevent multiple instances in a multi-threaded environment
        private var instance: OMFNotificationManager? = null

        @JvmStatic
        fun getInstance() =
            instance ?: throw CoreException2("OMFNotificationManager has not been initialized.")

        @JvmStatic
        fun init(maxNotificationsPerMinute: Int) {
            if (instance != null) {
                throw CoreException2("OMFNotificationManager has already been initialized.")
            }
            instance = synchronized(this) { // Prevent multiple instances in a multi-threaded environment
                OMFNotificationManager(maxNotificationsPerMinute)
            }
        }
    }

    private val timestampsOfLatestNotifications: MutableList<Long> = mutableListOf()

    fun showNotification(notification: Notification) {
        val currentTime = System.currentTimeMillis()

        // Purge timestamps older than 1 minute
        timestampsOfLatestNotifications.removeIf { it < currentTime - 60000 }

        if (timestampsOfLatestNotifications.size < maxNotificationsPerMinute) {
            // Log the timestamp of the notification
            timestampsOfLatestNotifications.add(currentTime)

            // Show notification in MagicDraw
            NotificationManager.getInstance().showNotification(notification)
        } else {
            notification.text?.let {
                OMFLogger2.toUI().warning(it)
                OMFLogger2.toSystem().warning(it)
            }
        }
    }
}