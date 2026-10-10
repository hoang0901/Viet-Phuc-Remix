package com.example.data.model

// 4 Trạng thái Cultural Check theo đặc tả mới (Hồ sơ hoàn chỉnh trang 3):
// ✓ Phù hợp → ! Lưu ý → ⚠ Cảnh báo mạnh → ? Cần kiểm chứng
enum class CulturalCheckSeverity(
    val symbol: String,
    val displayName: String
) {
    FIT("✓", "Phù hợp"),
    CAUTION("!", "Lưu ý"),
    STRONG_WARNING("⚠", "Cảnh báo mạnh"),
    NEEDS_VERIFICATION("?", "Cần kiểm chứng")
}

data class CulturalCheckResult(
    val severity: CulturalCheckSeverity,
    val title: String,
    val labelBadge: String,
    val explanation: String,
    val academicNote: String,
    val ruleType: String, // "Structure rule", "Context rule", "Remix rule", "Naming rule", "Era rule"
    val citations: List<String>,
    val sourceId: String = "VBHN-51/2026/VPQH",
    val confidence: String = "Cao",
    val reviewStatus: String = "Đã kiểm duyệt",
    val reviewer: String = "Hội đồng Cố vấn Di sản"
)

data class LookScore(
    val eventMatch: Int,      // 0 - 100
    val colorHarmony: Int,    // 0 - 100
    val personalStyle: Int,   // 0 - 100
    val practicality: Int,    // 0 - 100
    val culturalStatus: String // E.g. "Giao Thoa An Toàn", "Remix Đương Đại", "Nét Gốc Chuẩn Mực"
) {
    // Thuật toán: Score = 0.40 * EventMatch + 0.30 * ColorHarmony + 0.20 * PersonalStyle + 0.10 * Practicality
    val totalScore: Int
        get() = (0.40f * eventMatch + 0.30f * colorHarmony + 0.20f * personalStyle + 0.10f * practicality).toInt().coerceIn(0, 100)
}

enum class AvatarAngle(val label: String, val rotationDesc: String) {
    FRONT("Chính diện", "Góc nhìn thẳng 0°"),
    THREE_QUARTER("Góc 3/4", "Góc nghiêng 45°"),
    BACK("Góc sau", "Góc nhìn sau 180°")
}

enum class AvatarGender(val label: String, val symbol: String) {
    FEMALE("Nữ", "♀"),
    MALE("Nam", "♂")
}

data class ActiveOutfit(
    val id: String,
    val name: String,
    val occasion: OccasionEvent,
    val mode: CulturalMode,
    val mainGarment: GarmentItem,
    val innerGarment: GarmentItem?,
    val bottom: GarmentItem,
    val footwear: GarmentItem,
    val headwear: GarmentItem?,
    val accessory: GarmentItem?,
    val hairstyle: GarmentItem?,
    val primaryColor: ColorOption,
    val secondaryColor: ColorOption,
    val customNotes: String = "",
    val viewAngle: AvatarAngle = AvatarAngle.FRONT,
    val gender: AvatarGender = AvatarGender.FEMALE,
    val heightCm: Int = 165, // Chiều cao (150 - 190 cm)
    val weightKg: Int = 52   // Cân nặng (40 - 90 kg)
) {
    val bmi: Float
        get() {
            val hM = heightCm / 100f
            return if (hM > 0f) weightKg / (hM * hM) else 20.0f
        }

    val bodyTypeDesc: String
        get() = when {
            bmi < 18.5f -> "Thanh mảnh"
            bmi < 23.0f -> "Cân đối chuẩn Việt"
            bmi < 26.0f -> "Đầy đặn / Vạm vỡ"
            else -> "Đậm đà"
        }

    val fitAdvice: String
        get() = when {
            bmi < 18.5f -> "Dáng người thanh mảnh: Áo ngũ thân tay thụng (Áo tấc) hoặc vải gấm dệt dày dặn sẽ tạo phom đứng đắn, tôn thần thái nho nhã."
            bmi < 23.0f -> "Chỉ số BMI chuẩn mực người Việt! Rất hợp diện mọi dáng áo từ tay chẽn gọn gàng đến áo tấc trang nghiêm."
            bmi < 26.0f -> "Vóc người đầy đặn: Phom suông xòe cánh cung của áo ngũ thân tự nhiên che khuyết điểm eo, kết hợp quần lụa ống rộng giúp vóc dáng cao ráo."
            else -> "Nên chọn tà áo ngũ thân dài qua gối 15cm cùng tông màu trầm (Xanh navy, Xanh rêu, Đỏ sẫm) để tạo cảm giác thon thả, đĩnh đạc."
        }

    val traditionalMeasurements: String
        get() {
            // 1 thước ta = 40cm, 1 tấc = 4cm, 1 phân = 0.4cm (Quy chuẩn may đo triều Nguyễn)
            val daiAoTac = String.format(java.util.Locale.US, "%.1f", (heightCm * 0.68f) / 4f)
            val daiTayTac = String.format(java.util.Locale.US, "%.1f", (heightCm * 0.42f) / 4f)
            val vongCoTac = String.format(java.util.Locale.US, "%.1f", (if (gender == AvatarGender.MALE) 38f else 34f) / 4f)
            return "Dài áo: $daiAoTac tấc • Dài tay: $daiTayTac tấc • Vòng cổ: $vongCoTac tấc"
        }
}
