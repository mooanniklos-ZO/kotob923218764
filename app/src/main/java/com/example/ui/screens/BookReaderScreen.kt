package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BookUiState
import com.example.data.BookPage
import com.example.data.BookRepository
import com.example.ui.components.Book3DPageCard
import com.example.ui.theme.DarkGoldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HarvestGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    uiState: BookUiState,
    onPageSelected: (Int) -> Unit,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onToggle3D: () -> Unit,
    onChangeFontSize: (Boolean) -> Unit,
    onToggleTts: () -> Unit,
    onSetTtsSpeed: (Float) -> Unit,
    onSearchBook: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPage = BookRepository.pages[uiState.currentPageIndex]
    var showChapterSheet by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Controls Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chapters / TOC Button
                    IconButton(
                        onClick = { showChapterSheet = true },
                        modifier = Modifier.testTag("btn_table_of_contents")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "فهرس الأبواب",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Search Button
                    IconButton(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier.testTag("btn_search_book")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث في الكتاب",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Page Indicator in the center
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "صفحة ${currentPage.pageNumber} من ${BookRepository.pages.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Font Size Adjuster
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onChangeFontSize(false) },
                            modifier = Modifier.size(36.dp).testTag("btn_font_decrease")
                        ) {
                            Text("A-", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { onChangeFontSize(true) },
                            modifier = Modifier.size(36.dp).testTag("btn_font_increase")
                        ) {
                            Text("A+", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Audio Narrator (TTS) Toggle
                    IconButton(
                        onClick = onToggleTts,
                        modifier = Modifier.testTag("btn_toggle_tts")
                    ) {
                        Icon(
                            imageVector = if (uiState.isTtsPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                            contentDescription = "قراءة صوتية",
                            tint = if (uiState.isTtsPlaying) HarvestGold else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Audio Player Active Banner
            AnimatedVisibility(visible = uiState.isTtsPlaying) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔊", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "جاري القراءة الصوتية باللغة العربية...",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { showSpeedDialog = true }) {
                                Text(
                                    text = "${uiState.ttsSpeed}x",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = onToggleTts, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إيقاف",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Main Book 3D Content Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Book3DPageCard(
                    page = currentPage,
                    fontSizeScale = uiState.fontSizeScale,
                    isBookmarked = uiState.bookmarkedPages.contains(currentPage.pageNumber),
                    onBookmarkToggle = { onToggleBookmark(currentPage.pageNumber) },
                    onNext = onNextPage,
                    onPrev = onPrevPage,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Navigation & Slider Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Page Progress Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = uiState.currentPageIndex.toFloat(),
                            onValueChange = { onPageSelected(it.toInt()) },
                            valueRange = 0f..(BookRepository.pages.size - 1).toFloat(),
                            steps = BookRepository.pages.size - 2,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .testTag("slider_page_progress"),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${BookRepository.pages.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Next & Previous Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onPrevPage,
                            enabled = uiState.currentPageIndex > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_prev_page")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "الصفحة السابقة", style = MaterialTheme.typography.labelLarge)
                        }

                        Button(
                            onClick = onNextPage,
                            enabled = uiState.currentPageIndex < BookRepository.pages.size - 1,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_next_page")
                        ) {
                            Text(text = "الصفحة التالية", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                }
            }
        }

        // Table of Contents Modal Bottom Sheet
        if (showChapterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showChapterSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "📖 فهرس صفحات وأبواب الكتاب",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.height(400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(BookRepository.pages) { page ->
                            val isSelected = page.pageNumber - 1 == uiState.currentPageIndex
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onPageSelected(page.pageNumber - 1)
                                        showChapterSheet = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${page.pageNumber}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = page.mainTitle,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (page.subtitle.isNotEmpty()) {
                                                Text(
                                                    text = page.subtitle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    if (uiState.bookmarkedPages.contains(page.pageNumber)) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = HarvestGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Search in Book Dialog
        if (showSearchDialog) {
            var searchText by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                title = {
                    Text(
                        text = "🔍 بحث في نصوص ومواضيع الكتاب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                onSearchBook(it)
                            },
                            label = { Text("اكتب كلمة للبحث (مثال: تنقيط، طماطم، سماد)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_search_book"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (uiState.searchResults.isNotEmpty()) {
                            Text(
                                text = "نتائج البحث (${uiState.searchResults.size} صفحات):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyColumn(modifier = Modifier.height(200.dp)) {
                                items(uiState.searchResults) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                onPageSelected(item.pageNumber - 1)
                                                showSearchDialog = false
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "صفحة ${item.pageNumber}: ${item.mainTitle}",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            if (item.subtitle.isNotEmpty()) {
                                                Text(
                                                    text = item.subtitle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (searchText.isNotEmpty()) {
                            Text(
                                text = "لم يتم العثور على نتائج مطابقة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSearchDialog = false }) {
                        Text("إغلاق")
                    }
                }
            )
        }

        // TTS Speed Selector Dialog
        if (showSpeedDialog) {
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                title = { Text("سرعة القراءة الصوتية") },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                            Button(
                                onClick = {
                                    onSetTtsSpeed(speed)
                                    showSpeedDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.ttsSpeed == speed) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (uiState.ttsSpeed == speed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text("${speed}x")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedDialog = false }) {
                        Text("تم")
                    }
                }
            )
        }
    }
}
