package com.example.kadaracompose.two.workmanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.two.workmanager.data.WorkManagerRepository
import com.example.kadaracompose.two.workmanager.domain.model.WorkInfo
import com.example.kadaracompose.two.workmanager.domain.model.WorkTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class WorkManagerScreenState(
    // One-time
    val compressWork: WorkInfo? = null,
    // Chain
    val chainWorks: List<WorkInfo> = emptyList(),
    // Periodic
    val periodicWork: WorkInfo? = null,
    val isPeriodicScheduled: Boolean = false,
    // Constrained
    val constrainedWork: WorkInfo? = null
)

@HiltViewModel
class WorkManagerViewModel @Inject constructor(
    private val repository: WorkManagerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkManagerScreenState())
    val state = _state.asStateFlow()

    init {
        observeAllWork()
    }

    private fun observeAllWork() {
        // Observe each tag independently and merge into screen state
        repository.observeWorkByTag(WorkTags.IMAGE_COMPRESS)
            .onEach { work -> _state.update { it.copy(compressWork = work) } }
            .launchIn(viewModelScope)

        repository.observeChain()
            .onEach { works -> _state.update { it.copy(chainWorks = works) } }
            .launchIn(viewModelScope)

        repository.observeWorkByTag(WorkTags.PERIODIC_SYNC)
            .onEach { work ->
                _state.update {
                    it.copy(
                        periodicWork = work,
                        isPeriodicScheduled = work != null
                    )
                }
            }
            .launchIn(viewModelScope)

        repository.observeWorkByTag(WorkTags.CONSTRAINED_SYNC)
            .onEach { work -> _state.update { it.copy(constrainedWork = work) } }
            .launchIn(viewModelScope)
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    fun runOneTimeWork() {
        repository.enqueueImageCompress("photo_${System.currentTimeMillis()}.jpg")
    }

    fun runChain() {
        repository.enqueueCompressUploadChain("photo_${System.currentTimeMillis()}.jpg")
    }

    fun togglePeriodicSync() {
        if (_state.value.isPeriodicScheduled) {
            repository.cancelPeriodicSync()
            _state.update { it.copy(isPeriodicScheduled = false, periodicWork = null) }
        } else {
            repository.enqueuePeriodicSync()
            _state.update { it.copy(isPeriodicScheduled = true) }
        }
    }

    fun runConstrainedWork() {
        repository.enqueueConstrainedSync()
    }

    fun cancelConstrainedWork() {
        repository.cancelByTag(WorkTags.CONSTRAINED_SYNC)
    }
}
