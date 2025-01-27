package com.seanof.sakugatomo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.seanof.sakugatomo.ui.NavigationItem
import com.seanof.sakugatomo.ui.NavigationStack
import com.seanof.sakugatomo.ui.Screen
import com.seanof.sakugatomo.ui.theme.SakugaTomoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SakugaTomoTheme {
                val navController = rememberNavController()
                val viewModel: SakugaTomoViewModel by viewModels()
                val savedItems = viewModel.savedSakugaPosts.collectAsState(initial = listOf()).value
                val apiResult by viewModel.sakugaPosts.collectAsState()
                val searchText by viewModel.searchText.collectAsState()
                val sakugaTagsList by viewModel.sakugaTagsList.collectAsState()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentRoute by remember {
                    mutableStateOf(Screen.Latest.route)
                }
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
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
                                            Screen.Latest.route -> viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.LATEST)
                                            Screen.Popular.route -> viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.POPULAR)
                                            Screen.Search.route -> viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.SEARCH, searchText)
                                        }
                                        currentRoute = item.route
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
                                        if (item.title == Screen.Liked.route) Text(text = savedItems.size.toString())
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }

                        }
                    },
                    gesturesEnabled = true
                ) {
                    Scaffold(
                        topBar = {
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
                                                contentDescription = "Menu"
                                            )
                                        }
                                    },
                                    actions = {
                                        if (currentRoute == Screen.Latest.route) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.fetchSakugaPosts(SakugaTomoViewModel.FetchType.LATEST)
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Refresh"
                                                )
                                            }
                                        }
                                    }
                                )
                                if (currentRoute == Screen.Search.route) {
                                    var expanded by rememberSaveable { mutableStateOf(false) }
                                    SearchBar(
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
                                                    )},
                                                onSearch = {
                                                    viewModel.onSearchTextChange(it)
                                                    expanded = false
                                                },
                                                expanded = expanded,
                                                onExpandedChange = { expanded = it },
                                                placeholder = { Text("Search via tags") },
                                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                                trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.clickable { viewModel.onSearchTextChange("") }) }
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
                                            items(sakugaTagsList) {
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
                    ) { padding ->
                        NavigationStack(
                            navController,
                            padding = padding,
                            apiResult = apiResult,
                            savedPosts = savedItems,
                            viewModel::setLikedPostsFromSavedPosts,
                            viewModel::saveSakugaPost,
                            viewModel::removeSakugaPost)
                    }
                }
            }
        }
    }
}
