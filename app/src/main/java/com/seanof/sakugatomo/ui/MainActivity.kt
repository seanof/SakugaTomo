package com.seanof.sakugatomo.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seanof.sakugatomo.R
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.ui.navigation.NavigationItem
import com.seanof.sakugatomo.ui.navigation.NavigationStack
import com.seanof.sakugatomo.ui.navigation.ScreenRoute
import com.seanof.sakugatomo.ui.theme.SakugaTomoTheme
import com.seanof.sakugatomo.util.Const.EMPTY
import com.seanof.sakugatomo.util.Const.PLAYER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            SakugaTomoTheme {
                val navController = rememberNavController()
                val viewModel: SakugaTomoViewModel by viewModels()
                val savedItems = viewModel.savedSakugaPosts.collectAsStateWithLifecycle(initialValue = listOf()).value
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val searchText by viewModel.searchText.collectAsStateWithLifecycle()
                val sakugaTags by viewModel.sakugaTags.collectAsStateWithLifecycle(initialValue = listOf())
                val searchedSakugaTags by viewModel.searchedSakugaTags.collectAsStateWithLifecycle()
                val gesturesEnabled by viewModel.gesturesEnabled.collectAsStateWithLifecycle()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var showTopBar by rememberSaveable { mutableStateOf(true) }
                val currentRoute = navBackStackEntry?.destination?.route
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                showTopBar = when (navBackStackEntry?.destination?.route) {
                    ScreenRoute.Latest.route, ScreenRoute.Search.route, ScreenRoute.Favourites.route, ScreenRoute.Popular.route -> true
                    else -> false
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = gesturesEnabled,
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(modifier = Modifier.height(16.dp))
                            NavigationItem.items.forEachIndexed { index, item ->
                                NavigationDrawerItem(
                                    label = { Text(text = item.title) },
                                    selected = index == selectedItemIndex,
                                    onClick = {
                                        navController.navigate(item.route)
                                        when (item.route) {
                                            ScreenRoute.Latest.route -> viewModel.fetchSakugaPosts(
                                                SakugaTomoViewModel.FetchType.LATEST
                                            )
                                            ScreenRoute.Popular.route -> viewModel.fetchSakugaPosts(
                                                SakugaTomoViewModel.FetchType.POPULAR
                                            )
                                            ScreenRoute.Search.route -> viewModel.fetchSakugaPosts(
                                                SakugaTomoViewModel.FetchType.SEARCH, searchText)
                                        }
                                        selectedItemIndex = index
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (index == selectedItemIndex) {
                                                item.selectedIcon
                                            } else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    badge = {
                                        if (item.route == ScreenRoute.Favourites.route) Text(text = savedItems.size.toString())
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }

                        }
                    },
                ) {
                    Scaffold(
                        topBar = {
                            if (showTopBar) {
                                Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                                    TopAppBar(
                                        title = {
                                            Text(text = stringResource(R.string.app_name))
                                        },
                                        navigationIcon = {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (isClosed) open() else close()
                                                    }
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    tint = colorResource(id = R.color.colorTint),
                                                    contentDescription = stringResource(R.string.menu)
                                                )
                                            }
                                        },
                                        actions = {
                                            if (currentRoute == ScreenRoute.Latest.route) {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.fetchSakugaPosts(
                                                            SakugaTomoViewModel.FetchType.LATEST
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        tint = colorResource(id = R.color.colorTint),
                                                        contentDescription = stringResource(R.string.refresh)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                    if (currentRoute == ScreenRoute.Search.route) {
                                        var expanded by rememberSaveable { mutableStateOf(false) }
                                        drawerState
                                        SearchBar(
                                            windowInsets = WindowInsets(top = 0.dp),
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .background(MaterialTheme.colorScheme.background)
                                                .padding(bottom = 20.dp)
                                                .semantics { traversalIndex = 0f },
                                            inputField = {
                                                SearchBarDefaults.InputField(
                                                    query = searchText,
                                                    onQueryChange = {
                                                        viewModel.onSearchTextChange(it)
                                                        viewModel.fetchSakugaPosts(
                                                            SakugaTomoViewModel.FetchType.SEARCH,
                                                            it
                                                        )
                                                    },
                                                    onSearch = {
                                                        viewModel.onSearchTextChange(it)
                                                        expanded = false
                                                    },
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = it },
                                                    placeholder = { Text(stringResource(R.string.search_via_tags)) },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.Search,
                                                            contentDescription = null
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        Icon(
                                                            Icons.Default.Clear,
                                                            contentDescription = null,
                                                            modifier = Modifier.clickable {
                                                                viewModel.onSearchTextChange(EMPTY)
                                                            })
                                                    }
                                                )
                                            },
                                            expanded = expanded,
                                            onExpandedChange = { expanded = it },
                                        ) {
                                            LazyColumn(
                                                contentPadding = PaddingValues(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier
                                                    .semantics { traversalIndex = 1f },
                                            ) {
                                                items(searchedSakugaTags) {
                                                    Text(
                                                        text = it.name,
                                                        modifier = Modifier
                                                            .clickable {
                                                                viewModel.onSearchTextChange(it.name)
                                                                expanded = false
                                                                viewModel.fetchSakugaPosts(
                                                                    SakugaTomoViewModel.FetchType.SEARCH,
                                                                    it.name
                                                                )
                                                            }
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 16.dp),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { padding ->
                        NavigationStack(
                            navController,
                            padding = padding,
                            uiState = uiState,
                            savedPosts = savedItems,
                            sakugaTagsList = sakugaTags,
                            gesturesEnabled = viewModel::setGesturesEnabled,
                            likedPosts = viewModel::setLikedPostsFromSavedPosts,
                            onSaveItemToDownloads = viewModel::savePostToDownloads,
                            onItemLiked = viewModel::saveSakugaPost,
                            onItemDelete = viewModel::removeSakugaPost
                        )
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onBackInvokedDispatcher.registerOnBackInvokedCallback(100) {
                        if (navController.currentBackStackEntry?.destination?.route?.contains(PLAYER) == true) onBackPressedDispatcher.onBackPressed()
                        else if (drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        } else finish()
                    }
                }
            }
        }
    }
}
