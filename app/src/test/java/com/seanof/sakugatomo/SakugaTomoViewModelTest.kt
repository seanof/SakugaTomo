import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.db.SakugaPostRepository
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.remote.SakugaApiResult
import com.seanof.sakugatomo.data.remote.SakugaApiService
import com.seanof.sakugatomo.util.Const.DEFAULT_ERROR_MSG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

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
        Mockito.`when`(sakugaApiService.getSakugaPosts(50)).thenReturn(flowOf(mockResult))

        // Act: Call fetchSakugaPosts with FetchType.LATEST
        viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.LATEST)

        // Assert: Collect the state and verify it contains the expected posts
        val emittedState = viewModel.sakugaPosts.first()
        assert(emittedState is SakugaApiResult.Success)
        assert((emittedState as SakugaApiResult.Success).data == mockPosts)
    }

    @Test
    fun `test fetchSakugaPosts handles error`() = runTest {
        // Arrange: Mock the service to return an error
        val errorMessage = DEFAULT_ERROR_MSG
        Mockito.`when`(sakugaApiService.getSakugaPosts(50))
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
        Mockito.verify(sakugaPostRepository).insert(sakugaPost)
    }

    @Test
    fun `test removeSakugaPost interacts with repository`() = runTest {
        // Arrange: Create a mock SakugaPost
        val sakugaPost = SakugaPost(id = 1, source = "Test Post")

        // Act: Call removeSakugaPost on the ViewModel
        viewModel.removeSakugaPost(sakugaPost)

        // Assert: Verify that the repository's delete function was called
        Mockito.verify(sakugaPostRepository).delete(sakugaPost)
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

    @After
    fun tearDown() {
        // Cleanup after tests
        Dispatchers.resetMain()  // Reset the main dispatcher after each test
    }
}