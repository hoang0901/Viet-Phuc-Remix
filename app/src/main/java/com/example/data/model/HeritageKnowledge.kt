package com.example.data.model

enum class KnowledgeTaxonomy(val label: String, val badgeColorHex: Long) {
    RECORDED("Đã được ghi nhận", 0xFF1B6A58),            // Jade Teal
    MULTIPLE_PERSPECTIVES("Có nhiều quan điểm", 0xFFC98A3C), // Imperial Gold
    SYMBOLIC_INTERPRETATION("Diễn giải biểu tượng", 0xFF7D5260) // Rose Brown
}

data class HeritageArticle(
    val id: String,
    val title: String,
    val category: String, // "ao_ngu_than", "ao_dai", "ao_tu_than", "phu_kien", "triet_ly_remix"
    val summary60s: String,
    val fullContent: String,
    val taxonomy: KnowledgeTaxonomy,
    val confidenceLevel: String, // "Rất cao", "Trung bình - Cao", "Đang nghiên cứu"
    val debateNotes: String? = null,
    val sourceName: String,
    val sourceUrl: String,
    val publicationYear: String
)
