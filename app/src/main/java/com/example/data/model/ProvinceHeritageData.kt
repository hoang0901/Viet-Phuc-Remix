package com.example.data.model

import android.content.Context
import org.json.JSONObject

/**
 * Lớp dữ liệu chuẩn hóa cấp cao cho 34 tỉnh/thành phố Việt Nam,
 * được đọc từ nguồn dữ liệu tập trung `provinces_master.json`.
 *
 * Tích hợp toàn diện:
 * 1. Dữ liệu địa lý & hình học gốc (d, labelX, labelY, class).
 * 2. Phân loại vùng miền (Bắc Bộ, Trung Bộ, Nam Bộ, Tây Nguyên).
 * 3. Dữ liệu di sản văn hóa, trang phục truyền thống, đặc điểm nhận diện,
 *    ý nghĩa văn hóa, bối cảnh lịch sử, chất liệu và phụ kiện.
 * 4. Liên kết mã trang phục (costumeId) với kho đồ Việt Phục.
 */
data class MasterProvinceItem(
    val id: String,                         // Mã định danh địa phương (vd: "ha_noi", "bac_ninh")
    val svgId: String,                      // id thẻ vector SVG (vd: "svg_ha_noi")
    val name: String,                       // Tên chính thức tỉnh/thành (vd: "Hà Nội")
    val regionId: String,                   // Mã vùng miền ("bac_bo", "trung_bo", "nam_bo", "tay_nguyen")
    val regionName: String,                 // Tên vùng miền hiển thị
    val heritageTitle: String,              // Tiêu đề di sản hoặc đặc trưng địa phương
    val costumeName: String,                // Tên trang phục truyền thống tiêu biểu
    val costumeId: String?,                 // Mã loại trang phục liên kết trong ứng dụng (hoặc null)
    val identificationFeatures: String,     // Đặc điểm nhận diện trang phục
    val cultureSignificance: String,        // Ý nghĩa văn hóa
    val historicalContext: String,          // Bối cảnh lịch sử
    val materialAccessories: String,        // Chất liệu, phụ kiện và đặc điểm trang phục
    val illustrationTag: String,            // Gợi ý phong cách / tag hình ảnh minh họa
    val hasFullHeritageDetails: Boolean,    // Cờ dữ liệu di sản đầy đủ
    val mergeClass: String,                 // "province-merged" hoặc "province-unmerged"
    val isMerged: Boolean,
    val labelX: Float,
    val labelY: Float,
    val pathD: String
)

/**
 * Kho dữ liệu tập trung đọc file `provinces_master.json`.
 */
object ProvincesMasterRepository {
    private var cachedList: List<MasterProvinceItem>? = null
    private var cachedMap: Map<String, MasterProvinceItem>? = null

