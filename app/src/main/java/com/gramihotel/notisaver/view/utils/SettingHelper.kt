package com.gramihotel.notisaver.view.utils

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.gramihotel.notisaver.service.NotificationListener
import android.content.Context

class SettingsHelper {
    companion object {
        private fun isNotificationPermissionGranted(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.isNotificationListenerAccessGranted(
                    ComponentName(
                        context,
                        NotificationListener::class.java
                    )
                )
            } else {
                NotificationManagerCompat.getEnabledListenerPackages(context)
                    .contains(context.packageName)
            }
        }

        /**
         * openNotificationListenerSettings 함수는 사용자를 알림 리스너 설정 화면으로 이동시킵니다.
         *
         * @param context: 현재 액티비티나 애플리케이션 컨텍스트를 전달받습니다.
         */
        fun openNotificationListenerSettings(context: Context) {
            if (isNotificationPermissionGranted(context)) return
            // Android 11 (API 30, Build.VERSION_CODES.R) 이상인 경우,
            // 앱의 상세 알림 리스너 설정 페이지로 바로 이동하도록 Intent를 생성합니다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 시스템 상수 Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS 를 사용해
                // 알림 리스너 상세 설정 화면으로 이동할 Intent를 생성합니다.
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)

                // Intent 에 EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME 를 추가하여,
                // 현재 앱의 NotificationListener 컴포넌트를 지정합니다.
                // 이를 위해 ComponentName 을 사용하며, 이를 문자열 형태로 변환(flattenToString)합니다.
                intent.putExtra(
                    Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                    ComponentName(
                        context,                   // 현재 Context
                        NotificationListener::class.java,  // 알림 리스너 컴포넌트 클래스
                    ).flattenToString(),             // 문자열 형태로 변환
                )

                try {
                    // 생성한 Intent 로 설정 화면을 실행합니다.
                    // 이 Intent 는 직접 해당 앱의 알림 리스너 상세 설정 화면으로 이동합니다.
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // 일부 기기나 Android 11 이전 버전에서는 ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS
                    // 액션을 지원하지 않는 경우가 있으므로, 예외 발생 시 일반 알림 리스너 설정 화면으로 이동합니다.
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
            } else {
                // Android 11 미만 버전에서는 일반 알림 리스너 설정 화면으로 이동합니다.
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
    }
}