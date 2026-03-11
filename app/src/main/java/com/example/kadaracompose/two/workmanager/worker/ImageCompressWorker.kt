package com.example.kadaracompose.two.workmanager.worker

import android.content.Context
import androidx.work.*
import com.example.kadaracompose.two.workmanager.domain.model.WorkDataKeys
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import kotlinx.coroutines.delay

/**
 * Pattern: ONE-TIME WORK + PROGRESS REPORTING
 *
 * Simulates compressing an image file in the background.
 * Demonstrates:
 *  - setProgress() to push live updates to the UI while running
 *  - outputData to pass results to the next worker in a chain
 *  - CoroutineWorker (preferred over Worker) for suspend support
 *
 * setProgress() vs outputData:
 *  - setProgress() → intermediate updates while the worker is RUNNING
 *  - outputData (Result.success(data)) → final output, passed to next worker in chain
 */
class ImageCompressWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val fileName = inputData.getString(WorkDataKeys.INPUT_FILE_NAME)
            ?: "image_${System.currentTimeMillis()}.jpg"

        // Simulate compression in steps, reporting progress at each step
        val steps = 10
        repeat(steps) { step ->
            val progress = ((step + 1) * 100) / steps

            // Push progress to UI — observe via WorkInfo.progress in ViewModel
            setProgress(
                workDataOf(WorkDataKeys.PROGRESS to progress)
            )

            delay(400) // simulate real compression work
        }

        // Pass result to the next worker in the chain via outputData
        val compressedPath = "compressed/$fileName"
        return Result.success(
            workDataOf(
                WorkDataKeys.COMPRESSED_PATH to compressedPath,
                WorkDataKeys.RESULT_MESSAGE to "Compressed: $fileName"
            )
        )
    }

    companion object {
        /**
         * Factory to build the WorkRequest — keeps enqueue logic off the caller.
         * inputData lets you pass parameters into the worker.
         */
        fun buildRequest(fileName: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ImageCompressWorker>()
                .setInputData(workDataOf(WorkDataKeys.INPUT_FILE_NAME to fileName))
                .addTag(WorkTags.IMAGE_COMPRESS)
                .addTag(WorkTags.COMPRESS_UPLOAD_CHAIN)
                .build()
        }
    }
}
