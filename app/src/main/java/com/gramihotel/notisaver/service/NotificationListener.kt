package com.gramihotel.notisaver.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

    // 서비스가 강제로 종료되었을 때 시스템이 자동으로 다시 시작
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        if (isNotificationListenerServiceEnabled(applicationContext)) {
            val componentName =
                ComponentName(
                    this,
                    NotificationListener::class.java,
                )
            requestRebind(componentName)
        }
    }

    companion object {
        fun isNotificationListenerServiceEnabled(context: Context): Boolean {
            return NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.packageName)
        }

        /**
         * 알림 접근 권한이 이미 활성화된 경우,
         * (즉, NotificationListenerService가 등록되어 있지만 바인딩 문제 등으로 인해 정상 동작하지 않을 때)
         * 해당 서비스 컴포넌트를 잠시 비활성화했다가 다시 활성화시켜 서비스가 강제로 재바인딩(재시작)되도록 합니다.
         */
        fun tryReEnableNotificationListener(context: Context) {
            if (isNotificationListenerServiceEnabled(context)) {
                // Rebind the service if it's already enabled
                val componentName =
                    ComponentName(
                        context,
                        NotificationListener::class.java,
                    )
                val pm = context.packageManager
                pm.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP,
                )
                pm.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP,
                )
            }
        }
    }
}