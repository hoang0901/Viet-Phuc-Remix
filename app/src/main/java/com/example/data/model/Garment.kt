package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class GarmentLayerType(val displayName: String) {
    MAIN_GARMENT("Áo chính"),
    INNER_GARMENT("Trang phục bên trong / Khoác"),
    BOTTOM("Quần / Váy"),
    HEADWEAR("Khăn / Nón"),
    FOOTWEAR("Giày dép"),
    ACCESSORY("Phụ kiện & Túi"),
    HAIR("Kiểu tóc")
}

data class ColorOption(
    val id: String,
    val name: String,
    val hexCode: Long,
    val isHeritageTone: Boolean = true,
    val visualDesc: String = ""
) {
    val color: Color get() = Color(hexCode)
}

// History Layer: What is this? Provenance? Structure? Era? Region?
data class HistoryLayer(
    val originEra: String,
    val culturalSpace: String,
    val structuralFeatures: List<String>,
    val historicalContext: String,
    val confidenceNote: String,
    val citations: List<String>
)

// Style Layer: Modern colors, pairing recommendations, style tags
data class StyleLayer(
    val recommendedColors: List<ColorOption>,
    val compatibleModes: List<CulturalMode>,
    val styleTags: List<String>,
    val pairingAdvice: String
)

data class GarmentItem(
    val id: String,
    val name: String,
    val layerType: GarmentLayerType,
    val category: String, // "ao_ngu_than", "ao_dai", "ao_tu_than", "phu_kien", etc.
    val isHistoricalPiece: Boolean, // True if authentic historical piece, false if modern remix item
    val historyLayer: HistoryLayer,
    val styleLayer: StyleLayer,
    val shortStory: String
)
