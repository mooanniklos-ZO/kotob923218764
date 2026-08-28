package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookPage
import com.example.data.PageType
import com.example.ui.theme.DarkGoldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HarvestGold
import kotlinx.coroutines.launch

@Composable
fun Book3DPageCard(
    page: BookPage,
    fontSizeScale: Float,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .pointerInput(page.pageNumber) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (totalDrag > 80f) {
                            // Drag Right -> In Arabic (RTL) Next Page
                            scope.launch {
                                rotation.animateTo(
                                    targetValue = 25f,
                                    animationSpec = tween(150, easing = FastOutSlowInEasing)
                                )
                                rotation.animateTo(0f, tween(150))
                                onNext()
                            }
                        } else if (totalDrag < -80f) {
                            // Drag Left -> Prev Page
                            scope.launch {
                                rotation.animateTo(
                                    targetValue = -25f,
                                    animationSpec = tween(150, easing = FastOutSlowInEasing)
                                )
                                rotation.animateTo(0f, tween(150))
                                onPrev()
                            }
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }
    ) {
        // 3D Realistic Book Open Page Background with Spine Shadow
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 14f * density
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 16.dp, bottomEnd = 16.dp),
                    spotColor = Color(0x660B3819),
                    ambientColor = Color(0x33000000)
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 16.dp, bottomEnd = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Spine book shadow on the left edge (realistic 3D book spine)
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0x33000000),
                                    Color(0x18000000),
                                    Color(0x05000000),
                                    Color.Transparent
                                ),
                                startX = 0f,
                                endX = 35f
                            )
                        )
                        // Outer page edge gradient on right
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x08000000),
                                    Color(0x15000000)
                                ),
                                startX = size.width - 25f,
                                endX = size.width
                            )
                        )
                    }
            ) {
                // Main Content inside Scrollable Container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 20.dp, top = 18.dp, bottom = 18.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Page Number & Bookmark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${page.pageNumber}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Text(
                            text = page.headerTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        IconButton(
                            onClick = onBookmarkToggle,
                            modifier = Modifier.testTag("btn_bookmark_page_${page.pageNumber}")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ الصفحة",
                                tint = if (isBookmarked) HarvestGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Decorative Ornamental Divider
                    BookOrnamentalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Title section
                    Text(
                        text = page.mainTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = (MaterialTheme.typography.titleLarge.fontSize.value * fontSizeScale).sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (page.subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = page.subtitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = (MaterialTheme.typography.titleMedium.fontSize.value * fontSizeScale).sp
                            ),
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Special Page Types
                    when (page.pageType) {
                        PageType.COVER -> {
                            CoverPageLayout(page = page, fontSizeScale = fontSizeScale)
                        }
                        PageType.DEDICATION -> {
                            DedicationPageLayout(page = page, fontSizeScale = fontSizeScale)
                        }
                        PageType.INDEX -> {
                            IndexPageLayout(page = page, fontSizeScale = fontSizeScale)
                        }
                        PageType.WARNING -> {
                            WarningPageLayout(page = page, fontSizeScale = fontSizeScale)
                        }
                        PageType.CONCLUSION -> {
                            ConclusionPageLayout(page = page, fontSizeScale = fontSizeScale)
                        }
                        PageType.CONTENT -> {
                            StandardContentPageLayout(page = page, fontSizeScale = fontSizeScale)
                        }
                    }

                    // Author Advice Box if exists
                    if (page.authorNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AuthorTipBox(note = page.authorNotes, fontSizeScale = fontSizeScale)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bottom Page Indicator
                    Text(
                        text = "كتاب إستكفي مما تزرع • صفحة ${page.pageNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun StandardContentPageLayout(
    page: BookPage,
    fontSizeScale: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Paragraphs
        page.paragraphs.forEach { paragraph ->
            Text(
                text = paragraph,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontSizeScale).sp,
                    lineHeight = (30 * fontSizeScale).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Bullet Points with custom 3D botanical badges
        page.bulletPoints.forEachIndexed { index, point ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 10.dp)
                        .size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🌱",
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = point,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontSizeScale).sp,
                        lineHeight = (28 * fontSizeScale).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quote or Special note
        if (page.quoteOrNote.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = page.quoteOrNote,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (MaterialTheme.typography.bodyMedium.fontSize.value * fontSizeScale).sp,
                            lineHeight = (24 * fontSizeScale).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CoverPageLayout(page: BookPage, fontSizeScale: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Emblem Box
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldPrimary,
                            Color(0xFF0F3A15)
                        )
                    )
                )
                .border(3.dp, DarkGoldAccent, CircleShape)
                .shadow(12.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🌱", fontSize = 42.sp)
                Text(
                    text = "اكتفاء ذاتي",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Author Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "إعداد وتأليف",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "الدكتور مالك عبدالرحمن الرميمة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📞 هاتف التواصل: 771134103",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Book Premise
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌟 فكرة الكتاب وأهدافه:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = page.paragraphs.firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (15 * fontSizeScale).sp,
                        lineHeight = (26 * fontSizeScale).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (page.quoteOrNote.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = page.quoteOrNote,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DedicationPageLayout(page: BookPage, fontSizeScale: Float) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(HarvestGold, Color.Transparent)
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🤍", fontSize = 34.sp)
            Spacer(modifier = Modifier.height(10.dp))
            page.paragraphs.forEach { para ->
                Text(
                    text = para,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = (16 * fontSizeScale).sp,
                        lineHeight = (30 * fontSizeScale).sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun IndexPageLayout(page: BookPage, fontSizeScale: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        page.bulletPoints.forEachIndexed { index, item ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (15 * fontSizeScale).sp
                        ),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WarningPageLayout(page: BookPage, fontSizeScale: Float) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFE65100), Color(0xFFFF9800))
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "تحذير",
                tint = Color(0xFFE65100),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = page.warningText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = (17 * fontSizeScale).sp,
                    lineHeight = (30 * fontSizeScale).sp
                ),
                color = Color(0xFFBF360C),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            page.paragraphs.forEach { p ->
                Text(
                    text = p,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (15 * fontSizeScale).sp,
                        lineHeight = (26 * fontSizeScale).sp
                    ),
                    color = Color(0xFF5D4037),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ConclusionPageLayout(page: BookPage, fontSizeScale: Float) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(EmeraldPrimary, DarkGoldAccent)
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🇾🇪", fontSize = 38.sp)
            Spacer(modifier = Modifier.height(10.dp))
            page.paragraphs.forEach { paragraph ->
                val isKeyProverb = paragraph.startsWith("أمة")
                Text(
                    text = paragraph,
                    style = if (isKeyProverb) {
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = (17 * fontSizeScale).sp,
                            lineHeight = (30 * fontSizeScale).sp
                        )
                    } else {
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (15 * fontSizeScale).sp,
                            lineHeight = (26 * fontSizeScale).sp
                        )
                    },
                    fontWeight = if (isKeyProverb) FontWeight.Bold else FontWeight.Normal,
                    color = if (isKeyProverb) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (page.quoteOrNote.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                BookOrnamentalDivider()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = page.quoteOrNote,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AuthorTipBox(note: String, fontSizeScale: Float) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "💡", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "إشراقة زراعية من الدكتور مالك:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = (13 * fontSizeScale).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BookOrnamentalDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                )
        )
        Text(
            text = " ❦ 🌿 ❦ ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}
