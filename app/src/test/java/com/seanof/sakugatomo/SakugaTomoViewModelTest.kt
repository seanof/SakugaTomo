import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.db.SakugaPostRepository
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import com.seanof.sakugatomo.data.remote.SakugaApiResult
import com.seanof.sakugatomo.data.remote.SakugaApiService
import com.seanof.sakugatomo.util.Const.DEFAULT_ERROR_MSG
import com.seanof.sakugatomo.util.Const.LATEST_FETCH_LIMIT
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class SakugaTomoViewModelTest {

    @Mock
    private lateinit var sakugaApiService: SakugaApiService

    @Mock
    private lateinit var sakugaPostRepository: SakugaPostRepository

    private lateinit var viewModel: SakugaTomoViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // Use test dispatcher for coroutines

        // Initialize the ViewModel with mocked dependencies
        viewModel = SakugaTomoViewModel(sakugaApiService, sakugaPostRepository, testDispatcher)
    }

    @Test
    fun `test fetchSakugaPosts updates state with data`() = runTest {
        // Arrange: Mock the service to return a successful result
        val mockPosts = listOf(SakugaPost(id = 1, source = "Test Post"))
        val mockResult = SakugaApiResult.Success(mockPosts)
        Mockito.`when`(sakugaApiService.getSakugaPosts(LATEST_FETCH_LIMIT)).thenReturn(flowOf(mockResult))

        // Act: Call fetchSakugaPosts with FetchType.LATEST
        viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.LATEST, "testTag")

        // Assert: Collect the state and verify it contains the expected posts
        val emittedState = viewModel.sakugaPosts.first()
        assert(emittedState is SakugaApiResult.Success)
        assert((emittedState as SakugaApiResult.Success).data == mockPosts)
    }

    @Test
    fun `test fetchSakugaPosts with SEARCH fetch type`() = runTest {
        // Arrange: Mock the service to return a successful result
        val mockPosts = listOf(SakugaPost(id = 1, source = "Test Post"))
        val mockResult = SakugaApiResult.Success(mockPosts)
        Mockito.`when`(sakugaApiService.searchSakugaPosts("testTag")).thenReturn(flowOf(mockResult))

        // Act: Call fetchSakugaPosts with FetchType.SEARCH
        viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.SEARCH, "testTag")

        // Assert: Collect the state and verify it contains the expected posts
        val emittedState = viewModel.sakugaPosts.first()
        assert(emittedState is SakugaApiResult.Success)
        assert((emittedState as SakugaApiResult.Success).data == mockPosts)
    }

    @Test
    fun `test fetchSakugaPosts with POPULAR fetch type`() = runTest {
        // Arrange: Mock the service to return a successful result
        val mockPosts = listOf(SakugaPost(id = 1, source = "Test Post"))
        val mockResult = SakugaApiResult.Success(mockPosts)
        Mockito.`when`(sakugaApiService.getPopularSakugaPosts()).thenReturn(flowOf(mockResult))

        // Act: Call fetchSakugaPosts with FetchType.POPULAR
        viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.POPULAR)

        // Assert: Collect the state and verify it contains the expected posts
        val emittedState = viewModel.sakugaPosts.first()
        assert(emittedState is SakugaApiResult.Success)
        assert((emittedState as SakugaApiResult.Success).data == mockPosts)
    }

    @Test
    fun `test fetchSakugaPosts handles error`() = runTest {
        // Arrange: Mock the service to return an error
        val errorMessage = DEFAULT_ERROR_MSG
        Mockito.`when`(sakugaApiService.getSakugaPosts(LATEST_FETCH_LIMIT))
            .thenReturn(flowOf(SakugaApiResult.Error(errorMessage)))

        // Act: Call fetchSakugaPosts with FetchType.LATEST
        viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.LATEST)

        // Assert: Collect the state and verify it contains the error message
        val emittedState = viewModel.sakugaPosts.first()
        assert(emittedState is SakugaApiResult.Error)
        assert((emittedState as SakugaApiResult.Error).error == errorMessage)
    }

    @Test
    fun `test saveSakugaPost interacts with repository`() = runTest {
        // Arrange: Create a mock SakugaPost
        val sakugaPost = SakugaPost(id = 1, source = "Test Post")

        // Act: Call saveSakugaPost on the ViewModel
        viewModel.saveSakugaPost(sakugaPost)

        // Assert: Verify that the repository's insert function was called
        verify(sakugaPostRepository).insert(sakugaPost)
    }

    @Test
    fun `test removeSakugaPost interacts with repository`() = runTest {
        // Arrange: Create a mock SakugaPost
        val sakugaPost = SakugaPost(id = 1, source = "Test Post")

        // Act: Call removeSakugaPost on the ViewModel
        viewModel.removeSakugaPost(sakugaPost)

        // Assert: Verify that the repository's delete function was called
        verify(sakugaPostRepository).delete(sakugaPost)
    }

    @Test
    fun `test setLikedPostsFromSavedPosts updates posts`() = runTest {
        // Arrange: Create mock data
        val savedPosts = listOf(SakugaPost(id = 1, source = "Test Post", saved = false))
        val dbPosts = listOf(SakugaPost(id = 1, source = "Test Post", saved = true))

        // Act: Call setLikedPostsFromSavedPosts
        viewModel.setLikedPostsFromSavedPosts(savedPosts, dbPosts)

        // Assert: Verify that the saved field is updated for posts
        assert(savedPosts[0].saved)
    }

    @Test
    fun `test onSearchTextChange updates searchText state`() = runTest {
        val searchQuery = "new search query"
        viewModel.onSearchTextChange(searchQuery)
        val latestSearchText = viewModel.searchText.first()

        assertEquals(searchQuery, latestSearchText)
    }

    @Test
    fun `test fetchSakugaTags calls repository to insert tags`() = runTest {
        val mockTags = listOf(SakugaTag(id = 1, name = "tag1"), SakugaTag(id = 2, name = "tag2"))
        Mockito.`when`(sakugaApiService.getSakugaTags()).thenReturn(flowOf(SakugaApiResult.Success(mockTags)))
        viewModel.fetchSakugaTags()

        verify(sakugaPostRepository).insertTags(mockTags)
    }

    @After
    fun tearDown() {
        // Cleanup after tests
        Dispatchers.resetMain()  // Reset the main dispatcher after each test
    }
}
