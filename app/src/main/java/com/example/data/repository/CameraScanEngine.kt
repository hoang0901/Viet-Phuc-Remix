package com.example.data.repository

import com.example.data.model.*
import java.util.UUID

object CameraScanEngine {

    /**
     * Phân tích ảnh quét đa góc có cấu trúc theo quy chuẩn PRD:
     * - Nhận diện điểm neo cấu trúc: Cổ đứng, ngũ khuy, vạt đắp chéo, sống áo sau lưng, độ rủ tà
     * - Tính toán Confidence Band (Cao / Trung bình / Cần bổ sung)
     * - Áp dụng quy tắc điểm neo: Nếu mất điểm neo bắt buộc, tự động đổi tên thành:
     *   "Việt phục remix lấy cảm hứng từ [Tên áo gốc]"
     * - Map kết quả sang ActiveOutfit (OutfitConfig) đưa vào 3D Character Viewer
     */
    fun analyzeScan(
        capturedAngles: Set<CameraScanAngle>,
        simulatedGarmentId: String? = null,
        baseOutfit: ActiveOutfit
    ): CapturedOutfitAnalysis {
        val hasFront = capturedAngles.contains(CameraScanAngle.FRONT)
        val hasThreeQuarter = capturedAngles.contains(CameraScanAngle.THREE_QUARTER)
        val hasBack = capturedAngles.contains(CameraScanAngle.BACK)
        val hasSide = capturedAngles.contains(CameraScanAngle.SIDE)
        val hasCloseUp = capturedAngles.contains(CameraScanAngle.CLOSE_UP)

        // Phân tích điểm neo theo số góc chụp
        val anchors = mutableListOf<StructuralAnchor>()
        val missingAnchors = mutableListOf<String>()

        // 1. Điểm neo Cổ đứng (Mandarin Collar)
        val hasCollar = hasFront || hasCloseUp
        anchors.add(
            StructuralAnchor(
                name = "Cổ đứng (2.5 - 3.5cm)",
                description = "Cổ dựng khép kín, tròn ôm sát cổ, có viền lót trắng bên trong",
                isMandatory = true,
                isFound = hasCollar,
                note = if (hasCollar) "Đã nhận diện: Cổ đứng lót trắng ôm khít chuẩn triều Nguyễn" else "Chưa thấy rõ cổ áo do thiếu góc chụp chính diện hoặc cận cảnh"
            )
        )
        if (!hasCollar) missingAnchors.add("Cổ đứng lót trắng")

        // 2. Điểm neo Ngũ khuy (5 cúc cài hoàng tộc)
        val hasButtons = (hasFront && hasThreeQuarter) || hasCloseUp
        anchors.add(
            StructuralAnchor(
                name = "Ngũ khuy (5 cúc cài)",
                description = "Đúng 5 chiếc cúc cài bằng vàng/đồng biểu trưng cho Ngũ thường",
                isMandatory = true,
                isFound = hasButtons,
                note = if (hasButtons) "Đã nhận diện: 5 khuy cài chạy dọc từ cổ xuống nách sườn phải" else "Cần ảnh chụp gần hoặc góc 3/4 để đếm chính xác 5 cúc cài"
            )
        )
        if (!hasButtons) missingAnchors.add("Hệ 5 cúc cài (Ngũ khuy)")

        // 3. Điểm neo Vạt đắp cong sang nách phải
        val hasLapel = hasFront || hasThreeQuarter
        anchors.add(
            StructuralAnchor(
                name = "Nẹp vạt đắp cong",
                description = "Vạt áo lượn hình cánh cung sang nách sườn phải theo quy chuẩn",
                isMandatory = true,
                isFound = hasLapel,
                note = if (hasLapel) "Đã nhận diện: Vạt cong mềm mại phủ lên thân con bên trong" else "Thiếu ảnh chính diện để xác nhận đường lượn vạt chéo"
            )
        )
        if (!hasLapel) missingAnchors.add("Vạt đắp cong sang nách phải")

        // 4. Điểm neo Sống áo sau lưng
        val hasBackSeam = hasBack
        anchors.add(
            StructuralAnchor(
                name = "Sống áo may nối sau lưng",
                description = "Đường may nối giữa 2 thân sau thẳng tắp từ cổ xuống gấu tà",
                isMandatory = true,
                isFound = hasBackSeam,
                note = if (hasBackSeam) "Đã nhận diện: Đường sống áo thẳng tắp đặc trưng di sản áo ngũ thân/tứ thân" else "Cần ảnh chụp từ phía sau lưng (Back 180°) để xác thực sống áo"
            )
        )
        if (!hasBackSeam) missingAnchors.add("Đường sống áo sau lưng")

        // 5. Điểm neo Tà áo buông rủ & Quần lụa
        val hasDrapery = hasFront || hasSide || hasThreeQuarter
        anchors.add(
            StructuralAnchor(
                name = "Độ rủ tà áo & Quần lụa",
                description = "Tà áo xòe nhẹ hình cánh cung rủ trên ống quần lụa rộng suôn thẳng",
                isMandatory = false,
                isFound = hasDrapery,
                note = if (hasDrapery) "Đã nhận diện: Tà áo rủ sóng tự nhiên, phom dáng thanh thoát" else "Cần kiểm tra độ rủ của tà áo"
            )
        )

        // Tính toán độ tin cậy Confidence
        val foundMandatoryCount = anchors.count { it.isMandatory && it.isFound }
        val totalMandatory = anchors.count { it.isMandatory }

        val confidenceValue = when {
            foundMandatoryCount == totalMandatory && (hasSide || hasCloseUp) -> 0.94f
            foundMandatoryCount == totalMandatory -> 0.88f
            foundMandatoryCount >= 3 -> 0.72f
            foundMandatoryCount >= 2 -> 0.55f
            else -> 0.35f
        }

        val confidenceBand = when {
            confidenceValue >= 0.85f -> ConfidenceBand.HIGH
            confidenceValue >= 0.60f -> ConfidenceBand.MEDIUM
            else -> ConfidenceBand.LOW
        }

        // Tên định danh và Naming Resolution theo nguyên tắc "Điểm neo cấu trúc"
        val detectedName = simulatedGarmentId?.let { id ->
            when (id) {
                "ao_dai" -> "Áo Dài Truyền Thống"
                "ao_tu_than" -> "Áo Tứ Thân Bắc Bộ"
                else -> "Áo Ngũ Thân Tay Chẽn"
            }
        } ?: "Áo Ngũ Thân Tay Chẽn"

        val namingResolution = if (missingAnchors.isEmpty()) {
            "$detectedName nguyên bản"
        } else {
            "Việt phục remix lấy cảm hứng từ $detectedName"
        }

        val summary = when (confidenceBand) {
            ConfidenceBand.HIGH -> "Nhận diện thành công cấu trúc $detectedName. Toàn bộ các điểm neo bắt buộc (cổ đứng, vạt chéo, sống áo) đều hiện diện rõ nét."
            ConfidenceBand.MEDIUM -> "Nhận diện cơ bản $detectedName. Một số chi tiết sau lưng hoặc khuy cài chưa đủ góc chụp rõ ràng, cần chụp thêm để đạt độ tin cậy tối đa."
            ConfidenceBand.LOW -> "Chưa đủ dữ liệu ảnh góc bắt buộc. Vui lòng chụp bổ sung góc Mặt sau (Back) hoặc Cận cảnh cổ/khuy để AI xác thực cấu trúc chuẩn xác."
        }

        val academicNote = "Theo tư liệu Bảo tàng Lịch sử Quốc gia: Việc xác thực Việt phục dựa trên hệ quy chiếu 'điểm neo cấu trúc' thay vì phán xét cảm tính. Khi trang phục thiếu hoặc biến đổi điểm neo, ứng dụng tôn trọng sáng tạo bằng cách định danh minh bạch: 'Việt phục remix lấy cảm hứng từ...'"

        // Map sang ActiveOutfit tương ứng
        val mappedGarment = when {
            detectedName.contains("Tứ Thân", ignoreCase = true) -> VietPhucRepository.garments.firstOrNull { it.id == "ao_tu_than" } ?: VietPhucRepository.garments[0]
            detectedName.contains("Áo Dài", ignoreCase = true) -> VietPhucRepository.garments.firstOrNull { it.id == "ao_dai_truyen_thong" } ?: VietPhucRepository.garments[0]
            else -> VietPhucRepository.garments.firstOrNull { it.id == "ao_ngu_than_tay_chen" } ?: VietPhucRepository.garments[0]
        }

        val mappedOutfit = baseOutfit.copy(
            mainGarment = mappedGarment,
            name = namingResolution
        )

        return CapturedOutfitAnalysis(
            id = UUID.randomUUID().toString(),
            detectedGarmentType = detectedName,
            confidence = confidenceValue,
            confidenceBand = confidenceBand,
            detectedAnchors = anchors,
            missingAnchors = missingAnchors,
            anglesCaptured = capturedAngles.toList(),
            summary = summary,
            academicHeritageNote = academicNote,
            namingResolution = namingResolution,
            mappedOutfitConfig = mappedOutfit,
            isConsentGranted = true
        )
    }
}
