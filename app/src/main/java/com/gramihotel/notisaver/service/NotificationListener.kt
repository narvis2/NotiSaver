package com.gramihotel.notisaver.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.gramihotel.notisaver.data.model.noti.NotiPlatformType
import com.gramihotel.notisaver.work.NotiWorker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

    // 서비스가 강제로 종료되었을 때 시스템이 자동으로 다시 시작
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        Timber.e("📌 NotificationListener Start")
        super.onCreate()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn?.let {
            if (it.packageName in packageNames) {
                val notiKey = it.key
                val notiTime = it.postTime
                val extras = it.notification.extras ?: return
                val title = extras.getString(Notification.EXTRA_TITLE)
                val text = extras.getString(Notification.EXTRA_TEXT)

                if (title.isNullOrBlank() || text.isNullOrBlank()) return

                val type = when (it.packageName) {
                    "com.kakao.talk" -> {
                        if (title.contains("여기어때") && (text.contains("숙박") || text.contains("숙박취소"))) {
                            NotiPlatformType.YEOGI.name
                        } else {
                            return
                        }
                    }

                    "com.yanolja.partnercenter.app" -> {
                        if (text.contains("신규예약") || text.contains("예약취소")) {
                            if (title.contains("그라미호텔")) {
                                NotiPlatformType.YANOLJA_HOTEL.name
                            } else {
                                NotiPlatformType.YANOLJA_MOTEL.name
                            }
                        } else {
                            return
                        }
                    }

                    "com.naver.smartplace" -> {
                        if (title.contains("예약확정") || title.contains("예약취소")) {
                            NotiPlatformType.NAVER.name
                        } else {
                            return
                        }
                    }

                    else -> return
                }

                val data = workDataOf(
                    "key" to notiKey,
                    "notiPlatformType" to type,
                    "postTime" to notiTime,
                    "title" to title,
                    "text" to text,
                )

                Timber.e("📌 data 👉 $data")
                val work = OneTimeWorkRequestBuilder<NotiWorker>().setInputData(data).build()
                WorkManager.getInstance(applicationContext).enqueue(work)
            }
        }
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

        /**
         * com.goodchoice.abouthereowner
         * 여기어때의 Noti 가 불규칙하게 와서 여기어때 Noti 에 의존하지 않고
         * kakaoTalk Noti 에 의존하여 처리되도록 수정
         */
        private val packageNames = listOf(
            "com.kakao.talk",
            "com.yanolja.partnercenter.app",
            "com.naver.smartplace",
        )
    }
}