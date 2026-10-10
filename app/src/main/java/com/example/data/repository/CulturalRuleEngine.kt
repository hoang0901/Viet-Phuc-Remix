package com.example.data.repository

import com.example.data.model.*

object CulturalRuleEngine {

    fun evaluate(outfit: ActiveOutfit): Pair<CulturalCheckResult, LookScore> {
        val garment = outfit.mainGarment
        val bottom = outfit.bottom
        val footwear = outfit.footwear
        val inner = outfit.innerGarment
        val headwear = outfit.headwear
        val accessory = outfit.accessory
        val mode = outfit.mode
        val occasion = outfit.occasion

        val modernItems = mutableListOf<String>()
        if (!bottom.isHistoricalPiece) modernItems.add(bottom.name)
        if (!footwear.isHistoricalPiece) modernItems.add(footwear.name)
        if (inner != null && !inner.isHistoricalPiece) modernItems.add(inner.name)
        if (headwear != null && !headwear.isHistoricalPiece) modernItems.add(headwear.name)
        if (accessory != null && !accessory.isHistoricalPiece) modernItems.add(accessory.name)

        val checkResult: CulturalCheckResult
        val culturalStatus: String

        // =========================================================================
        // 4 TRẠNG THÁI CULTURAL CHECK (Theo đặc tả trang 3):
        // ✓ Phù hợp → ! Lưu ý → ⚠ Cảnh báo mạnh → ? Cần kiểm chứng
        // =========================================================================

        when (mode) {
            CulturalMode.HERITAGE -> {
                if (modernItems.isNotEmpty()) {
                    val conflictItem = modernItems.first()
                    // Trạng thái ! Lưu ý hoặc ⚠ Cảnh báo mạnh
                    if (conflictItem.contains("Jeans", ignoreCase = true)) {
                        checkResult = CulturalCheckResult(
                            severity = CulturalCheckSeverity.STRONG_WARNING,
                            title = "⚠ Cảnh báo mạnh: Sai lệch cấu trúc Nét Gốc",
                            labelBadge = "Xung đột chuẩn mực",
                            explanation = "$conflictItem là phục trang denim phương Tây hiện đại, hoàn toàn không tương thích với chế độ Nét Gốc của ${garment.name}.",
                            academicNote = "Chế độ 'Nét Gốc' yêu cầu giữ nguyên phom dáng quần lụa ống rộng truyền thống. Nếu bạn muốn phối phá cách cùng $conflictItem, hãy chuyển sang chế độ 'Remix Gen Z'.",
                            ruleType = "Structure rule",
                            citations = listOf(
                                "Luật Di sản văn hóa (Văn bản hợp nhất số 51/VBHN-VPQH)",
                                "Bảo tàng Lịch sử Quốc gia",
                                "TTXVN / VietnamPlus"
                            ),
                            sourceId = "VBHN-51/2026/VPQH",
                            confidence = "Rất cao",
                            reviewStatus = "Đã kiểm duyệt chuyên gia",
                            reviewer = "Hội đồng Cố vấn Di sản"
                        )
                        culturalStatus = "Cần chỉnh sửa bối cảnh"
                    } else {
                        // Trạng thái ! Lưu ý theo đúng ví dụ trang 3 của PDF
                        checkResult = CulturalCheckResult(
                            severity = CulturalCheckSeverity.CAUTION,
                            title = "! Lưu ý: Giày & phụ kiện hiện đại",
                            labelBadge = "Lưu ý nguyên bản",
                            explanation = "$conflictItem làm giảm mức nguyên bản của bộ trang phục.",
                            academicNote = "Bạn có thể đổi sang phụ kiện truyền thống (guốc mộc, hài thêu) để giữ trọn vẹn Nét Gốc, hoặc chuyển sang chế độ 'Giao Thoa' để giữ yếu tố hiện đại.",
                            ruleType = "Context & Mode rule",
                            citations = listOf(
                                "Hội thảo quốc gia về Áo dài (Bảo tàng Phụ nữ Việt Nam)",
                                "TTXVN / VietnamPlus"
                            ),
                            sourceId = "VBHN-51/2026/VPQH",
                            confidence = "Cao",
                            reviewStatus = "Đã kiểm duyệt",
                            reviewer = "Ban Biên tập Di sản"
                        )
                        culturalStatus = "Nét Gốc Cần Lưu Ý"
                    }
                } else if (garment.id == "ao_tu_than" && headwear?.id == "khan_van") {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.NEEDS_VERIFICATION,
                        title = "? Cần kiểm chứng: Giao lưu vùng miền",
                        labelBadge = "Cần kiểm chứng bối cảnh",
                        explanation = "Áo tứ thân gắn liền không gian văn hóa Bắc Bộ (nón quai thao, khăn mỏ quạ). Khăn vấn kiểu Huế là dạng phục sức Trung Bộ.",
                        academicNote = "Sự kết hợp này có thể xem là giao lưu vùng miền trong trình diễn nghệ thuật, nhưng chưa có cứ liệu lịch sử cổ truyền nguyên bản.",
                        ruleType = "Region rule",
                        citations = listOf(
                            "Thời trang nữ - Bảo tàng Phụ nữ Việt Nam",
                            "Viện Văn hóa Nghệ thuật Quốc gia"
                        ),
                        sourceId = "BTPN-2025/VN",
                        confidence = "Đang thảo luận",
                        reviewStatus = "Cần kiểm chứng",
                        reviewer = "Hội đồng Cố vấn Di sản"
                    )
                    culturalStatus = "Giao Lưu Vùng Miền"
                } else {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.FIT,
                        title = "✓ Phù hợp: Nét Gốc nguyên bản",
                        labelBadge = "Nét Gốc Chuẩn Mực",
                        explanation = "Cách phối giữ trọn vẹn các điểm nhận diện đã được nguồn và người duyệt xác nhận.",
                        academicNote = "Cấu trúc áo chính, chất liệu, phụ kiện và bối cảnh đồng nhất với chuẩn mực lịch sử.",
                        ruleType = "Structure rule",
                        citations = listOf(
                            "Văn bản hợp nhất số 51/VBHN-VPQH về Luật Di sản văn hóa",
                            "Bảo tàng Cổ vật Cung đình Huế",
                            "TTXVN / VietnamPlus"
                        ),
                        sourceId = "VBHN-51/2026/VPQH",
                        confidence = "Rất cao",
                        reviewStatus = "Đã kiểm duyệt chính thức",
                        reviewer = "Viện Di sản & Bảo tàng"
                    )
                    culturalStatus = "Nét Gốc Chuẩn Mực"
                }
            }

