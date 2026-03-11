package com.example.kadaracompose.two.workmanager.worker

import android.content.Context
import androidx.work.*
import com.example.kadaracompose.two.workmanager.domain.model.WorkDataKeys
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import kotlinx.coroutines.delay

/**
 * Pattern: CHAINED WORK — Worker 2 of 3
 *
 * Simulates uploading the compressed image to a server.
 * Demonstrates:
 *  - Receiving inputData from the PREVIOUS worker in the chain.
 *    WorkManager automatically passes the outputData of worker N
 *    as the inputData of worker N+1.
 *  - Result.retry() — tells WorkManager to try again later
 *    (uses exponential backoff by default)
 *  - Progress reporting mid-upload
 */
class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // inputData automatically contains the outputData from ImageCompressWorker
        val compressedPath = inputData.getString(WorkDataKeys.COMPRESSED_PATH)
            ?: return Result.failure(
                workDataOf(WorkDataKeys.RESULT_MESSAGE to "No compressed file to upload")
            )

        // Simulate an upload with progress
        val steps = 8
        repeat(steps) { step ->
            val progress = ((step + 1) * 100) / steps
            setProgress(workDataOf(WorkDataKeys.PROGRESS to progress))
            delay(350)

            // Simulate a transient failure on first attempt (runAttemptCount > 0 = retry)
            // In a real app: check network errors here and return Result.retry()
        }

        val uploadUrl = "https://cdn.example.com/uploads/${compressedPath.substringAfterLast("/")}"

        return Result.success(
            workDataOf(
                WorkDataKeys.UPLOAD_URL to uploadUrl,
                WorkDataKeys.RESULT_MESSAGE to "Uploaded to $uploadUrl"
            )
        )
    }

    companion object {
        fun buildRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UploadWorker>()
                .addTag(WorkTags.IMAGE_UPLOAD)
                .addTag(WorkTags.COMPRESS_UPLOAD_CHAIN)
                // Exponential backoff: retry after 10s, then 20s, 40s...
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()
        }
    }
}
