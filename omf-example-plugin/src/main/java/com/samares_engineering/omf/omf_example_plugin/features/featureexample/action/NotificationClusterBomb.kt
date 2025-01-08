/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.featureexample.action

import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.hyperlinks.FileHyperlink
import com.nomagic.magicdraw.ui.notification.HRefRunnable
import com.nomagic.magicdraw.ui.notification.Notification
import com.nomagic.magicdraw.ui.notification.NotificationSeverity
import com.nomagic.magicdraw.ui.notification.config.NotificationViewConfig
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFNotificationManager
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import java.awt.Color
import java.awt.Desktop
import java.net.URI
import javax.swing.ImageIcon
import kotlin.random.Random


@MenuAction
@DeactivateListener
@MDAction(actionName = "Notification cluster bomb", category = "OMF")
class NotificationClusterBomb : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return true
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFNotificationManager.getInstance().clearNotificationCooldown()

        val imageIcon = ImageIcon(javaClass.getResource("/img/rickroll-icon.gif"))
        // New thread to avoid blocking the UI
        Thread {
            for (i in 0..100) {
                // Open a youtube video on system default browser
                val webLink = HRefRunnable.create("I'm a youtube video trust me it's safe to click me\n", false) {
                    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
                    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
                    }
                }
                // Open a local gif
                val fileLink = HRefRunnable.create("I'm a local gif trust me it's safe to click me\n", false) {
                    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
                    if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
                        desktop.open(java.io.File("../../src/main/resources/img/rickroll.gif"))
                    }
                }
                val elementLink = HRefRunnable.createHRefRunnableForElement(
                    Application.getInstance().project
                        ?.getElementByID("_17_0_4beta_1b3a0482_1355297523562_243393_22925") as Element
                )
                val actions: Array<HRefRunnable> = arrayOf(
                    webLink,
                    fileLink,
                    elementLink
                )
                val rand = Random.Default
                val notifConfig = NotificationViewConfig().apply {
                    this.backgroundColor = Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))
                    this.expirationTime = 20
                    this.icon = imageIcon
                    this.setShowMultipleNotifications(true) // Show multiple notifications with same id
                }

               //OMFLogger2.toNotification().log(OMFLog("BOOM ! Testing notification throttling x$i"))
                val notification = Notification(
                    i.toString(), "[$i] Never gonna give you up", "Never gonna let you down",
                    "Never gonna run around and desert you\n" +
                            "Never gonna make you cry\n" +
                            "Never gonna say goodbye\n" +
                            "Never gonna tell a lie and hurt you", actions,
                    NotificationSeverity.WARNING, Notification.Context.PROJECT
                )
                OMFNotificationManager.getInstance().showNotification(notification, notifConfig)
                // Sleep for 0.1 seconds
               //Thread.sleep(100)
            }
        }.start()
    }
}