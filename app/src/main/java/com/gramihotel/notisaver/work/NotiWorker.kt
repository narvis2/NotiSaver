package com.gramihotel.notisaver.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gramihotel.notisaver.data.model.noti.NotiCreateRequest
import com.gramihotel.notisaver.data.repository.NotiRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class NotiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notiRepository: NotiRepository,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Timber.e("📌 WorkManager Called")
        val key = inputData.getString("key")
        val notiPlatformType = inputData.getString("notiPlatformType")
        val postTime = inputData.getLong(
            "postTime",
            defaultValue = 0
        )
        val title = inputData.getString("title")
        val text = inputData.getString("text")

        if (key.isNullOrBlank() || notiPlatformType.isNullOrBlank() || title.isNullOrBlank() || text.isNullOrBlank()) {
            return Result.failure()
        }

        Timber.e("📌 key -> $key, notiPlatformType -> $notiPlatformType, postTime -> $postTime, title -> $title, text -> $text")

        return try {
            val response = notiRepository.createNoti(NotiCreateRequest(
                hotelId = 1,
                key = key,
                postTime = postTime,
                title = title,
                text = text,
                notiPlatformType = notiPlatformType
            ))

            val data = response.body()

            Timber.e("📌 response -> $data")

            if (response.isSuccessful && data != null && data.success) {
                return Result.success()
            }

            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}