    /**
     * Nạp toàn bộ danh mục 34 tỉnh/thành từ `app/src/main/assets/provinces_master.json`.
     */
    fun loadProvinces(context: Context): List<MasterProvinceItem> {
        cachedList?.let { return it }

        val list = mutableListOf<MasterProvinceItem>()
        try {
            val jsonString = context.assets.open("provinces_master.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)
            val array = root.getJSONArray("provinces")

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val pid = obj.getString("id")
                val isMerged = obj.optBoolean("isMerged", false)
                val mergeClass = obj.optString("mergeClass", if (isMerged) "province-merged" else "province-unmerged")

                list.add(
                    MasterProvinceItem(
                        id = pid,
                        svgId = obj.optString("svgId", "svg_$pid"),
                        name = obj.getString("name"),
                        regionId = obj.optString("regionId", "chua_xac_dinh"),
                        regionName = obj.optString("regionName", "Đang cập nhật"),
                        heritageTitle = obj.optString("heritageTitle", "Di sản đặc trưng địa phương"),
                        costumeName = obj.optString("costumeName", "Trang phục truyền thống"),
                        costumeId = if (obj.isNull("costumeId")) null else obj.optString("costumeId").takeIf { it.isNotBlank() },
                        identificationFeatures = obj.optString("identificationFeatures", "Đang cập nhật mẫu chuẩn."),
                        cultureSignificance = obj.optString("cultureSignificance", "Đang cập nhật tư liệu di sản."),
                        historicalContext = obj.optString("historicalContext", "Đang cập nhật bối cảnh lịch sử."),
                        materialAccessories = obj.optString("materialAccessories", "Chất liệu và phụ kiện truyền thống."),
                        illustrationTag = obj.optString("illustrationTag", "default_heritage"),
                        hasFullHeritageDetails = obj.optBoolean("hasFullHeritageDetails", false),
                        mergeClass = mergeClass,
                        isMerged = isMerged,
                        labelX = obj.optDouble("labelX", 0.0).toFloat(),
                        labelY = obj.optDouble("labelY", 0.0).toFloat(),
                        pathD = obj.optString("pathD", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cachedList = list
        cachedMap = list.associateBy { it.id }
        return list
    }

    /**
     * Tra cứu một tỉnh/thành theo id hoặc tên.
     */
    fun getProvinceById(context: Context, rawId: String): MasterProvinceItem? {
        if (cachedMap == null) {
            loadProvinces(context)
        }
        val cleanId = rawId.lowercase().replace("svg_", "").trim()
        return cachedMap?.get(cleanId) ?: cachedList?.firstOrNull {
            it.name.equals(rawId, ignoreCase = true) || it.svgId.equals(rawId, ignoreCase = true)
        }
    }
}

/**
 * Lớp dữ liệu nguyên thủy từ file SVG gốc (tương thích ngược).
 */
data class VietnamProvinceData(
    val id: String,                         // Mã định danh dữ liệu data-province-id (vd: "ha_noi", "tp_hcm")
    val svgId: String,                      // id thẻ vector trong SVG (vd: "svg_ha_noi", "svg_tp_hcm")
    val name: String,                       // Tên chính thức từ thẻ <title> & nhãn bản đồ SVG
    val mergeStatus: ProvinceMergeStatus,   // Phân loại từ thuộc tính class trong SVG
    val hasOriginalVector: Boolean = true,  // Vùng có vector độc lập trong file SVG gốc
    val isInteractive: Boolean = true,      // Có thể bấm chọn tương tác trên bản đồ

    // Các trường dữ liệu NGOÀI PHẠM VI file SVG gốc:
    val populationNote: String = "Chưa có trong file SVG gốc (Cần bổ sung sau)",
    val areaNote: String = "Chưa có trong file SVG gốc (Cần bổ sung sau)",
    val historicalCultureNote: String = "Chưa có trong file SVG gốc (Cần bổ sung sau)",
    val administrativeBoundaryNote: String = "Chưa có trong file SVG gốc (Cần bổ sung sau)"
)

enum class ProvinceMergeStatus(
    val code: String,
    val title: String,
    val badgeLabel: String,
    val colorHex: Long
) {
    MERGED("province-merged", "Đã hợp nhất (Merged)", "Hợp nhất", 0xFF1E3A8A),
    UNMERGED("province-unmerged", "Chưa hợp nhất (Unmerged)", "Chưa hợp nhất", 0xFF78350F)
}

/**
 * Thực thể quần đảo / đảo chủ quyền Việt Nam trong file SVG gốc.
 */
data class VietnamIslandEntity(
    val id: String,
    val name: String,
    val governingUnit: String?,
    val isArchipelago: Boolean,
    val coordinateNote: String
)

object VietnamMapRegistry {

    /**
     * Danh mục 4 thực thể đảo và quần đảo chủ quyền được ghi nhận trong file SVG gốc.
     */
    val islandEntities = listOf(
        VietnamIslandEntity(
            id = "hoang_sa",
            name = "QUẦN ĐẢO HOÀNG SA",
            governingUnit = "TP ĐÀ NẴNG - VIỆT NAM",
            isArchipelago = true,
            coordinateNote = "Tọa độ vector translate(1466.6, 1595.2)"
        ),
        VietnamIslandEntity(
            id = "truong_sa",
            name = "QUẦN ĐẢO TRƯỜNG SA",
            governingUnit = "TỈNH KHÁNH HÒA - VIỆT NAM",
            isArchipelago = true,
            coordinateNote = "Tọa độ vector translate(1453.0, 3110.4)"
        ),
        VietnamIslandEntity(
            id = "phu_quoc",
            name = "Đảo Phú Quốc",
            governingUnit = "Tỉnh Kiên Giang (sau sáp nhập An Giang)",
            isArchipelago = false,
            coordinateNote = "Tọa độ vector translate(403.7, 2842.0)"
        ),
        VietnamIslandEntity(
            id = "con_dao",
            name = "Côn Đảo",
            governingUnit = "Bà Rịa - Vũng Tàu",
            isArchipelago = false,
            coordinateNote = "Tọa độ vector translate(753.1, 3147.4)"
        )
    )

    val provinces: Map<String, VietnamProvinceData> = listOf(
        VietnamProvinceData("an_giang", "svg_an_giang", "An Giang", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("bac_ninh", "svg_bac_ninh", "Bắc Ninh", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("ca_mau", "svg_ca_mau", "Cà Mau", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("can_tho", "svg_can_tho", "Cần Thơ", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("cao_bang", "svg_cao_bang", "Cao Bằng", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("tp_da_nang", "svg_tp_da_nang", "TP Đà Nẵng", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("dak_lak", "svg_dak_lak", "Đắk Lắk", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("dien_bien", "svg_dien_bien", "Điện Biên", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("dong_nai", "svg_dong_nai", "Đồng Nai", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("dong_thap", "svg_dong_thap", "Đồng Tháp", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("gia_lai", "svg_gia_lai", "Gia Lai", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("ha_noi", "svg_ha_noi", "Hà Nội", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("ha_tinh", "svg_ha_tinh", "Hà Tĩnh", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("hai_phong", "svg_hai_phong", "Hải Phòng", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("tp_hue", "svg_tp_hue", "TP Huế", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("hung_yen", "svg_hung_yen", "Hưng Yên", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("khanh_hoa", "svg_khanh_hoa", "Khánh Hòa", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("lai_chau", "svg_lai_chau", "Lai Châu", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("lam_dong", "svg_lam_dong", "Lâm Đồng", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("lang_son", "svg_lang_son", "Lạng Sơn", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("lao_cai", "svg_lao_cai", "Lào Cai", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("nghe_an", "svg_nghe_an", "Nghệ An", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("ninh_binh", "svg_ninh_binh", "Ninh Bình", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("phu_tho", "svg_phu_tho", "Phú Thọ", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("quang_ngai", "svg_quang_ngai", "Quảng Ngãi", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("quang_ninh", "svg_quang_ninh", "Quảng Ninh", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("quang_tri", "svg_quang_tri", "Quảng Trị", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("son_la", "svg_son_la", "Sơn La", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("tay_ninh", "svg_tay_ninh", "Tây Ninh", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("thai_nguyen", "svg_thai_nguyen", "Thái Nguyên", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("thanh_hoa", "svg_thanh_hoa", "Thanh Hóa", ProvinceMergeStatus.UNMERGED),
        VietnamProvinceData("tp_hcm", "svg_tp_hcm", "TP. Hồ Chí Minh", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("tuyen_quang", "svg_tuyen_quang", "Tuyên Quang", ProvinceMergeStatus.MERGED),
        VietnamProvinceData("vinh_long", "svg_vinh_long", "Vĩnh Long", ProvinceMergeStatus.MERGED)
    ).associateBy { it.id }

    fun findById(rawId: String): VietnamProvinceData? {
        val cleanId = rawId.lowercase().replace("svg_", "").trim()
        return provinces[cleanId] ?: provinces.values.firstOrNull {
            it.name.equals(rawId, ignoreCase = true) || it.svgId.equals(rawId, ignoreCase = true)
        }
    }
}

// --------------------------------------------------------------------------
// Lớp chuyển tiếp tương thích ngược (Backward-Compatibility Wrapper)
// --------------------------------------------------------------------------

enum class RegionType(val displayName: String, val colorHex: Long) {
    BAC_BO("Miền Bắc", 0xFF8A1E1E),
    TRUNG_BO("Miền Trung", 0xFFC98A2C),
    NAM_BO("Miền Nam", 0xFF2A6F54),
    TAY_NGUYEN("Tây Nguyên", 0xFF7D4E2D),
    UNKNOWN("Toàn Quốc", 0xFF5C5C5C)
}

data class ProvinceHeritage(
    val id: String,
    val name: String,
    val region: RegionType = RegionType.UNKNOWN,
    val hasHeritageData: Boolean = false,
    val costumeTitle: String? = null,
    val costumePeriod: String? = null,
    val summary: String? = null,
    val fabrics: List<String> = emptyList(),
    val patterns: List<String> = emptyList(),
    val culturalSignificance: String? = null,
    val relatedArticleId: String? = null,
    val dressCodeAdvice: String? = null,
    val provinceData: VietnamProvinceData? = null,
    val masterItem: MasterProvinceItem? = null
)

object ProvinceHeritageRepository {
    fun getHeritageForProvince(rawId: String, rawName: String?): ProvinceHeritage {
        val prov = VietnamMapRegistry.findById(rawId) ?: rawName?.let { VietnamMapRegistry.findById(it) }
        val displayName = prov?.name ?: rawName ?: rawId
        return ProvinceHeritage(
            id = prov?.id ?: rawId,
            name = displayName,
            region = RegionType.UNKNOWN,
            hasHeritageData = true,
            provinceData = prov
        )
    }
}
