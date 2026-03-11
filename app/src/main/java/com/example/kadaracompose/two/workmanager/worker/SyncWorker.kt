package com.example.kadaracompose.two.workmanager.worker

import android.content.Context
import androidx.work.*
import com.example.kadaracompose.two.workmanager.domain.model.WorkDataKeys
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * Pattern: PERIODIC WORK
 *
 * Simulates a background data sync that runs on a schedule.
 * Demonstrates:
 *  - PeriodicWorkRequest — repeats automatically after each interval
 *  - enqueueUniquePeriodicWork — ensures only ONE instance ever runs,
 *    even if the app calls enqueue multiple times
 *  - ExistingPeriodicWorkPolicy.KEEP vs REPLACE vs UPDATE
 *
 * Important constraint: WorkManager enforces a minimum interval of 15 minutes
 * for periodic work. You cannot schedule it more frequently than that.
 * In the experiment we use 15 minutes (the minimum) so you can verify
 * it's scheduled even if you can't wait for it to fire.
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(WorkDataKeys.PROGRESS to 0))
        delay(600)
        setProgress(workDataOf(WorkDataKeys.PROGRESS to 50))
        delay(600)
        setProgress(workDataOf(WorkDataKeys.PROGRESS to 100))

        val syncTime = java.text.SimpleDateFormat(
            "HH:mm:ss", java.util.Locale.getDefault()
        ).format(java.util.Date())

        return Result.success(
            workDataOf(WorkDataKeys.RESULT_MESSAGE to "Synced at $syncTime")
        )
    }

    companion object {
        /**
         * Use enqueueUniquePeriodicWork with this name to ensure only one
         * periodic sync ever runs at a time.
         */
        const val UNIQUE_WORK_NAME = "periodic_sync_work"

        fun buildRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .addTag(WorkTags.PERIODIC_SYNC)
                // Optional: add a flex window — work runs in the last N minutes
                // of each interval (improves battery by batching with other work)
                // .setFlexTimeInterval(5, TimeUnit.MINUTES)
                .build()
        }
    }
}
