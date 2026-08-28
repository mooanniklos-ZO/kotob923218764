package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.screens.AboutAuthorScreen
import com.example.ui.screens.BookReaderScreen
import com.example.ui.screens.CropsEncyclopediaScreen
import com.example.ui.screens.GardenPlannerScreen
import com.example.ui.screens.NotesAndBookmarksScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: BookViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == NavigationTab.BOOK_READER,
                    onClick = { viewModel.selectTab(NavigationTab.BOOK_READER) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == NavigationTab.BOOK_READER) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                            contentDescription = "الكتاب 3D"
                        )
                    },
                    label = { Text("الكتاب 3D") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_book_reader")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == NavigationTab.CROPS_ENCYCLOPEDIA,
                    onClick = { viewModel.selectTab(NavigationTab.CROPS_ENCYCLOPEDIA) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == NavigationTab.CROPS_ENCYCLOPEDIA) Icons.Filled.Yard else Icons.Outlined.Yard,
                            contentDescription = "المحاصيل"
                        )
                    },
                    label = { Text("المحاصيل") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_crops")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == NavigationTab.GARDEN_PLANNER,
                    onClick = { viewModel.selectTab(NavigationTab.GARDEN_PLANNER) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == NavigationTab.GARDEN_PLANNER) Icons.Filled.Agriculture else Icons.Outlined.Agriculture,
                            contentDescription = "المخطط"
                        )
                    },
                    label = { Text("المخطط") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_planner")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == NavigationTab.NOTES_BOOKMARKS,
                    onClick = { viewModel.selectTab(NavigationTab.NOTES_BOOKMARKS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == NavigationTab.NOTES_BOOKMARKS) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "الملاحظات"
                        )
                    },
                    label = { Text("الملاحظات") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_notes")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == NavigationTab.ABOUT_AUTHOR,
                    onClick = { viewModel.selectTab(NavigationTab.ABOUT_AUTHOR) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == NavigationTab.ABOUT_AUTHOR) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "المؤلف"
                        )
                    },
                    label = { Text("المؤلف") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_author")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                NavigationTab.BOOK_READER -> {
                    BookReaderScreen(
                        uiState = uiState,
                        onPageSelected = { viewModel.goToPage(it) },
                        onNextPage = { viewModel.nextPage() },
                        onPrevPage = { viewModel.prevPage() },
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onToggle3D = { viewModel.toggle3DMode() },
                        onChangeFontSize = { viewModel.changeFontSize(it) },
                        onToggleTts = { viewModel.toggleTts() },
                        onSetTtsSpeed = { viewModel.setTtsSpeed(it) },
                        onSearchBook = { viewModel.searchBook(it) }
                    )
                }

                NavigationTab.CROPS_ENCYCLOPEDIA -> {
                    CropsEncyclopediaScreen(
                        uiState = uiState,
                        onCategorySelected = { viewModel.setCropCategory(it) },
                        onSearchChanged = { viewModel.setCropSearchQuery(it) },
                        onCropSelected = { viewModel.selectCrop(it) }
                    )
                }

                NavigationTab.GARDEN_PLANNER -> {
                    GardenPlannerScreen(
                        uiState = uiState,
                        onToggleTask = { viewModel.toggleTask(it) }
                    )
                }

                NavigationTab.NOTES_BOOKMARKS -> {
                    NotesAndBookmarksScreen(
                        uiState = uiState,
                        onNavigateToPage = {
                            viewModel.goToPage(it)
                            viewModel.selectTab(NavigationTab.BOOK_READER)
                        },
                        onAddNote = { title, content, page ->
                            viewModel.addNote(title, content, page)
                        },
                        onDeleteNote = { viewModel.deleteNote(it) }
                    )
                }

                NavigationTab.ABOUT_AUTHOR -> {
                    AboutAuthorScreen()
                }
            }
        }
    }
}
