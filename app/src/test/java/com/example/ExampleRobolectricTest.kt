package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.VietnamMapRegistry
import com.example.data.repository.VietnamSvgMapParser
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
    assertEquals("Việt Phục", appName)
  }

  @Test
  fun `test vietnam 34 provinces svg parsing from assets`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertTrue("File vietnam-34-provinces.svg phải có sẵn trong assets", VietnamSvgMapParser.isSvgAssetAvailable(context))

    context.assets.open(VietnamSvgMapParser.ASSET_FILE_NAME).use { stream ->
      val result = VietnamSvgMapParser.parse(stream)
      assertTrue("Parse SVG thành công", result.isSuccess)

      val map = result.getOrNull()
      assertNotNull("Map không được null", map)
      assertEquals("viewBox width phải là 2400", 2400f, map!!.viewBox.width)
      assertEquals("viewBox height phải là 3600", 3600f, map.viewBox.height)

      // Kiểm tra chính xác 34 tỉnh/thành
      assertEquals("Phải có chính xác 34 tỉnh/thành", 34, map.provinces.size)

      // 22 tỉnh hợp nhất, 12 tỉnh chưa hợp nhất
      val merged = map.provinces.filter { it.isMerged }
      val unmerged = map.provinces.filter { !it.isMerged }
      assertEquals("Phải có 22 tỉnh hợp nhất", 22, merged.size)
      assertEquals("Phải có 12 tỉnh chưa hợp nhất", 12, unmerged.size)

      // Kiểm tra 4 thực thể đảo/quần đảo
      assertEquals("Phải có 4 đảo/quần đảo", 4, map.islandMarkers.size)
      assertTrue("Phải có Hoàng Sa", map.islandMarkers.any { it.name.contains("HOÀNG SA") })
      assertTrue("Phải có Trường Sa", map.islandMarkers.any { it.name.contains("TRƯỜNG SA") })
      assertTrue("Phải có Phú Quốc", map.islandMarkers.any { it.name.contains("Phú Quốc") })
      assertTrue("Phải có Côn Đảo", map.islandMarkers.any { it.name.contains("Côn Đảo") })

      // Kiểm tra Registry 34 tỉnh
      assertEquals(34, VietnamMapRegistry.provinces.size)
    }
  }
}
