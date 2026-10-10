package com.example.data.repository

import com.example.data.local.SavedLookDao
import com.example.data.local.SavedLookEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VietPhucRepository(private val savedLookDao: SavedLookDao) {

    // ==========================================
    // ROOM LOCAL DATABASE PERSISTENCE FLOWS
    // ==========================================
    fun getAllSavedLooks(): Flow<List<SavedLookEntity>> = savedLookDao.getAllSavedLooks()

    suspend fun saveOutfit(outfit: ActiveOutfit, lookScore: LookScore): Long {
        val entity = SavedLookEntity(
            lookName = outfit.name,
            occasionId = outfit.occasion.id,
            modeId = outfit.mode.id,
            mainGarmentId = outfit.mainGarment.id,
            innerGarmentId = outfit.innerGarment?.id,
            bottomId = outfit.bottom.id,
            footwearId = outfit.footwear.id,
            headwearId = outfit.headwear?.id,
            accessoryId = outfit.accessory?.id,
            hairstyleId = outfit.hairstyle?.id,
            primaryColorId = outfit.primaryColor.id,
            secondaryColorId = outfit.secondaryColor.id,
            totalScore = lookScore.totalScore,
            culturalStatus = lookScore.culturalStatus,
            userNotes = outfit.customNotes
        )
        return savedLookDao.insertLook(entity)
    }

    suspend fun deleteLook(id: Long) = savedLookDao.deleteLookById(id)

    // Dataset accessors delegating to companion object
    val colorPalettes get() = Companion.colorPalettes
    val garments get() = Companion.garments
    val innerGarments get() = Companion.innerGarments
    val bottoms get() = Companion.bottoms
    val footwear get() = Companion.footwear
    val headwear get() = Companion.headwear
    val accessories get() = Companion.accessories
    val heritageArticles get() = Companion.heritageArticles
    fun getDefaultOutfit(occasion: OccasionEvent = OccasionEvent.GRADUATION) = Companion.getDefaultOutfit(occasion)
    fun getHueMinimalOutfit() = Companion.getHueMinimalOutfit()
    fun getTetHeritageOutfit() = Companion.getTetHeritageOutfit()

    companion object {

        // ==========================================
        // DATASET: COLOR PALETTES
        // ==========================================
        val colorPalettes = listOf(
        ColorOption("xanh_reu", "Xanh Rêu Cổ Phong", 0xFF2C4A3E, isHeritageTone = true, visualDesc = "Trầm ổn, thanh tịnh của di sản cố đô"),
        ColorOption("do_son", "Đỏ Son Chu Sa", 0xFFBA2D2C, isHeritageTone = true, visualDesc = "Rực rỡ, may mắn trong lễ hội và Tết"),
        ColorOption("vang_tram", "Vàng Trầm Hoàng Tộc", 0xFFC98A3C, isHeritageTone = true, visualDesc = "Sang trọng, tôn nghiêm gắn liền lịch sử"),
        ColorOption("xanh_ngoc", "Xanh Ngọc Bích", 0xFF1B6A58, isHeritageTone = true, visualDesc = "Thanh tao, tươi mát và quý phái"),
        ColorOption("xanh_navy", "Xanh Chàm Navy", 0xFF1D2A44, isHeritageTone = true, visualDesc = "Hiện đại, đĩnh đạc và dễ phối đồ"),
        ColorOption("kem", "Kem Trắng Ngà", 0xFFF5EFE6, isHeritageTone = true, visualDesc = "Trang nhã, tối giản, cân bằng mọi gam màu"),
        ColorOption("hong_sen", "Hồng Cánh Sen", 0xFFDE7C88, isHeritageTone = true, visualDesc = "Duyên dáng, nữ tính nhẹ nhàng"),
        ColorOption("den_tuyen", "Đen Tuyền Tuyệt Sắc", 0xFF1E2321, isHeritageTone = true, visualDesc = "Cổ điển, sắc nét, tương phản ấn tượng")
    )

    // ==========================================
    // DATASET: GARMENTS WITH HISTORY & STYLE LAYERS
    // ==========================================
    val garments = listOf(
        GarmentItem(
            id = "ao_ngu_than_tay_chen",
            name = "Áo Ngũ Thân Tay Chẽn",
            layerType = GarmentLayerType.MAIN_GARMENT,
            category = "ao_ngu_than",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Định hình thời chúa Nguyễn Phúc Khoát (1744) - Chuẩn hóa thời Minh Mạng",
                culturalSpace = "Toàn quốc (Đặc biệt phát triển rực rỡ tại Huế)",
                structuralFeatures = listOf(
                    "Cấu trúc 5 thân (4 thân ngoài tượng trưng tứ thân phụ mẫu, 1 thân con bên trong)",
                    "5 cúc cài bên sườn tượng trưng ngũ thường (Nhân, Lễ, Nghĩa, Trí, Tín)",
                    "Cổ đứng dựng cao 2.5 - 3.5cm thẳng đứng, khép kín tôn kính",
                    "Ống tay chẽn may ôm gọn gàng từ khuỷu tay xuống cổ tay"
                ),
                historicalContext = "Trang phục hàng ngày và lễ nghi phổ quát của người Việt thế kỷ 18 đến đầu thế kỷ 20, dành cho cả nam lẫn nữ.",
                confidenceNote = "Nguồn gốc định hình từ cải cách Đàng Trong năm 1744 được đông đảo nhà nghiên cứu ghi nhận, dù vẫn có những luận giải bổ sung về tiền đề trước đó.",
                citations = listOf("TTXVN / VietnamPlus", "Báo Văn Hóa", "Trung tâm Bảo tồn Di tích Cố đô Huế")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[0], colorPalettes[4], colorPalettes[2]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Minimal", "Heritage", "Thanh lịch", "Tốt nghiệp"),
                pairingAdvice = "Phối cùng quần âu ống suông màu kem và giày loafer tạo look giao thoa rất thanh lịch; phối cùng áo cử nhân trong ngày ra trường."
            ),
            shortStory = "Áo ngũ thân có cấu trúc năm thân, năm cúc cài; nguồn gốc chính xác gắn với cải cách trang phục năm 1744 ở Đàng Trong."
        ),
        GarmentItem(
            id = "ao_ngu_than_tay_thung",
            name = "Áo Ngũ Thân Tay Thụng (Áo Tấc)",
            layerType = GarmentLayerType.MAIN_GARMENT,
            category = "ao_ngu_than",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Triều Nguyễn (Thế kỷ 19 - đầu thế kỷ 20)",
                culturalSpace = "Cung đình & Lễ tiết dân gian các vùng",
                structuralFeatures = listOf(
                    "Cấu trúc 5 thân trang trọng với tà áo buông rộng",
                    "Ống tay may thụng dài thụng rộng quét qua đầu ngón tay",
                    "Khi chắp tay tạo dáng chữ V tôn nghiêm, lễ độ"
                ),
                historicalContext = "Lễ phục quan trọng trong các dịp cúng tế, cưới hỏi, lễ tốt nghiệp hàn lâm xưa.",
                confidenceNote = "Tài liệu bảo tàng và hiện vật cung đình lưu giữ nhiều mẫu nguyên bản.",
                citations = listOf("Cục Di sản Văn hóa", "Bảo tàng Cổ vật Cung đình Huế")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[1], colorPalettes[2], colorPalettes[3]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Trang trọng", "Hoàng gia", "Cưới hỏi", "Lễ hội"),
                pairingAdvice = "Khuyên dùng trong bối cảnh trang trọng. Không nên mix với quần jeans bó sát để tránh phá vỡ dáng thụng tôn nghiêm."
            ),
            shortStory = "Áo tấc là lễ phục tay rộng trang trọng, biểu trưng cho sự khiêm cung và tôn ti trật tự văn hóa."
        ),
        GarmentItem(
            id = "ao_dai_truyen_thong",
            name = "Áo Dài Truyền Thống",
            layerType = GarmentLayerType.MAIN_GARMENT,
            category = "ao_dai",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Phát triển từ áo ngũ thân từ thập niên 1930 đến nay",
                culturalSpace = "Toàn quốc (Biểu tượng quốc phục Việt Nam)",
                structuralFeatures = listOf(
                    "Hai tà áo trước - sau dài thướt tha chạm mắt cá",
                    "Cổ áo dựng cao kín đáo, eo thắt nhẹ nhàng duyên dáng",
                    "Xẻ tà hai bên hông tạo dáng đi uyển chuyển bay bổng"
                ),
                historicalContext = "Biểu tượng thời trang dân tộc trường tồn, xuất hiện trong Tết, đám cưới, giảng đường và ngoại giao.",
                confidenceNote = "Tư liệu lịch sử và hình ảnh phong phú, phản ánh quá trình hiện đại hóa trang phục Việt.",
                citations = listOf("Cục Du lịch Quốc gia Việt Nam", "Bảo tàng Phụ nữ Việt Nam")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[1], colorPalettes[5], colorPalettes[6]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Duyên dáng", "Quốc phục", "Tết", "Chụp ảnh"),
                pairingAdvice = "Dễ phối với quần lụa rộng trắng/đen nguyên bản hoặc thử nghiệm cùng sneaker năng động."
            ),
            shortStory = "Áo dài đã qua nhiều thập kỷ biến đổi cấu trúc từ áo ngũ thân, trở thành trang phục đại diện tâm hồn Việt."
        ),
        GarmentItem(
            id = "ao_dai_cach_tan",
            name = "Áo Dài Cách Tân Phom Suông",
            layerType = GarmentLayerType.MAIN_GARMENT,
            category = "ao_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Thiết kế đương đại thế kỷ 21 (Phong trào Retro & Gen Z)",
                culturalSpace = "Đời sống đô thị đương đại (Hà Nội, Sài Gòn, Đà Nẵng)",
                structuralFeatures = listOf(
                    "Cổ áo cách điệu (cổ tròn, cổ sen thấp hoặc không cổ)",
                    "Phom dáng suông rộng phóng khoáng, thoải mái vận động",
                    "Tà áo ngắn ngang bắp chân tạo sự năng động"
                ),
                historicalContext = "Sản phẩm của tư duy ứng dụng thời trang đương đại, giúp giới trẻ diện Việt phục mỗi ngày.",
                confidenceNote = "Đây là trang phục cách tân đương đại, không phải cổ phục lịch sử.",
                citations = listOf("Báo Tiền Phong", "Tạp chí Đẹp")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[0], colorPalettes[3], colorPalettes[6]),
                compatibleModes = listOf(CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Gen Z", "Streetwear", "Trẻ trung", "Dạo phố"),
                pairingAdvice = "Cực kỳ ăn ý khi đi cùng sneaker trắng và túi tote vải canvas."
            ),
            shortStory = "Áo dài phom suông là bước tiến đương đại giúp di sản thời trang thở nhịp cùng đời sống trẻ."
        ),
        GarmentItem(
            id = "ao_tu_than",
            name = "Áo Tứ Thân Bắc Bộ",
            layerType = GarmentLayerType.MAIN_GARMENT,
            category = "ao_tu_than",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Truyền thống dân gian Bắc Bộ (Nhiều thế kỷ lưu truyền)",
                culturalSpace = "Đồng bằng sông Hồng, Kinh Bắc, làng quê Bắc Bộ",
                structuralFeatures = listOf(
                    "Bốn thân áo (hai thân sau may liền sống, hai thân trước buông dải thắt)",
                    "Không cài khuy mà buộc chéo hai vạt trước qua bụng",
                    "Phối cùng yếm đào bên trong và thắt lưng vải dải lụa mềm"
                ),
                historicalContext = "Trang phục lao động, trẩy hội, ca quan họ và nghi lễ dân gian của người phụ nữ miền Bắc.",
                confidenceNote = "Không gian văn hóa và nhận diện rất rõ, dù mốc thời gian ra đời chính xác chưa có văn bản tuyệt đối.",
                citations = listOf("Cục Di sản Văn hóa", "Viện Văn hóa Nghệ thuật Quốc gia")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[0], colorPalettes[2], colorPalettes[6]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Kinh Bắc", "Lễ hội", "Dân gian", "Nghệ thuật"),
                pairingAdvice = "Phối cùng nón quai thao hoặc khăn mỏ quạ và guốc mộc để giữ trọn vẻ đẹp quan họ."
            ),
            shortStory = "Áo tứ thân gắn liền với hình ảnh người con gái Kinh Bắc duyên dáng bên câu quan họ ngày hội."
        )
    )

    // ==========================================
    // DATASET: INNER / OUTER LAYERS
    // ==========================================
    val innerGarments = listOf(
        GarmentItem(
            id = "ao_cu_nhan",
            name = "Áo Cử Nhân Tốt Nghiệp",
            layerType = GarmentLayerType.INNER_GARMENT,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Trang phục học thuật phương Tây du nhập thế kỷ 20-21",
                culturalSpace = "Các trường đại học & học viện đương đại",
                structuralFeatures = listOf("Dáng áo choàng thụng mở ngực", "Vạt dài có nếp gấp học vị"),
                historicalContext = "Được sinh viên Gen Z sáng tạo mặc ngoài áo ngũ thân hoặc áo dài trong ngày nhận bằng.",
                confidenceNote = "Yếu tố hiện đại trong trào lưu remix thanh xuân.",
                citations = listOf("Báo Thanh Niên 2026")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[4], colorPalettes[7]),
                compatibleModes = listOf(CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Graduation", "Học vị", "Gen Z"),
                pairingAdvice = "Mở khóa vạt trước để lộ hoa văn và khuy cài của áo ngũ thân bên trong."
            ),
            shortStory = "Sự kết hợp giữa tri thức hiện đại và di sản tổ tiên trong ngày lễ ra trường."
        ),
        GarmentItem(
            id = "yem_dao",
            name = "Yếm Đào Lụa Tơ Tằm",
            layerType = GarmentLayerType.INNER_GARMENT,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Cổ truyền Việt Nam (Thời Lê - Nguyễn)",
                culturalSpace = "Bắc Bộ & Trung Bộ",
                structuralFeatures = listOf("Tấm vải che ngực hình vuông hoặc quả trám", "Dây buộc quanh cổ và sau lưng"),
                historicalContext = "Nội y truyền thống của phụ nữ Việt, phối chuẩn mực dưới áo tứ thân hoặc áo cánh.",
                confidenceNote = "Hiện vật phong phú tại các viện bảo tàng quốc gia.",
                citations = listOf("Bảo tàng Lịch sử Quốc gia")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[6], colorPalettes[1]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Kinh điển", "Truyền thống"),
                pairingAdvice = "Tuyệt đối không thể thiếu khi mặc cùng Áo Tứ Thân."
            ),
            shortStory = "Chiếc yếm đào là nét duyên ngầm e ấp của người phụ nữ Việt xưa."
        ),
        GarmentItem(
            id = "ao_lot_trang",
            name = "Áo Lót Trắng Cổ Đứng",
            layerType = GarmentLayerType.INNER_GARMENT,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Thời Nguyễn",
                culturalSpace = "Toàn quốc",
                structuralFeatures = listOf("Vải lụa mỏng trắng nhẹ", "Cổ đứng thấp lót bên trong bảo vệ áo chính"),
                historicalContext = "Giữ vệ sinh cho áo ngũ thân đắt giá bên ngoài và tạo viền cổ trắng lịch sự.",
                confidenceNote = "Quy cách chuẩn mực khi mặc áo ngũ thân cung đình.",
                citations = listOf("Trung tâm Bảo tồn Di tích Cố đô Huế")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[5]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Thanh lịch", "Cổ chuẩn"),
                pairingAdvice = "Để lộ nhẹ viền trắng nơi cổ áo tạo vẻ sáng sủa, trang nhã."
            ),
            shortStory = "Lớp áo lót trắng bên trong là chi tiết tinh tế giúp tôn lên màu sắc của tà áo chính."
        )
    )

    // ==========================================
    // DATASET: BOTTOMS
    // ==========================================
    val bottoms = listOf(
        GarmentItem(
            id = "quan_lua_rong",
            name = "Quần Lụa Trắng Ống Rộng",
            layerType = GarmentLayerType.BOTTOM,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Thời Nguyễn đến hiện đại",
                culturalSpace = "Toàn quốc",
                structuralFeatures = listOf("Ống quần may rộng rãi từ 28-35cm", "Chất liệu lụa mượt mà rủ sóng tự nhiên"),
                historicalContext = "Quy cách chuẩn mực đi cùng áo dài và áo ngũ thân của cả nam lẫn nữ.",
                confidenceNote = "Tư liệu đồng nhất qua các thời kỳ.",
                citations = listOf("Cục Di sản Văn hóa")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[5], colorPalettes[7]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Thanh thoát", "Nguyên bản"),
                pairingAdvice = "Lựa chọn an toàn tuyệt đối cho mọi set đồ Nét Gốc."
            ),
            shortStory = "Quần lụa ống rộng mang lại dáng đi khoan thai, uyển chuyển đặc trưng người Việt."
        ),
        GarmentItem(
            id = "quan_au",
            name = "Quần Âu Suông Kem / Beige",
            layerType = GarmentLayerType.BOTTOM,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Âu phục đương đại",
                culturalSpace = "Thời trang quốc tế du nhập",
                structuralFeatures = listOf("Phom suông xếp ly nhẹ thẳng đứng", "Chất liệu kaki hoặc dạ mỏng đứng dáng"),
                historicalContext = "Được người trẻ kết hợp cùng áo ngũ thân để đi làm hoặc dự sự kiện hiện đại.",
                confidenceNote = "Yếu tố cách tân đương đại.",
                citations = listOf("Báo Văn Hóa 2026")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[5], colorPalettes[7]),
                compatibleModes = listOf(CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Smart Casual", "Hiện đại", "Thanh lịch"),
                pairingAdvice = "Giúp tôn dáng và bước đi dứt khoát trong môi trường đô thị."
            ),
            shortStory = "Chiếc quần âu đứng dáng kết hợp áo ngũ thân là công thức giao thoa ưa thích của giới trẻ."
        ),
        GarmentItem(
            id = "vay_xep_ly",
            name = "Váy Lụa Dài Xếp Ly",
            layerType = GarmentLayerType.BOTTOM,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Cổ truyền Bắc Bộ",
                culturalSpace = "Đồng bằng sông Hồng",
                structuralFeatures = listOf("Váy đụp lụa đen buông rủ kín đáo", "Dễ dàng thắt dải thắt lưng áo tứ thân"),
                historicalContext = "Phần dưới truyền thống của phụ nữ thôn quê và liền chị quan họ Kinh Bắc.",
                confidenceNote = "Hiện vật phong phú tại bảo tàng.",
                citations = listOf("Viện Dân tộc học")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[7], colorPalettes[0]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Kinh Bắc", "Dân gian"),
                pairingAdvice = "Mặc đồng bộ cùng áo tứ thân và thắt lưng lụa đào."
            ),
            shortStory = "Chiếc váy đen buông rủ làm nền tôn lên dải yếm thắm và tà áo tứ thân rực rỡ."
        ),
        GarmentItem(
            id = "quan_jeans",
            name = "Quần Jeans Ống Suông",
            layerType = GarmentLayerType.BOTTOM,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Denim hiện đại thế kỷ 20-21",
                culturalSpace = "Toàn cầu",
                structuralFeatures = listOf("Vải bò dệt chéo cá tính", "Phom suông rộng thoải mái"),
                historicalContext = "Biểu tượng thời trang đường phố Gen Z.",
                confidenceNote = "Tuyệt đối không thuộc lịch sử cổ phục.",
                citations = listOf("Gen Z Fashion Survey 2026")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[4]),
                compatibleModes = listOf(CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Streetwear", "Bụi bặm", "Phá cách"),
                pairingAdvice = "Chỉ dùng trong chế độ Remix Gen Z; không quảng bá là cổ phục truyền thống."
            ),
            shortStory = "Jeans mang đến diện mạo đường phố tự do và nổi loạn khi kết hợp cùng áo cách tân."
        )
    )

    // ==========================================
    // DATASET: FOOTWEAR
    // ==========================================
    val footwear = listOf(
        GarmentItem(
            id = "guoc_moc",
            name = "Guốc Mộc Cổ Truyền",
            layerType = GarmentLayerType.FOOTWEAR,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Cổ truyền Việt Nam hàng trăm năm",
                culturalSpace = "Cả ba miền Bắc - Trung - Nam",
                structuralFeatures = listOf("Thân guốc đẽo từ gỗ mộc tự nhiên", "Quai vải nhung hoặc da mềm êm ái"),
                historicalContext = "Phụ kiện bước đi tạo tiếng lách cách thanh tao quen thuộc trong ngõ phố xưa.",
                confidenceNote = "Hiện vật văn hóa dân gian tiêu biểu.",
                citations = listOf("Bảo tàng Dân tộc học Việt Nam")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[2], colorPalettes[7]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Hoài cổ", "Mộc mạc"),
                pairingAdvice = "Tạo nên dáng đứng thanh nhã chuẩn phong cách xưa."
            ),
            shortStory = "Tiếng guốc mộc gõ trên thềm gạch là âm thanh mang đậm ký ức làng quê Việt."
        ),
        GarmentItem(
            id = "hai_theu",
            name = "Hài Vải Thêu Sen",
            layerType = GarmentLayerType.FOOTWEAR,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Thời Lê - Nguyễn",
                culturalSpace = "Cung đình & tầng lớp quý tộc xưa",
                structuralFeatures = listOf("Mũi hài cong nhẹ thanh thoát", "Thêu chỉ tơ họa tiết hoa sen và mây nước"),
                historicalContext = "Giày lễ nghi cao quý dùng trong các đại lễ và hôn lễ truyền thống.",
                confidenceNote = "Tài liệu bảo tàng ghi chép chi tiết các kiểu hài triều Nguyễn.",
                citations = listOf("Bảo tàng Lịch sử Quốc gia")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[1], colorPalettes[2]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Quý phái", "Lễ nghi"),
                pairingAdvice = "Rất đẹp khi đi cùng Áo Tấc hoặc Áo Ngũ Thân lễ tiết."
            ),
            shortStory = "Đôi hài cong thêu sen nâng niu từng bước chân đài các của thiếu nữ Việt xưa."
        ),
        GarmentItem(
            id = "loafer",
            name = "Giày Loafer Da Thanh Lịch",
            layerType = GarmentLayerType.FOOTWEAR,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Âu phục thế kỷ 20",
                culturalSpace = "Thời trang quốc tế",
                structuralFeatures = listOf("Chất liệu da bóng nhẹ hoặc da lộn", "Dáng giày lười tiện dụng không dây buộc"),
                historicalContext = "Được thanh niên hiện đại chuộng phối với ngũ thân nam và áo dài cách tân.",
                confidenceNote = "Yếu tố phụ kiện hiện đại.",
                citations = listOf("Báo Văn Hóa 2026")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[7], colorPalettes[2]),
                compatibleModes = listOf(CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Vintage", "Smart Casual", "Minimal"),
                pairingAdvice = "Tạo cảm giác cổ điển phương Tây hòa quyện cùng nét xưa phương Đông."
            ),
            shortStory = "Loafer da kết hợp áo ngũ thân là nét giao thoa tinh tế giữa hoài cổ và hiện đại."
        ),
        GarmentItem(
            id = "sneaker",
            name = "Sneaker Trắng Tối Giản",
            layerType = GarmentLayerType.FOOTWEAR,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Thời trang thể thao đường phố thế kỷ 20-21",
                culturalSpace = "Toàn cầu",
                structuralFeatures = listOf("Đế cao su êm ái linh hoạt", "Kiểu dáng chunky hoặc basic trẻ trung"),
                historicalContext = "Biểu tượng năng động của giới trẻ Gen Z trong các buổi chụp ảnh kỷ yếu.",
                confidenceNote = "Yếu tố hiện đại trong trào lưu remix thời trang.",
                citations = listOf("Báo Thanh Niên 2026")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[5]),
                compatibleModes = listOf(CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Streetwear", "Gen Z", "Năng động"),
                pairingAdvice = "Chỉ dùng cho phong cách Remix Gen Z, chú thích rõ là phụ kiện hiện đại."
            ),
            shortStory = "Đôi sneaker trắng giúp người trẻ bước đi nhanh nhẹn, tự tin mang Việt phục vào đời sống."
        )
    )

    // ==========================================
    // DATASET: HEADWEAR
    // ==========================================
    val headwear = listOf(
        GarmentItem(
            id = "khan_van",
            name = "Khăn Vấn / Khăn Đóng Truyền Thống",
            layerType = GarmentLayerType.HEADWEAR,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Thời Nguyễn (Thế kỷ 19)",
                culturalSpace = "Huế và cả nước",
                structuralFeatures = listOf("Các vòng nếp vải cuốn khéo léo tạo hình chữ Nhân hoặc chữ Nhất", "Chất liệu gấm hoặc lụa đen"),
                historicalContext = "Đi liền với áo ngũ thân và áo tấc trong mọi bối cảnh lễ nghi.",
                confidenceNote = "Chuẩn mực trang phục cung đình và dân gian triều Nguyễn.",
                citations = listOf("TTXVN / VietnamPlus", "Bảo tàng Cổ vật Cung đình Huế")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[7], colorPalettes[4]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Chuẩn mực", "Nét Gốc"),
                pairingAdvice = "Đội cân đối, nếp chữ Nhân chính giữa trán."
            ),
            shortStory = "Khăn vấn tạo nên diện mạo đoan chính, đĩnh đạc của người mang tinh thần cổ phong."
        ),
        GarmentItem(
            id = "non_quai_thao",
            name = "Nón Quai Thao (Nón Ba Tầm)",
            layerType = GarmentLayerType.HEADWEAR,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Cổ truyền Bắc Bộ",
                culturalSpace = "Vùng Kinh Bắc, đồng bằng sông Hồng",
                structuralFeatures = listOf("Vành nón tròn phẳng rộng như tấm khiên che nắng mưa", "Dây quai thao tết bằng tơ tằm buông dài"),
                historicalContext = "Phụ kiện bất ly thân của các liền chị quan họ trong ngày hội làng.",
                confidenceNote = "Biểu tượng văn hóa phi vật thể của Bắc Bộ.",
                citations = listOf("Cục Di sản Văn hóa")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[2], colorPalettes[5]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Kinh Bắc", "Lễ hội"),
                pairingAdvice = "Chỉ nên phối cùng Áo Tứ Thân hoặc áo mớ ba mớ bảy để giữ chuẩn không gian văn hóa."
            ),
            shortStory = "Chiếc nón quai thao nghiêng che nụ cười duyên trong câu hát mời trầu đằm thắm."
        ),
        GarmentItem(
            id = "khan_mo_qua",
            name = "Khăn Mỏ Quạ Bắc Bộ",
            layerType = GarmentLayerType.HEADWEAR,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Cổ truyền phụ nữ miền Bắc",
                culturalSpace = "Làng quê Bắc Bộ",
                structuralFeatures = listOf("Gấp khăn vuông thành hình tam giác", "Buộc ôm gọn đầu với chóp nhọn trước trán như mỏ quạ"),
                historicalContext = "Tôn lên khuôn mặt trái xoan thanh tú của người phụ nữ nông thôn Bắc Bộ.",
                confidenceNote = "Tư liệu dân tộc học ghi nhận rõ nét.",
                citations = listOf("Viện Văn hóa Dân gian")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[7]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Dân gian", "Mộc mạc"),
                pairingAdvice = "Rất ăn ý khi kết hợp cùng áo tứ thân và răng nhánh hạt huyền."
            ),
            shortStory = "Khăn mỏ quạ đen tôn lên nước da trắng và nét cười thắt đáy lưng ong."
        ),
        GarmentItem(
            id = "beret_gen_z",
            name = "Mũ Nồi Beret Cổ Điển",
            layerType = GarmentLayerType.HEADWEAR,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Thế kỷ 20 du nhập",
                culturalSpace = "Đô thị hiện đại",
                structuralFeatures = listOf("Dáng mũ tròn mềm đội nghiêng", "Chất liệu dạ len vintage"),
                historicalContext = "Được Gen Z ứng dụng phối phong cách retro cùng áo dài và áo ngũ thân.",
                confidenceNote = "Phụ kiện hiện đại.",
                citations = listOf("Gen Z Lookbook 2026")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[7], colorPalettes[0]),
                compatibleModes = listOf(CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Retro", "Vintage", "Nghệ sĩ"),
                pairingAdvice = "Đội lệch 45 độ tạo vẻ đẹp lãng mạn như tranh vẽ thời kỳ Đông Dương."
            ),
            shortStory = "Chiếc beret mang phong vị hội họa Đông Dương đầu thế kỷ 20."
        )
    )

    // ==========================================
    // DATASET: ACCESSORIES & BAGS
    // ==========================================
    val accessories = listOf(
        GarmentItem(
            id = "the_bai",
            name = "Thẻ Bài Gỗ Trầm Cổ Phong",
            layerType = GarmentLayerType.ACCESSORY,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Triều Nguyễn",
                culturalSpace = "Cung đình & quan lại thời xưa",
                structuralFeatures = listOf("Thẻ bài khắc chữ nho đeo trước ngực bằng tua rua đỏ", "Khắc chữ chúc phúc bình an"),
                historicalContext = "Từng là biểu trưng phẩm hàm, ngày nay là phụ kiện trang sức cổ phong thanh tao.",
                confidenceNote = "Hiện vật phong phú tại bảo tàng cổ vật cung đình.",
                citations = listOf("Bảo tàng Cổ vật Cung đình Huế")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[2], colorPalettes[1]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Cổ phong", "Sang trọng"),
                pairingAdvice = "Đeo lệch ngực phải cùng áo ngũ thân để làm điểm nhấn thị giác tinh tế."
            ),
            shortStory = "Chiếc thẻ bài gỗ trầm thơm nhắc nhớ về phong thái nho nhã của tiền nhân."
        ),
        GarmentItem(
            id = "quat_lua",
            name = "Quạt Xếp Lụa Thêu Sen",
            layerType = GarmentLayerType.ACCESSORY,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Nhiều thế kỷ lưu truyền",
                culturalSpace = "Làng nghề Chàng Sơn, Hà Nội và cả nước",
                structuralFeatures = listOf("Nan tre uốn cong bọc lụa tơ tằm thêu tay", "Tua rua chỉ đỏ buông rủ"),
                historicalContext = "Vật tùy thân của các văn nhân, thiếu nữ khi dạo cảnh và trò chuyện.",
                confidenceNote = "Nghề thủ công truyền thống tiêu biểu.",
                citations = listOf("Cục Di sản Văn hóa")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[5], colorPalettes[6]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Thanh tao", "Chụp ảnh"),
                pairingAdvice = "Cầm tay tạo dáng chụp ảnh hoặc quạt nhẹ duyên dáng."
            ),
            shortStory = "Quạt lụa khẽ khàng mở ra mang theo làn gió thoang thoảng hương sen mùa hạ."
        ),
        GarmentItem(
            id = "tui_mini_deo_cheo",
            name = "Túi Mini Da Đeo Chéo",
            layerType = GarmentLayerType.ACCESSORY,
            category = "hien_dai",
            isHistoricalPiece = false,
            historyLayer = HistoryLayer(
                originEra = "Thời trang đương đại thế kỷ 21",
                culturalSpace = "Đô thị toàn cầu",
                structuralFeatures = listOf("Kích thước nhỏ gọn đựng điện thoại", "Dây xích hoặc dây da thanh mảnh"),
                historicalContext = "Phụ kiện tiện ích của người trẻ khi dạo phố cuối tuần.",
                confidenceNote = "Yếu tố hiện đại.",
                citations = listOf("Gen Z Fashion Report")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[7], colorPalettes[2]),
                compatibleModes = listOf(CulturalMode.FUSION, CulturalMode.REMIX_GEN_Z),
                styleTags = listOf("Minimal", "Gen Z", "Tiện ích"),
                pairingAdvice = "Đeo chéo ngực tạo sự tương phản hiện đại trên nền áo dài truyền thống."
            ),
            shortStory = "Chiếc túi nhỏ nhắn tiện lợi giúp người trẻ tự do bước ra phố cùng trang phục truyền thống."
        ),
        GarmentItem(
            id = "chuoi_ngoc_trai",
            name = "Chuỗi Hạt Ngọc Trai",
            layerType = GarmentLayerType.ACCESSORY,
            category = "truyen_thong",
            isHistoricalPiece = true,
            historyLayer = HistoryLayer(
                originEra = "Thời phong kiến đến thời kỳ Đông Dương",
                culturalSpace = "Tầng lớp thượng lưu Hà Nội & Huế",
                structuralFeatures = listOf("Các hạt ngọc trai thiên nhiên xâu chuỗi", "Ánh xà cừ óng ánh sang quý"),
                historicalContext = "Điểm xuyết quý phái trên nền áo dài lụa nhung màu đậm.",
                confidenceNote = "Hình ảnh tư liệu phụ nữ Hà Nội xưa.",
                citations = listOf("Bảo tàng Phụ nữ Việt Nam")
            ),
            styleLayer = StyleLayer(
                recommendedColors = listOf(colorPalettes[5]),
                compatibleModes = listOf(CulturalMode.HERITAGE, CulturalMode.FUSION),
                styleTags = listOf("Quý phái", "Vintage"),
                pairingAdvice = "Rất đẹp khi đeo cùng áo dài màu đỏ son hoặc xanh chàm cổ."
            ),
            shortStory = "Chuỗi hạt ngọc trai trắng ngần tôn vinh nét đài các, kín đáo của người phụ nữ Việt."
        )
    )

    // ==========================================
    // DATASET: HERITAGE KNOWLEDGE ARTICLES
    // (Follows pages 4, 19, 20, 21, 28 of PDF)
    // ==========================================
    val heritageArticles = listOf(
        HeritageArticle(
            id = "ao_ngu_than_1744",
            title = "Áo Ngũ Thân: Cấu trúc 5 thân & Cải cách Đàng Trong",
            category = "ao_ngu_than",
            summary60s = "Áo ngũ thân có 5 thân (4 thân ngoài tượng trưng tứ thân phụ mẫu, 1 thân con bên trong) và 5 khuy cài tượng trưng ngũ thường Nho giáo. Gắn liền mốc 1744 thời chúa Nguyễn Phúc Khoát.",
            fullContent = """
                Áo ngũ thân là một trong những di sản trang phục tiêu biểu nhất của người Việt. 
                
                Về cấu tạo vật chất:
                - Áo gồm 5 thân (2 thân trước, 2 thân sau và 1 thân con ẩn kín bên trong ngực phải).
                - 5 khuy cài ở cổ áo và mạn sườn phải, thường làm bằng đồng, ngọc hoặc gỗ quý.
                - Cổ đứng dựng vuông vức kín đáo, thể hiện đạo làm người ngay thẳng, liêm khiết.
                - Hai dạng ống tay chính: Tay Chẽn (gọn gàng, thuận tiện lao động và sinh hoạt) và Tay Thụng (Áo Tấc - dùng cho lễ nghi, học vị trang trọng).
                
                Về bối cảnh lịch sử và niên đại:
                Một quan điểm học thuật phổ biến cho rằng áo ngũ thân được định hình trong cuộc cải cách trang phục ở Đàng Trong năm 1744 dưới thời chúa Võ Vương Nguyễn Phúc Khoát, nhằm xác lập bản sắc phục sức riêng biệt. Sau đó, vua Minh Mạng đã chuẩn hóa và ban bố quy định y phục này trên toàn quốc vào thế kỷ 19.
            """.trimIndent(),
            taxonomy = KnowledgeTaxonomy.RECORDED,
            confidenceLevel = "Trung bình - Cao",
            debateNotes = "Nguồn gốc chính xác và quá trình hình thành vẫn có những góc nhìn khác nhau giữa các nhà nghiên cứu về các tiền đề trang phục trước năm 1744.",
            sourceName = "TTXVN / VietnamPlus (Độc đáo áo ngũ thân - Bản sắc văn hoá Việt)",
            sourceUrl = "https://mega.vietnamplus.vn/doc-dao-ao-ngu-than-ban-sac-van-hoa-viet-5419.html",
            publicationYear = "2024 - 2026"
        ),
        HeritageArticle(
            id = "y_nghia_bieu_tuong_ngu_than",
            title = "Diễn giải Biểu tượng: Tứ Thân Phụ Mẫu & Ngũ Thường",
            category = "ao_ngu_than",
            summary60s = "Bốn thân ngoài tượng trưng cho cha mẹ đẻ và cha mẹ chồng/vợ; thân thứ năm là bản thân người mặc. Năm khuy cài biểu trưng cho Nhân, Lễ, Nghĩa, Trí, Tín.",
            fullContent = """
                Trong văn hóa Việt, các nhà nghiên cứu và nghệ nhân dân gian thường đưa ra những diễn giải biểu tượng sâu sắc về cấu trúc áo ngũ thân:
                
                - Bốn thân áo bên ngoài tượng trưng cho 'Tứ thân phụ mẫu': phụ thân, mẫu thân của mình và cha mẹ bên vợ/chồng, thể hiện tấm lòng hiếu đạo đối với bậc sinh thành.
                - Thân thứ năm nhỏ hơn nằm bên trong tượng trưng cho chính người mặc, được che chở trong tình yêu thương gia đình.
                - Năm chiếc khuy cài tượng trưng cho ngũ thường trong triết lý đạo đức: Nhân (lòng nhân ái), Lễ (sự tôn ti lễ phép), Nghĩa (sự ngay thẳng chính nghĩa), Trí (trí tuệ hiểu biết), Tín (sự thành thực giữ lời).
                
                Lưu ý học thuật: Đây là cách diễn giải mang tính biểu tượng văn hóa để răn dạy đạo đức, không nên coi là nguyên nhân kỹ thuật lịch sử duy nhất của cấu trúc cắt may.
            """.trimIndent(),
            taxonomy = KnowledgeTaxonomy.SYMBOLIC_INTERPRETATION,
            confidenceLevel = "Diễn giải văn hóa",
            debateNotes = "Mang tính lý giải triết lý và đạo đức truyền thống, được nhiều nghệ nhân truyền miệng ghi nhận.",
            sourceName = "Báo Văn Hóa & Nghiên cứu Cố đô Huế",
            sourceUrl = "https://baovanhoa.vn/van-hoa/bai-2-cach-tan-cho-ao-ngu-than-113850.html",
            publicationYear = "2025"
        ),
        HeritageArticle(
            id = "ao_dai_tien_trinh",
            title = "Tiến trình Áo Dài: Từ Ngũ Thân đến Hiện Đại",
            category = "ao_dai",
            summary60s = "Áo dài hiện đại là kết quả của quá trình giao thoa và cải tiến từ áo ngũ thân truyền thống, qua các cuộc cải cách của họa sĩ Cát Tường (Lemur) và Lê Phổ những năm 1930.",
            fullContent = """
                Trang thông tin chính thức của Cục Du lịch Quốc gia Việt Nam mô tả áo dài hiện nay xuất hiện trong Tết, lễ cưới, chụp ảnh và nhiều dịp trang trọng.
                
                Nguồn gốc của áo dài có sợi dây liên kết mật thiết với áo ngũ thân:
                - Vào thập niên 1930, các họa sĩ nhóm Tự Lực Văn Đoàn như Cát Tường (Lemur) và Lê Phổ đã cách tân áo ngũ thân: giảm từ 5 thân xuống còn 2 tà trước sau, nhấn eo, tạo cổ thuyền hoặc cổ sen, đưa áo dài trở thành biểu tượng thời trang thanh tân của người phụ nữ Việt Nam.
                - Ngày nay, các nhà thiết kế trẻ tiếp tục thay đổi cổ áo, tay áo, độ dài và cấu trúc vải để tạo ra các phiên bản thực dụng hơn trong đời sống hiện đại mà không làm mất đi hồn cốt dân tộc.
            """.trimIndent(),
            taxonomy = KnowledgeTaxonomy.RECORDED,
            confidenceLevel = "Rất cao",
            debateNotes = null,
            sourceName = "Cục Du lịch Quốc gia Việt Nam (Vietnam Tourism)",
            sourceUrl = "https://vietnam.travel/things-to-do/ao-dai-vietnam",
            publicationYear = "2024"
        ),
        HeritageArticle(
            id = "ao_tu_than_kinh_bac",
            title = "Áo Tứ Thân & Không gian Văn hóa Bắc Bộ",
            category = "ao_tu_than",
            summary60s = "Không gian văn hóa: Bắc Bộ. Nhận diện: Áo ngoài nhiều tà xẻ, phối yếm đào, thắt lưng lụa, khăn mỏ quạ hoặc nón quai thao. Thường gặp trong hội làng, biểu diễn dân ca quan họ.",
            fullContent = """
                Áo tứ thân thường được nhận diện là trang phục phụ nữ miền Bắc; các nguồn phổ thông ghi nhận nó từng được sử dụng phổ biến trong đời sống thường ngày và ngày nay xuất hiện chủ yếu trong lễ hội, trình diễn hoặc tái hiện văn hóa.
                
                Đối với áo tứ thân, ứng dụng tập trung vào nhận diện bộ trang phục và không gian văn hóa, thay vì cố khẳng định một mốc 'ra đời năm nào', vì các nguồn dân gian chưa có một văn bản xác lập niên đại đơn nhất.
                
                Đặc điểm nhận diện tiêu biểu:
                - Áo dài qua gối, bốn thân ghép lại, hai thân trước xẻ để buộc vạt trước bụng.
                - Mặc kèm yếm đào bên trong, thắt lưng hoa lý hoặc hoa đào.
                - Đi cùng nón ba tầm (nón quai thao) và khăn mỏ quạ đặc trưng.
            """.trimIndent(),
            taxonomy = KnowledgeTaxonomy.RECORDED,
            confidenceLevel = "Tổng quan",
            debateNotes = "Nguồn gốc niên đại dân gian lưu truyền từ nhiều thế kỷ, không có một sắc lệnh duy nhất khai sinh.",
            sourceName = "Cục Di sản Văn hóa & Viện Văn hóa Nghệ thuật",
            sourceUrl = "https://congdulieu.dsvh.gov.vn/",
            publicationYear = "2025"
        ),
        HeritageArticle(
            id = "triet_ly_remix",
            title = "Triết lý 'Remix phong cách, không remix lịch sử'",
            category = "triet_ly_remix",
            summary60s = "Sáng tạo được hoan nghênh nhưng phải minh bạch: Yếu tố hiện đại phải được dán nhãn rõ ràng. Giới trẻ có thể tự do phối sneaker, blazer nhưng không được ngộ nhận là phục dựng cổ phong.",
            fullContent = """
                Việt Phục Remix được xây dựng dựa trên nguyên tắc đạo đức của UNESCO đối với di sản văn hóa phi vật thể: tôn trọng bản sắc, sự đa dạng văn hóa và sự tham gia chủ động của thanh niên.
                
                Nguyên tắc xuyên suốt của sản phẩm:
                1. 'Remix được phép, nhưng remix phải được gọi đúng là remix.'
                2. Phân định rõ 2 tầng dữ liệu: Tầng Lịch sử (History Layer) và Tầng Phong cách (Style Layer).
                3. Không biến mọi thứ thành 'Cổ phục'. Cần phân biệt rõ: Trang phục truyền thống, Phục dựng lịch sử, Trang phục cách tân, và Remix đương đại.
                4. Tôn trọng người dùng: Không phán xét thẩm mỹ cá nhân, mà đóng vai trò người cố vấn văn hóa thông thái và chân thành.
            """.trimIndent(),
            taxonomy = KnowledgeTaxonomy.MULTIPLE_PERSPECTIVES,
            confidenceLevel = "Định hướng chuẩn mực",
            debateNotes = "Phù hợp với hướng dẫn thực thi đạo đức UNESCO về di sản văn hóa phi vật thể.",
            sourceName = "UNESCO Ethics and Intangible Cultural Heritage",
            sourceUrl = "https://ich.unesco.org/en/ethics-and-ich-00866",
            publicationYear = "2026"
        )
    )

    // ==========================================
    // INITIAL SAMPLE OUTFITS (For Instant Inspiration)
    // ==========================================
    fun getDefaultOutfit(occasion: OccasionEvent = OccasionEvent.GRADUATION): ActiveOutfit {
        val main = garments.first { it.id == "ao_ngu_than_tay_chen" }
        val inner = innerGarments.first { it.id == "ao_cu_nhan" }
        val bottom = bottoms.first { it.id == "quan_au" }
        val foot = footwear.first { it.id == "loafer" }
        val acc = accessories.first { it.id == "tui_mini_deo_cheo" }
        return ActiveOutfit(
            id = "outfit_grad_1",
            name = "Thanh Xuân Tốt Nghiệp",
            occasion = occasion,
            mode = CulturalMode.FUSION,
            mainGarment = main,
            innerGarment = inner,
            bottom = bottom,
            footwear = foot,
            headwear = null,
            accessory = acc,
            hairstyle = null,
            primaryColor = colorPalettes[4], // Xanh navy
            secondaryColor = colorPalettes[5], // Kem
            customNotes = "Look tốt nghiệp kết hợp áo cử nhân hiện đại trên nền ngũ thân tay chẽn xanh navy."
        )
    }

    fun getHueMinimalOutfit(): ActiveOutfit {
        val main = garments.first { it.id == "ao_ngu_than_tay_chen" }
        val bottom = bottoms.first { it.id == "quan_au" }
        val foot = footwear.first { it.id == "loafer" }
        val acc = accessories.first { it.id == "the_bai" }
        return ActiveOutfit(
            id = "outfit_hue_minimal",
            name = "Huế Minimal",
            occasion = OccasionEvent.HERITAGE_TOUR,
            mode = CulturalMode.FUSION,
            mainGarment = main,
            innerGarment = null,
            bottom = bottom,
            footwear = foot,
            headwear = null,
            accessory = acc,
            hairstyle = null,
            primaryColor = colorPalettes[0], // Xanh rêu
            secondaryColor = colorPalettes[5], // Kem
            customNotes = "Áo ngũ thân xanh rêu · quần kem · loafer nâu. Phong cách Minimal di sản cố đô."
        )
    }

    fun getTetHeritageOutfit(): ActiveOutfit {
        val main = garments.first { it.id == "ao_dai_truyen_thong" }
        val bottom = bottoms.first { it.id == "quan_lua_rong" }
        val foot = footwear.first { it.id == "hai_theu" }
        val head = headwear.first { it.id == "khan_van" }
        val acc = accessories.first { it.id == "quat_lua" }
        return ActiveOutfit(
            id = "outfit_tet_heritage",
            name = "Xuân Thì Chu Sa",
            occasion = OccasionEvent.TET,
            mode = CulturalMode.HERITAGE,
            mainGarment = main,
            innerGarment = null,
            bottom = bottom,
            footwear = foot,
            headwear = head,
            accessory = acc,
            hairstyle = null,
            primaryColor = colorPalettes[1], // Đỏ son
            secondaryColor = colorPalettes[2], // Vàng trầm
            customNotes = "Áo dài truyền thống màu đỏ son cùng quần lụa trắng và quạt thêu sen đón Tết."
        )
    }
    }
}
