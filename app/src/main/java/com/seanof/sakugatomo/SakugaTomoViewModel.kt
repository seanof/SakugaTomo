package com.seanof.sakugatomo

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanof.sakugatomo.data.db.SakugaPostRepository
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import com.seanof.sakugatomo.data.remote.SakugaApiResult
import com.seanof.sakugatomo.data.remote.SakugaApiService
import com.seanof.sakugatomo.util.Const.DEFAULT_ERROR_MSG
import com.seanof.sakugatomo.util.Const.EMPTY
import com.seanof.sakugatomo.util.Const.LATEST_FETCH_LIMIT
import com.seanof.sakugatomo.util.Const.MIME_TYPE_VIDEO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SakugaTomoViewModel @Inject constructor(
    private val sakugaApiService: SakugaApiService,
    private val sakugaPostRepository: SakugaPostRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _searchText = MutableStateFlow(EMPTY)
    val searchText = _searchText.asStateFlow()

    val savedSakugaPosts = sakugaPostRepository.sakugaPosts

    private val _uiState = MutableStateFlow<ScreenUiState>(ScreenUiState.Loading)
    val uiState: StateFlow<ScreenUiState> = _uiState.asStateFlow()

    sealed class ScreenUiState {
        data object Loading : ScreenUiState()
        data class Error(val errorMessage: String) : ScreenUiState()
        data class Success(val posts: List<SakugaPost>) : ScreenUiState()
    }

    init {
        fetchSakugaTags()
        fetchSakugaPosts(FetchType.LATEST)
    }

    fun fetchSakugaTags() = viewModelScope.launch {
        sakugaApiService.getSakugaTags()
            .flowOn(defaultDispatcher)
            .catch { it.printStackTrace() }
            .collect { it.data?.let { tags -> sakugaPostRepository.insertTags(tags) } }
    }

    fun fetchSakugaPosts(fetchType: FetchType, tags: String = EMPTY) = viewModelScope.launch {
        val flow = when (fetchType) {
            FetchType.LATEST -> sakugaApiService.getSakugaPosts(LATEST_FETCH_LIMIT)
            FetchType.POPULAR -> sakugaApiService.getPopularSakugaPosts()
            FetchType.SEARCH -> sakugaApiService.searchSakugaPosts(tags)
        }

        flow.flowOn(defaultDispatcher)
            .catch {
                _uiState.value = ScreenUiState.Error(it.message ?: DEFAULT_ERROR_MSG)
            }
            .collect { result ->
                _uiState.value = when (result) {
                    is SakugaApiResult.Success -> ScreenUiState.Success(result.data ?: emptyList())
                    is SakugaApiResult.Error -> ScreenUiState.Error(result.error ?: DEFAULT_ERROR_MSG)
                    is SakugaApiResult.Loading -> ScreenUiState.Loading
                }
            }
    }

    fun setLikedPostsFromSavedPosts(data: List<SakugaPost>?, dbList: List<SakugaPost>) {
        val savedIds = dbList.map { it.id }.toSet()
        data?.forEach { it.saved = it.id in savedIds }
    }

    fun saveSakugaPost(post: SakugaPost) = viewModelScope.launch {
        sakugaPostRepository.insert(post)
    }

    fun removeSakugaPost(post: SakugaPost) = viewModelScope.launch {
        sakugaPostRepository.delete(post)
    }

    fun savePostToDownloads(context: Context, url: String, fileName: String) {
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_VIDEO)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        context.contentResolver.insert(videoCollection, contentValues)?.let { uri ->
            Toast.makeText(context, context.getString(R.string.download_text), Toast.LENGTH_SHORT).show()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    URL(url).openStream().use { input ->
                        context.contentResolver.openOutputStream(uri)?.use { output ->
                            input.copyTo(output, DEFAULT_BUFFER_SIZE)
                        }
                    }
                }
            }
        }
    }

    private val sakugaTags: StateFlow<List<SakugaTag>> =
        sakugaPostRepository.sakugaTags
            .catch { it.printStackTrace() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val sakugaTagsList: StateFlow<List<SakugaTag>> = searchText
        .combine(sakugaTags) { text, tags ->
            val query = text.trim().uppercase()
            tags.filter { it.name.uppercase().contains(query) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TAG_LIST_TIMEOUT), emptyList())

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    enum class FetchType { LATEST, POPULAR, SEARCH }

    private companion object {
        const val TAG_LIST_TIMEOUT = 5000L
    }
}
