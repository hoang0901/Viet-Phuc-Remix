package com.example.data.model

enum class CulturalMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val freedomLevel: String,
    val freedomFraction: Float,
    val description: String
) {
    HERITAGE(
        id = "net_goc",
        title = "Nét Gốc",
        subtitle = "Chuẩn mực truyền thống",
        freedomLevel = "Thấp",
        freedomFraction = 0.33f,
        description = "Gần với cấu trúc và phụ kiện nguyên bản, ưu tiên phom dáng và chất liệu truyền thống."
    ),
    FUSION(
        id = "giao_thoa",
        title = "Giao Thoa",
        subtitle = "Hiện đại hóa thanh lịch",
        freedomLevel = "Trung bình",
        freedomFraction = 0.66f,
        description = "Hiện đại hóa phụ kiện (loafer, quần âu phom suông, túi nhỏ) nhưng giữ nguyên vẹn cấu trúc áo chính."
    ),
    REMIX_GEN_Z(
        id = "remix_gen_z",
        title = "Remix Gen Z",
        subtitle = "Phá cách sáng tạo",
        freedomLevel = "Cao",
        freedomFraction = 1.0f,
        description = "Cho phép phối phụ kiện hiện đại mạnh mẽ (sneaker, áo cử nhân, túi chéo, màu tương phản) mà vẫn hiểu rõ ranh giới văn hóa."
    );

    companion object {
        fun fromId(id: String): CulturalMode = entries.firstOrNull { it.id == id } ?: FUSION
    }
}

enum class OccasionEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val recommendedGarments: List<String>,
    val description: String
) {
    TET(
        id = "tet",
        title = "Tết Nguyên Đán",
        subtitle = "Du xuân & Chúc Tết",
        tag = "Lễ tiết",
        recommendedGarments = listOf("ao_dai", "ao_ngu_than"),
        description = "Không khí xuân rộn ràng, ưa chuộng các gam màu tươi sáng như đỏ son, vàng hoàng yến, xanh ngọc."
    ),
    GRADUATION(
        id = "tot_nghiep",
        title = "Lễ Tốt Nghiệp",
        subtitle = "Khoảnh khắc thanh xuân",
        tag = "Đương đại",
        recommendedGarments = listOf("ao_ngu_than", "ao_dai"),
        description = "Xu hướng sinh viên phối áo ngũ thân, áo tấc kết hợp cùng áo cử nhân và giày thanh lịch."
    ),
    HERITAGE_TOUR(
        id = "di_san",
        title = "Tham Quan Di Sản",
        subtitle = "Cố đô Huế & Đình làng cổ",
        tag = "Văn hóa",
        recommendedGarments = listOf("ao_ngu_than", "ao_tu_than"),
        description = "Không gian trầm mặc, tôn nghiêm, thích hợp tông màu rêu, kem, chàm và phom dáng cổ phong."
    ),
    FESTIVAL(
        id = "le_hoi",
        title = "Lễ Hội Văn Hóa",
        subtitle = "Hội Lim & Festival di sản",
        tag = "Cộng đồng",
        recommendedGarments = listOf("ao_tu_than", "ao_ngu_than"),
        description = "Không gian sinh hoạt văn hóa dân gian Bắc Bộ và lễ hội truyền thống đa sắc màu."
    ),
    PHOTO_SHOOT(
        id = "chup_anh",
        title = "Chụp Ảnh Nghệ Thuật",
        subtitle = "Concept Lookbook cá nhân",
        tag = "Sáng tạo",
        recommendedGarments = listOf("ao_dai", "ao_ngu_than", "ao_tu_than"),
        description = "Tự do khám phá bảng màu tương phản, bố cục thị giác ấn tượng và phong cách cá nhân."
    ),
    CASUAL(
        id = "dao_pho",
        title = "Dạo Phố & Tự Chọn",
        subtitle = "Ứng dụng đời sống hiện đại",
        tag = "Streetwear",
        recommendedGarments = listOf("ao_dai", "ao_ngu_than"),
        description = "Đưa Việt phục hòa nhập tự nhiên vào nhịp sống thường nhật với sneaker và phụ kiện tối giản."
    );

    companion object {
        fun fromId(id: String): OccasionEvent = entries.firstOrNull { it.id == id } ?: TET
    }
}
