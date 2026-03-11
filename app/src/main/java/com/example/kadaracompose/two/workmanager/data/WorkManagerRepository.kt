package com.example.kadaracompose.two.workmanager.data

import android.content.Context
import androidx.work.*
import com.example.kadaracompose.two.workmanager.domain.model.WorkDataKeys
import com.example.kadaracompose.two.workmanager.domain.model.WorkInfo
import com.example.kadaracompose.two.workmanager.domain.model.WorkStatus
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import com.example.kadaracompose.two.workmanager.worker.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import androidx.work.WorkInfo as WMWorkInfo

@Singleton
class WorkManagerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    // ── Observe work state as Flow ────────────────────────────────────────────

    /**
     * getWorkInfosByTagFlow() returns a Flow<List<WorkInfo>>.
     * Every time a worker's state changes (ENQUEUED → RUNNING → SUCCEEDED),
     * the flow emits a new list. The UI just collects and reacts.
     */
    fun observeWorkByTag(tag: String): Flow<WorkInfo?> {
        return workManager.getWorkInfosByTagFlow(tag)
            .map { infos -> infos.firstOrNull()?.toDomain(tag) }
    }

    fun observeChain(): Flow<List<WorkInfo>> {
        return workManager.getWorkInfosByTagFlow(WorkTags.COMPRESS_UPLOAD_CHAIN)
            .map { infos -> infos.map { it.toDomain(it.tags.first()) } }
    }

    // ── Enqueue: One-time ─────────────────────────────────────────────────────

    fun enqueueImageCompress(fileName: String) {
        // enqueueUniqueWork with REPLACE cancels any previous completed/running
        // instance and starts fresh — this is what allows re-running the worker.
        // Plain enqueue() would just add another entry and the old SUCCEEDED
        // record stays, making the UI appear stuck.
        workManager.enqueueUniqueWork(
            WorkTags.IMAGE_COMPRESS,
            ExistingWorkPolicy.REPLACE,
            ImageCompressWorker.buildRequest(fileName)
        )
    }

    // ── Enqueue: Chain (compress → upload → notify) ───────────────────────────

    /**
     * beginUniqueWork() is the chained equivalent of enqueueUniqueWork().
     * REPLACE cancels the previous chain (all 3 workers) and starts a new one.
     * Without this, pressing Run again after SUCCEEDED does nothing visible.
     */
    fun enqueueCompressUploadChain(fileName: String) {
        workManager
            .beginUniqueWork(
                WorkTags.COMPRESS_UPLOAD_CHAIN,
                ExistingWorkPolicy.REPLACE,
                ImageCompressWorker.buildRequest(fileName)
            )
            .then(UploadWorker.buildRequest())
            .then(NotifyWorker.buildRequest())
            .enqueue()
    }

    // ── Enqueue: Periodic ─────────────────────────────────────────────────────

    /**
     * enqueueUniquePeriodicWork ensures only ONE periodic work with this name
     * exists at a time. Calling this multiple times won't create duplicates.
     *
     * ExistingPeriodicWorkPolicy:
     *  - KEEP    → ignore new request if one already exists (most common)
     *  - REPLACE → cancel existing, enqueue new one (resets the timer)
     *  - UPDATE  → keep existing work, update its parameters (API 31+)
     */
    fun enqueuePeriodicSync() {
        workManager.enqueueUniquePeriodicWork(
            SyncWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            SyncWorker.buildRequest()
        )
    }

    fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(SyncWorker.UNIQUE_WORK_NAME)
    }

    // ── Enqueue: Constrained ──────────────────────────────────────────────────

    fun enqueueConstrainedSync() {
        workManager.enqueueUniqueWork(
            WorkTags.CONSTRAINED_SYNC,
            ExistingWorkPolicy.REPLACE,
            ConstrainedSyncWorker.buildRequest()
        )
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    fun cancelByTag(tag: String) {
        workManager.cancelAllWorkByTag(tag)
    }

    // ── Mapping: WorkManager WorkInfo → domain WorkInfo ───────────────────────

    private fun WMWorkInfo.toDomain(tag: String): WorkInfo {
        val progress = progress.getInt(WorkDataKeys.PROGRESS, 0)
        val outputMessage = outputData.getString(WorkDataKeys.RESULT_MESSAGE)

        return WorkInfo(
            id = id,
            tag = tag,
            label = tagToLabel(tag),
            status = state.toDomain(),
            progress = if (state == WMWorkInfo.State.RUNNING) progress else
                if (state == WMWorkInfo.State.SUCCEEDED) 100 else progress,
            outputMessage = outputMessage
        )
    }

    private fun WMWorkInfo.State.toDomain(): WorkStatus = when (this) {
        WMWorkInfo.State.ENQUEUED  -> WorkStatus.ENQUEUED
        WMWorkInfo.State.RUNNING   -> WorkStatus.RUNNING
        WMWorkInfo.State.SUCCEEDED -> WorkStatus.SUCCEEDED
        WMWorkInfo.State.FAILED    -> WorkStatus.FAILED
        WMWorkInfo.State.CANCELLED -> WorkStatus.CANCELLED
        WMWorkInfo.State.BLOCKED   -> WorkStatus.BLOCKED
    }

    private fun tagToLabel(tag: String): String = when (tag) {
        WorkTags.IMAGE_COMPRESS   -> "1. Compress Image"
        WorkTags.IMAGE_UPLOAD     -> "2. Upload Image"
        WorkTags.IMAGE_NOTIFY     -> "3. Send Notification"
        WorkTags.PERIODIC_SYNC    -> "Periodic Sync"
        WorkTags.CONSTRAINED_SYNC -> "Constrained Sync"
        else -> tag
    }
}