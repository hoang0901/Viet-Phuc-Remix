package com.example.data.model

/**
 * 4 Loại Badge phân định nguồn gốc theo đặc tả (Trang 1 & 2):
 * 1. 3D Outfit: Model 3D kỹ thuật số từ GLB/Canvas chuẩn hóa
 * 2. Ảnh chụp người thật: Ảnh chụp thực tế của người dùng hoặc bảo tàng
 * 3. AI phân tích: Kết quả phân tích có cấu trúc từ Camera Scan
 * 4. AI minh họa - cần kiểm tra cấu trúc: Ảnh sinh bởi AI (không phải nguồn xác thực văn hóa)
 */
enum class OutfitBadgeType(
    val label: String,
    val shortCode: String,
    val description: String
) {
    THREE_D_OUTFIT(
        "3D Outfit",
        "3D",
        "Mô hình 3D kỹ thuật số chuẩn hóa hình học"
    ),
    REAL_PHOTO(
        "Ảnh chụp người thật",
        "THẬT",
        "Ảnh tư liệu thực tế của người mặc"
    ),
    AI_ANALYZED(
        "AI phân tích",
        "SCAN",
        "Phân tích cấu trúc từ ảnh quét đa góc"
    ),
    AI_ILLUSTRATION_UNVERIFIED(
        "AI minh họa – cần kiểm tra cấu trúc",
        "AI",
        "Minh họa thị giác từ AI, không thay thế xác thực di sản"
    )
}

enum class CameraScanAngle(
    val id: String,
    val label: String,
    val subTitle: String,
    val guideDescription: String,
    val isMandatory: Boolean
) {
    FRONT(
        "front",
        "Chính diện",
        "Góc nhìn thẳng 0°",
        "Đứng thẳng, chụp từ cổ áo đến gấu tà để nhận diện cổ đứng, vạt chéo và ngũ khuy.",
        true
    ),
    THREE_QUARTER(
        "three_quarter",
        "Góc 3/4",
        "Góc nghiêng 35° - 45°",
        "Xoay người nhẹ để quan sát chiều sâu tay áo, độ rủ tà áo và nẹp vạt.",
        true
    ),
    SIDE(
        "side",
        "Góc cạnh",
        "Góc nghiêng 90°",
        "Chụp ngang sườn để nhận diện độ mở của tà áo, đường xẻ nách và cạp quần lụa.",
        false
    ),
    BACK(
        "back",
        "Mặt sau lưng",
        "Góc sau 180°",
        "Chụp lưng áo để kiểm tra đường may sống áo nối giữa hai thân sau.",
        true
    ),
    CLOSE_UP(
        "close_up",
        "Cận cảnh chi tiết",
        "Chi tiết cổ & khuy cài",
        "Chụp gần cổ áo, 5 cúc cài hoàng gia hoặc hoa văn thêu dệt trên nền gấm/lụa.",
        false
    )
}

enum class ConfidenceBand(
    val label: String,
    val description: String,
    val minThreshold: Float
) {
    HIGH("Độ tin cậy cao", "Cấu trúc rõ ràng, đủ điểm neo bắt buộc", 0.85f),
    MEDIUM("Độ tin cậy trung bình", "Nhận diện được lõi áo, thiếu một vài góc phụ", 0.60f),
    LOW("Cần chụp bổ sung", "Ảnh mờ hoặc thiếu góc bắt buộc, AI không đủ chắc chắn", 0.0f)
}

data class StructuralAnchor(
    val name: String,
    val description: String,
    val isMandatory: Boolean,
    val isFound: Boolean,
    val note: String
)

data class CapturedOutfitAnalysis(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val detectedGarmentType: String,
    val confidence: Float,
    val confidenceBand: ConfidenceBand,
    val detectedAnchors: List<StructuralAnchor>,
    val missingAnchors: List<String>,
    val anglesCaptured: List<CameraScanAngle>,
    val summary: String,
    val academicHeritageNote: String,
    val namingResolution: String, // E.g. "Áo ngũ thân tay chẽn nguyên bản" hoặc "Việt phục remix lấy cảm hứng từ Áo ngũ thân"
    val mappedOutfitConfig: ActiveOutfit,
    val isConsentGranted: Boolean = true
)
