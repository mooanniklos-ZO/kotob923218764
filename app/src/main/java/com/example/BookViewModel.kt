package com.example

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookPage
import com.example.data.BookRepository
import com.example.data.CropCategory
import com.example.data.CropInfo
import com.example.data.GardeningNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GardenTask(
    val id: String,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false,
    val tip: String
)

data class BookUiState(
    val currentPageIndex: Int = 0, // 0 to 14 (Pages 1 to 15)
    val is3DViewMode: Boolean = true,
    val fontSizeScale: Float = 1.0f, // 0.8f to 1.4f
    val bookmarkedPages: Set<Int> = setOf(1, 4, 10),
    val searchQuery: String = "",
    val searchResults: List<BookPage> = emptyList(),
    val isTtsPlaying: Boolean = false,
    val ttsSpeed: Float = 1.0f,
    val selectedCropCategory: CropCategory = CropCategory.ALL,
    val cropSearchQuery: String = "",
    val selectedCrop: CropInfo? = null,
    val personalNotes: List<GardeningNote> = listOf(
        GardeningNote(
            id = 1L,
            pageNumber = 10,
            title = "تجربة الري بالتنقيط للأحواض المنزلية",
            content = "تم تركيب شبكة أنابيب صغيرة موصلة بخزان 200 لتر لزراعة الطماطم والخيار، والنتيجة ممتازة وتوفير كبير في الماء.",
            dateString = "2024-05-12"
        ),
        GardeningNote(
            id = 2L,
            pageNumber = 5,
            title = "تسوير حوض السطح بالشباك",
            content = "استخدمنا الشباك الحديدي بارتفاع متر لمنع الطيور والقطط، مع إضافة سماد بلدي معالج.",
            dateString = "2024-06-01"
        )
    ),
    val gardenTasks: List<GardenTask> = listOf(
        GardenTask("1", "إحكام تسوير المساحة بالشباك الحديدي", "تجهيز المساحة", true, "وضع مدخل ومخرج واحد لحماية المحصول من الحيوانات"),
        GardenTask("2", "فحص وتسميد التربة بالسماد الأبيض والعضوي", "التربة والتسميد", false, "يضاف قبل هطول الأمطار أو قبل الري المباشر"),
        GardenTask("3", "تقسيم التربة إلى أحواض صغيرة منفصلة", "طرق الزراعة", false, "زرع كل صنف في حوض مستقل لتنظيم النمو"),
        GardenTask("4", "تثبيت دعائم وحبال للأشجار ذات الثمار الثقيلة", "العناية والأشجار", false, "مثل الطماطم والباذنجان لمنع انكسار الغصون"),
        GardenTask("5", "فحص شبكة الري بالتنقيط وخزان الماء", "الري الحديث", false, "ري منتظم موفر للماء في الصباح الباكر"),
        GardenTask("6", "تجهيز أوعية وأكياس لحفظ البذور", "استدامة البذور", false, "حفظ في مكان جاف ومكيف بعيداً عن الرطوبة")
    ),
    val activeTab: NavigationTab = NavigationTab.BOOK_READER
)

enum class NavigationTab(val title: String, val iconName: String) {
    BOOK_READER("الكتاب 3D", "MenuBook"),
    CROPS_ENCYCLOPEDIA("موسوعة المحاصيل", "Yard"),
    GARDEN_PLANNER("المخطط الزراعي", "Agriculture"),
    NOTES_BOOKMARKS("الملاحظات", "Bookmark"),
    ABOUT_AUTHOR("عن المؤلف", "Person")
}

class BookViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            val arabicLocale = Locale("ar")
            val result = tts?.setLanguage(arabicLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _uiState.update { it.copy(isTtsPlaying = true) }
                }

                override fun onDone(utteranceId: String?) {
                    _uiState.update { it.copy(isTtsPlaying = false) }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _uiState.update { it.copy(isTtsPlaying = false) }
                }
            })
        }
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun goToPage(pageIndex: Int) {
        val clampedIndex = pageIndex.coerceIn(0, BookRepository.pages.size - 1)
        _uiState.update { it.copy(currentPageIndex = clampedIndex) }
        if (_uiState.value.isTtsPlaying) {
            speakCurrentPage()
        }
    }

    fun nextPage() {
        val next = (_uiState.value.currentPageIndex + 1).coerceAtMost(BookRepository.pages.size - 1)
        goToPage(next)
    }

    fun prevPage() {
        val prev = (_uiState.value.currentPageIndex - 1).coerceAtLeast(0)
        goToPage(prev)
    }

    fun toggle3DMode() {
        _uiState.update { it.copy(is3DViewMode = !it.is3DViewMode) }
    }

    fun changeFontSize(increase: Boolean) {
        _uiState.update { state ->
            val newScale = if (increase) {
                (state.fontSizeScale + 0.1f).coerceAtMost(1.5f)
            } else {
                (state.fontSizeScale - 0.1f).coerceAtLeast(0.8f)
            }
            state.copy(fontSizeScale = newScale)
        }
    }

    fun toggleBookmark(pageNumber: Int) {
        _uiState.update { state ->
            val updatedBookmarks = state.bookmarkedPages.toMutableSet()
            if (updatedBookmarks.contains(pageNumber)) {
                updatedBookmarks.remove(pageNumber)
            } else {
                updatedBookmarks.add(pageNumber)
            }
            state.copy(bookmarkedPages = updatedBookmarks)
        }
    }

    fun searchBook(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                emptyList()
            } else {
                val q = query.trim().lowercase()
                BookRepository.pages.filter { page ->
                    page.mainTitle.lowercase().contains(q) ||
                            page.subtitle.lowercase().contains(q) ||
                            page.paragraphs.any { it.lowercase().contains(q) } ||
                            page.bulletPoints.any { it.lowercase().contains(q) } ||
                            page.quoteOrNote.lowercase().contains(q) ||
                            page.warningText.lowercase().contains(q)
                }
            }
            state.copy(searchQuery = query, searchResults = filtered)
        }
    }

    fun setCropCategory(category: CropCategory) {
        _uiState.update { it.copy(selectedCropCategory = category) }
    }

    fun setCropSearchQuery(query: String) {
        _uiState.update { it.copy(cropSearchQuery = query) }
    }

    fun selectCrop(crop: CropInfo?) {
        _uiState.update { it.copy(selectedCrop = crop) }
    }

    fun toggleTask(taskId: String) {
        _uiState.update { state ->
            val updated = state.gardenTasks.map {
                if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
            }
            state.copy(gardenTasks = updated)
        }
    }

    fun addNote(title: String, content: String, pageNumber: Int) {
        if (title.isBlank() && content.isBlank()) return
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val note = GardeningNote(
            pageNumber = pageNumber,
            title = title.ifBlank { "ملاحظة زراعية (صفحة $pageNumber)" },
            content = content,
            dateString = dateStr
        )
        _uiState.update { state ->
            state.copy(personalNotes = listOf(note) + state.personalNotes)
        }
    }

    fun deleteNote(noteId: Long) {
        _uiState.update { state ->
            state.copy(personalNotes = state.personalNotes.filter { it.id != noteId })
        }
    }

    fun toggleTts() {
        if (_uiState.value.isTtsPlaying) {
            stopTts()
        } else {
            speakCurrentPage()
        }
    }

    private fun speakCurrentPage() {
        if (!isTtsInitialized || tts == null) return
        val currentPage = BookRepository.pages[_uiState.value.currentPageIndex]
        val textToRead = buildString {
            append(currentPage.mainTitle)
            append(". ")
            if (currentPage.subtitle.isNotEmpty()) {
                append(currentPage.subtitle)
                append(". ")
            }
            currentPage.paragraphs.forEach { append(it); append(". ") }
            currentPage.bulletPoints.forEach { append(it); append(". ") }
            if (currentPage.warningText.isNotEmpty()) {
                append("تنبيه: "); append(currentPage.warningText); append(". ")
            }
            if (currentPage.quoteOrNote.isNotEmpty()) {
                append(currentPage.quoteOrNote); append(". ")
            }
        }

        tts?.setSpeechRate(_uiState.value.ttsSpeed)
        tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "PageUtterance_${currentPage.pageNumber}")
        _uiState.update { it.copy(isTtsPlaying = true) }
    }

    fun setTtsSpeed(speed: Float) {
        _uiState.update { it.copy(ttsSpeed = speed) }
        tts?.setSpeechRate(speed)
    }

    fun stopTts() {
        tts?.stop()
        _uiState.update { it.copy(isTtsPlaying = false) }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
