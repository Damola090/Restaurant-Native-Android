package com.example.kadaracompose.two.workmanager.worker

import android.content.Context
import androidx.work.*
import com.example.kadaracompose.two.workmanager.domain.model.WorkDataKeys
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * Pattern: CONSTRAINTS
 *
 * Simulates a heavy sync that should only run when conditions are ideal —
 * connected to WiFi AND plugged in to charge.
 *
 * Available constraints:
 *  - setRequiredNetworkType(NetworkType.UNMETERED)  → WiFi only
 *  - setRequiredNetworkType(NetworkType.CONNECTED)  → any network
 *  - setRequiresCharging(true)                      → must be charging
 *  - setRequiresBatteryNotLow(true)                 → battery above ~20%
 *  - setRequiresDeviceIdle(true)                    → device not in active use (API 23+)
 *  - setRequiresStorageNotLow(true)                 → enough disk space
 *
 * WorkManager monitors these constraints in real time. If WiFi drops
 * while the worker is ENQUEUED (waiting), it stays queued until
 * WiFi returns. If it drops while RUNNING, behaviour depends on
 * the worker — CoroutineWorker gets cancelled and re-queued.
 */
class ConstrainedSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // By the time doWork() is called, all constraints are guaranteed to be met
        setProgress(workDataOf(WorkDataKeys.PROGRESS to 25))
        delay(500)
        setProgress(workDataOf(WorkDataKeys.PROGRESS to 75))
        delay(500)
        setProgress(workDataOf(WorkDataKeys.PROGRESS to 100))

        return Result.success(
            workDataOf(WorkDataKeys.RESULT_MESSAGE to "Heavy sync complete (ran on WiFi + charging)")
        )
    }

    companion object {
        fun buildRequest(): OneTimeWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
                .setRequiresCharging(true)                     // must be plugged in
                .setRequiresBatteryNotLow(true)                // battery above threshold
                .build()

            return OneTimeWorkRequestBuilder<ConstrainedSyncWorker>()
                .setConstraints(constraints)
                .addTag(WorkTags.CONSTRAINED_SYNC)
                // If constraints aren't met immediately, WorkManager will wait.
                // Set an initial delay to demonstrate the ENQUEUED state.
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build()
        }
    }
}
