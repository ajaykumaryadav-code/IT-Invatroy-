package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Facility
import com.example.data.model.ItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("IT Inventory", appName)
  }

  @Test
  fun `facility locations include central nodes`() {
    val facilities = Facility.locationList()
    assertTrue(facilities.contains(Facility.LUCKNOW))
    assertTrue(facilities.contains(Facility.KANPUR))
    assertTrue(facilities.contains(Facility.AYODHYA))
  }

  @Test
  fun `categories cover IT hardware network and software`() {
    val categories = ItemCategory.realCategories()
    assertTrue(categories.contains(ItemCategory.HARDWARE))
    assertTrue(categories.contains(ItemCategory.NETWORK))
    assertTrue(categories.contains(ItemCategory.CONSUMABLES))
    assertTrue(categories.contains(ItemCategory.SOFTWARE))
  }
}
