package com.example.kadaracompose.two.workmanager.domain.model

import java.util.UUID

/**
 * Mirrors WorkManager's internal states but lives in domain
 * so the UI has no direct dependency on WorkManager classes.
 */
enum class WorkStatus {
    IDLE,       // not yet enqueued
    ENQUEUED,   // queued, waiting for constraints to be met
    RUNNING,    // actively executing
    SUCCEEDED,  // finished successfully
    FAILED,     // finished with failure
    CANCELLED,  // manually cancelled
    BLOCKED     // in a chain, waiting for predecessor to finish
}

/**
 * Represents a single tracked work item in the UI.
 */
data class WorkInfo(
    val id: UUID,
    val tag: String,
    val label: String,
    val status: WorkStatus = WorkStatus.IDLE,
    val progress: Int = 0,          // 0–100, only meaningful when RUNNING
    val outputMessage: String? = null
)

/**
 * Tags used to identify and observe each piece of work.
 * Using constants prevents typos when enqueuing vs observing.
 */
object WorkTags {
    const val IMAGE_COMPRESS  = "image_compress"
    const val IMAGE_UPLOAD    = "image_upload"
    const val IMAGE_NOTIFY    = "image_notify"
    const val PERIODIC_SYNC   = "periodic_sync"
    const val CONSTRAINED_SYNC = "constrained_sync"

    // Chain tag — applied to all 3 workers so we can observe the whole chain
    const val COMPRESS_UPLOAD_CHAIN = "compress_upload_chain"
}

/**
 * Keys for passing data between workers in a chain (WorkManager's Data API).
 */
object WorkDataKeys {
    const val PROGRESS         = "progress"
    const val INPUT_FILE_NAME  = "input_file_name"
    const val COMPRESSED_PATH  = "compressed_path"
    const val UPLOAD_URL       = "upload_url"
    const val RESULT_MESSAGE   = "result_message"
}
