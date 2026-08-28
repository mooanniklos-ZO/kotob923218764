package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BookRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("استكفِ مما تزرع", appName)
  }

  @Test
  fun `verify book pages count and content integrity`() {
    assertEquals(15, BookRepository.pages.size)
    assertEquals("الدكتور مالك عبدالرحمن الرميمة", BookRepository.AUTHOR_NAME)
    assertEquals("771134103", BookRepository.AUTHOR_PHONE)
    assertTrue(BookRepository.cropCatalog.isNotEmpty())
  }
}
