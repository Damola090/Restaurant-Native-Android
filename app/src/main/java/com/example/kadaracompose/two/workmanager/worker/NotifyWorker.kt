package com.example.kadaracompose.two.workmanager.worker

import android.content.Context
import androidx.work.*
import com.example.kadaracompose.two.workmanager.domain.model.WorkDataKeys
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import kotlinx.coroutines.delay

/**
 * Pattern: CHAINED WORK — Worker 3 of 3 (terminal)
 *
 * Simulates sending a "your upload is ready" push notification
 * or webhook after the upload completes.
 *
 * Key lesson: if ANY worker in a chain returns Result.failure(),
 * all subsequent workers are BLOCKED and never run.
 * Only Result.success() passes execution to the next worker.
 */
class NotifyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Receives the upload URL from UploadWorker's outputData
        val uploadUrl = inputData.getString(WorkDataKeys.UPLOAD_URL)
            ?: return Result.failure(
                workDataOf(WorkDataKeys.RESULT_MESSAGE to "No upload URL to notify about")
            )

        delay(500) // simulate network call to notification service

        return Result.success(
            workDataOf(
                WorkDataKeys.RESULT_MESSAGE to "✅ Notified! File live at $uploadUrl"
            )
        )
    }

    companion object {
        fun buildRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NotifyWorker>()
                .addTag(WorkTags.IMAGE_NOTIFY)
                .addTag(WorkTags.COMPRESS_UPLOAD_CHAIN)
                .build()
        }
    }
}
