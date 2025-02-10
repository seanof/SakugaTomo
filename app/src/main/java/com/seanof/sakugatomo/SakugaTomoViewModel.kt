package com.seanof.sakugatomo

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SakugaTomoViewModel @Inject constructor(
    private val sakugaApiService: SakugaApiService,
    private val sakugaPostRepository: SakugaPostRepository,
    private val defaultDispatcher: CoroutineDispatcher) : ViewModel() {
    private val _posts = MutableStateFlow<SakugaApiResult<List<SakugaPost>>>(SakugaApiResult.Loading())
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    val sakugaPosts = _posts.asStateFlow()
    private val getAllSakugaTags: StateFlow<List<SakugaTag>> =
        sakugaPostRepository.sakugaTags
            .catch { exception -> exception.printStackTrace() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val savedSakugaPosts = sakugaPostRepository.sakugaPosts

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
                    _posts.value = SakugaApiResult.Error(it.message ?: DEFAULT_ERROR_MSG)
                }
                .collect {
                    _posts.value = it
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