            CulturalMode.FUSION -> {
                if (inner?.id == "ao_cu_nhan") {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.FIT,
                        title = "✓ Phù hợp: Giao thoa lễ tốt nghiệp",
                        labelBadge = "Giao Thoa Tốt Nghiệp",
                        explanation = "Áo cử nhân khoác ngoài kết hợp tôn vinh áo ngũ thân/áo dài bên trong ngày ra trường.",
                        academicNote = "Hiện tượng đương đại được báo chí chính thống (Thanh Niên, Báo Văn Hóa) ghi nhận tích cực. Thể hiện niềm tự hào di sản của thế hệ trẻ.",
                        ruleType = "Context rule",
                        citations = listOf(
                            "Báo Thanh Niên (Sinh viên diện Việt phục ngày tốt nghiệp 2026)",
                            "Báo Văn Hóa"
                        ),
                        sourceId = "TNO-2026-GRAD",
                        confidence = "Cao",
                        reviewStatus = "Đã kiểm duyệt",
                        reviewer = "Hội đồng Cố vấn Di sản"
                    )
                    culturalStatus = "Giao Thoa Đương Đại"
                } else if (modernItems.isNotEmpty()) {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.CAUTION,
                        title = "! Lưu ý: Yếu tố đương đại thanh lịch",
                        labelBadge = "Giao Thoa Thanh Lịch",
                        explanation = "${modernItems.joinToString(", ")} là phụ kiện hiện đại. Lõi trang phục chính vẫn được bảo toàn nguyên vẹn.",
                        academicNote = "Cách phối này phù hợp cho đời sống hiện đại, dạo phố và công sở. Tuyệt đối minh bạch ranh giới, không ngộ nhận là phục dựng lịch sử.",
                        ruleType = "Remix rule",
                        citations = listOf(
                            "Hội thảo khoa học quốc gia về Áo dài (Bảo tàng Phụ nữ Việt Nam)",
                            "Báo Văn Hóa (Cách tân áo ngũ thân cho người trẻ)"
                        ),
                        sourceId = "BTPN-2025/AD",
                        confidence = "Cao",
                        reviewStatus = "Đã kiểm duyệt",
                        reviewer = "Hội đồng Cố vấn Di sản"
                    )
                    culturalStatus = "Giao Thoa An Toàn"
                } else {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.FIT,
                        title = "✓ Phù hợp: Giao thoa chuẩn mực",
                        labelBadge = "Thanh Lịch Cổ Phong",
                        explanation = "Giữ trọn vẹn đường nét cổ truyền nhưng toát lên thần thái tươi sáng, thanh lịch.",
                        academicNote = "Điểm neo nhận diện của ${garment.name} nổi bật, tôn vinh cốt cách người mặc.",
                        ruleType = "Structure rule",
                        citations = listOf("Cục Di sản Văn hóa", "Bảo tàng Phụ nữ Việt Nam"),
                        sourceId = "VBHN-51/2026/VPQH",
                        confidence = "Rất cao",
                        reviewStatus = "Đã kiểm duyệt",
                        reviewer = "Hội đồng Cố vấn Di sản"
                    )
                    culturalStatus = "Giao Thoa Chuẩn Mực"
                }
            }

            CulturalMode.REMIX_GEN_Z -> {
                if (modernItems.size >= 3) {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.CAUTION,
                        title = "! Lưu ý: Chuyển nhãn 'Lấy cảm hứng từ…'",
                        labelBadge = "Lấy cảm hứng di sản",
                        explanation = "Look phối nhiều yếu tố cách tân. Theo quy tắc của đề tài, khi mất một phần điểm neo thì gọi là 'Việt phục remix lấy cảm hứng từ ${garment.name}'.",
                        academicNote = "Nguyên tắc: 'Remix được phép, nhưng remix phải được gọi đúng là remix'. Sáng tạo cá nhân được khuyến khích khi đi kèm sự minh bạch.",
                        ruleType = "Naming rule",
                        citations = listOf(
                            "Nguyên tắc đạo đức UNESCO về Di sản văn hóa phi vật thể",
                            "Báo Văn Hóa"
                        ),
                        sourceId = "UNESCO-ICH-2026",
                        confidence = "Định hướng chuẩn mực",
                        reviewStatus = "Đã kiểm duyệt",
                        reviewer = "Hội đồng Cố vấn Di sản"
                    )
                    culturalStatus = "Lấy Cảm Hứng Di Sản"
                } else {
                    checkResult = CulturalCheckResult(
                        severity = CulturalCheckSeverity.FIT,
                        title = "✓ Phù hợp: Remix Gen Z cá tính",
                        labelBadge = "Remix Đương Đại",
                        explanation = "Giữ vững điểm neo cấu trúc áo chính kết hợp tinh tế cùng phụ kiện hiện đại.",
                        academicNote = "Sáng tạo trẻ trung, mang lại luồng sinh khí mới cho trang phục truyền thống trong nhịp sống đương đại.",
                        ruleType = "Remix rule",
                        citations = listOf("Báo Thanh Niên", "Báo Văn Hóa"),
                        sourceId = "GENZ-REMIX-2026",
                        confidence = "Cao",
                        reviewStatus = "Đã kiểm duyệt",
                        reviewer = "Hội đồng Cố vấn Di sản"
                    )
                    culturalStatus = "Remix Đương Đại"
                }
            }
        }

        // =========================================================================
        // THUẬT TOÁN CHẤM ĐIỂM XẾP HẠNG (Score Formula)
        // Score = 0.40 * EventMatch + 0.30 * ColorHarmony + 0.20 * PersonalStyle + 0.10 * Practicality
        // =========================================================================
        var eventMatch = 82
        if (occasion.recommendedGarments.contains(garment.category)) eventMatch += 10
        if (occasion == OccasionEvent.GRADUATION && inner?.id == "ao_cu_nhan") eventMatch += 6
        if (occasion == OccasionEvent.TET && (outfit.primaryColor.id.contains("do") || outfit.primaryColor.id.contains("vang"))) eventMatch += 4
        eventMatch = eventMatch.coerceIn(60, 98)

        var colorHarmony = 84
        if (outfit.primaryColor.isHeritageTone) colorHarmony += 5
        if (outfit.primaryColor.id == "xanh_reu" && outfit.secondaryColor.id == "kem") colorHarmony += 8
        if (outfit.primaryColor.id == "do_son" && outfit.secondaryColor.id == "vang_tram") colorHarmony += 6
        if (outfit.primaryColor.id == "xanh_navy" && outfit.secondaryColor.id == "kem") colorHarmony += 7
        colorHarmony = colorHarmony.coerceIn(65, 96)

        var personalStyle = 85
        if (mode == CulturalMode.REMIX_GEN_Z && modernItems.isNotEmpty()) personalStyle += 7
        if (mode == CulturalMode.HERITAGE && modernItems.isEmpty()) personalStyle += 9
        if (mode == CulturalMode.FUSION) personalStyle += 6
        personalStyle = personalStyle.coerceIn(70, 96)

        var practicality = 80
        if (footwear.id == "sneaker" || footwear.id == "loafer") practicality += 10
        if (bottom.id == "quan_au" || bottom.id == "quan_lua_rong") practicality += 5
        practicality = practicality.coerceIn(60, 95)

        val score = LookScore(
            eventMatch = eventMatch,
            colorHarmony = colorHarmony,
            personalStyle = personalStyle,
            practicality = practicality,
            culturalStatus = culturalStatus
        )

        return Pair(checkResult, score)
    }
}
