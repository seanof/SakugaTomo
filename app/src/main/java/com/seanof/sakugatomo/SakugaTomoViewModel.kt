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
    private val defaultDispatcher: CoroutineDispatcher) : ViewModel() {
    private val _searchText = MutableStateFlow("")
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

    fun fetchSakugaTags() {
        viewModelScope.launch {
            sakugaApiService.getSakugaTags()
                .flowOn(defaultDispatcher)
                .catch { it.printStackTrace() }
                .collect {
                    it.data?.let { tags -> sakugaPostRepository.insertTags(tags) }
                }
        }
    }

    fun fetchSakugaPosts(fetchType: FetchType, tags: String = EMPTY) {
        viewModelScope.launch {
            when (fetchType) {
                FetchType.LATEST ->  sakugaApiService.getSakugaPosts(LATEST_FETCH_LIMIT)
                FetchType.POPULAR -> sakugaApiService.getPopularSakugaPosts()
                FetchType.SEARCH -> sakugaApiService.searchSakugaPosts(tags)
            }
                .flowOn(defaultDispatcher)
                .catch {
                    _uiState.value = ScreenUiState.Error(it.message ?: DEFAULT_ERROR_MSG)
                }
                .collect { result ->
                    when (result) {
                        is SakugaApiResult.Success ->
                            _uiState.value = ScreenUiState.Success(result.data ?: emptyList())

                        is SakugaApiResult.Error ->
                            _uiState.value = ScreenUiState.Error(result.error ?: DEFAULT_ERROR_MSG)

                        is SakugaApiResult.Loading ->
                            _uiState.value = ScreenUiState.Loading
                    }
                }
        }
    }

    fun setLikedPostsFromSavedPosts(data: List<SakugaPost>?, dbList: List<SakugaPost>) {
        viewModelScope.launch {
            val listData = data?.toList()
            for (dbItem in dbList) {
                if (listData != null) {
                    for (post in listData) {
                        if (dbItem.id == post.id) {
                            post.saved = true
                        }
                    }
                }
            }
        }
    }

    fun saveSakugaPost(sakugaPost: SakugaPost) {
        viewModelScope.launch { sakugaPostRepository.insert(sakugaPost) }
    }

    fun removeSakugaPost(sakugaPost: SakugaPost) {
        viewModelScope.launch { sakugaPostRepository.delete(sakugaPost) }
    }

    fun savePostToDownloads(context: Context, url: String, fileName: String) {
        val videoCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_VIDEO)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(videoCollection, contentValues)
        if (uri != null) {
            Toast.makeText(context, context.getString(R.string.download_text), Toast.LENGTH_SHORT).show()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    URL(url).openStream().use { input ->
                        resolver.openOutputStream(uri).use { output ->
                            input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                        }
                    }
                }
            }
        }
    }

    // Converts Flow into StateFlow for use in listing tags for SearchBar
    private val getAllSakugaTags: StateFlow<List<SakugaTag>> =
        sakugaPostRepository.sakugaTags
            .catch { exception -> exception.printStackTrace() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _sakugaTagsList = getAllSakugaTags
    val sakugaTagsList = searchText
        .combine(_sakugaTagsList) { text, sakugaTags ->
            sakugaTags.filter { sakugaTag ->
                sakugaTag.name.uppercase().contains(text.trim().uppercase())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _sakugaTagsList.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    enum class FetchType {
        LATEST, POPULAR, SEARCH
    }
}
