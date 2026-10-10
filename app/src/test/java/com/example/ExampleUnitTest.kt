package com.example

import com.example.data.model.*
import com.example.data.repository.VietPhucRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testActiveOutfitBodyMetricsAndTraditionalMeasurements() {
    val defaultOutfit = VietPhucRepository.getHueMinimalOutfit()
    assertEquals(165, defaultOutfit.heightCm)
    assertEquals(52, defaultOutfit.weightKg)

    // BMI: 52 / (1.65 * 1.65) = 19.1 -> Cân đối chuẩn Việt
    assertTrue(defaultOutfit.bmi in 18.5f..23.0f)
    assertEquals("Cân đối chuẩn Việt", defaultOutfit.bodyTypeDesc)

    // Thước Tấc Cổ Truyền
    val traditional = defaultOutfit.traditionalMeasurements
    assertTrue(traditional.contains("Dài áo:"))
    assertTrue(traditional.contains("tấc"))
    assertTrue(traditional.contains("Dài tay:"))
    assertTrue(traditional.contains("Vòng cổ:"))
  }

  @Test
  fun testBodyMetricsVariations() {
    val slimOutfit = VietPhucRepository.getHueMinimalOutfit().copy(heightCm = 175, weightKg = 50)
    assertTrue(slimOutfit.bmi < 18.5f)
    assertEquals("Thanh mảnh", slimOutfit.bodyTypeDesc)

    val robustOutfit = VietPhucRepository.getHueMinimalOutfit().copy(heightCm = 170, weightKg = 75)
    assertTrue(robustOutfit.bmi >= 25.0f)
  }
}
