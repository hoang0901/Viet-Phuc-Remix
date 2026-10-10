package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MasterProvinceItem

/**
 * Bảng chi tiết Văn Hóa & Trang Phục truyền thống của Tỉnh/Thành phố
 * được chọn từ bản đồ Việt Nam.
 *
 * Đọc trực tiếp từ kho dữ liệu tập trung `provinces_master.json`.
 * Sắp xếp đầy đủ các mục:
 * A. TỔNG QUAN ĐỊA PHƯƠNG
 * B. TRANG PHỤC TRUYỀN THỐNG (Đặc điểm, Ý nghĩa, Lịch sử, Chất liệu & Phụ kiện)
 * C. GIÁ TRỊ VĂN HÓA & KHÔNG GIAN DI SẢN
 * D. HÌNH ẢNH & MINH HỌA MỸ THUẬT VIỆT PHỤC
 */
@Composable
fun ProvinceHeritageDetailSheet(
    province: MasterProvinceItem,
    onClose: () -> Unit,
    onNavigateToOutfit: ((String) -> Unit)? = null,
    onNavigateToArticle: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }

    // Bảng màu di sản theo vùng miền
    val (regionPrimaryColor, regionBgColor, regionIcon) = when (province.regionId) {
        "bac_bo" -> Triple(Color(0xFFDC2626), Color(0xFF450A0A), Icons.Default.TempleHindu)
        "trung_bo" -> Triple(Color(0xFFF59E0B), Color(0xFF451A03), Icons.Default.Castle)
        "nam_bo" -> Triple(Color(0xFF10B981), Color(0xFF064E3B), Icons.Default.Waves)
        "tay_nguyen" -> Triple(Color(0xFFD97706), Color(0xFF3F2305), Icons.Default.Terrain)
        else -> Triple(Color(0xFF60A5FA), Color(0xFF0F172A), Icons.Default.Place)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = if (isExpanded) 480.dp else 230.dp)
            .testTag("province_detail_sheet"),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFF0B132B).copy(alpha = 0.98f),
        border = BorderStroke(1.2.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Drag Handle Bar (Bấm vào để mở rộng / thu gọn)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF64748B))
                    .clickable { isExpanded = !isExpanded }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // HEADER: TÊN ĐỊA PHƯƠNG & HUY HIỆU VÙNG MIỀN
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = regionBgColor,
                        border = BorderStroke(1.5.dp, regionPrimaryColor),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = regionIcon,
                                contentDescription = null,
                                tint = regionPrimaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = province.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Vùng miền
                        Text(
                            text = province.regionName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = regionPrimaryColor
                        )
                    }
                }

                // Controls: Nút thu gọn/mở rộng & Nút đóng
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isExpanded) "Thu gọn chi tiết" else "Mở rộng chi tiết",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("close_province_sheet_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng chi tiết tỉnh",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nội dung có thể cuộn
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState)
            ) {
                // ==========================================
                // D. KHỐI MINH HỌA MỸ THUẬT & TIÊU ĐỀ DI SẢN
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, regionPrimaryColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        regionBgColor,
                                        Color(0xFF1E293B)
                                    )
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ĐẶC TRƯNG DI SẢN ĐỊA PHƯƠNG",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = province.heritageTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Tên trang phục tiêu biểu dạng Highlight Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A).copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, Color(0xFFC98A3C))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Checkroom,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE68A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = province.costumeName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE68A)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // B. CHI TIẾT TRANG PHỤC TRUYỀN THỐNG
                // ==========================================
                SectionHeader(
                    icon = Icons.Default.Style,
                    title = "TRANG PHỤC TRUYỀN THỐNG",
                    tint = Color(0xFF60A5FA)
                )

                Spacer(modifier = Modifier.height(8.dp))

                HeritageDetailCard(
                    title = "Đặc Điểm Nhận Diện",
                    content = province.identificationFeatures,
                    icon = Icons.Default.Visibility,
                    tint = Color(0xFF93C5FD)
                )

                Spacer(modifier = Modifier.height(8.dp))

                HeritageDetailCard(
                    title = "Chất Liệu & Phụ Kiện Đặc Trưng",
                    content = province.materialAccessories,
                    icon = Icons.Default.Texture,
                    tint = Color(0xFFFBBF24)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // C. GIÁ TRỊ VĂN HÓA & BỐI CẢNH LỊCH SỬ
                // ==========================================
                SectionHeader(
                    icon = Icons.Default.AccountBalance,
                    title = "GIÁ TRỊ VĂN HÓA & LỊCH SỬ",
                    tint = Color(0xFF34D399)
                )

                Spacer(modifier = Modifier.height(8.dp))

                HeritageDetailCard(
                    title = "Ý Nghĩa Văn Hóa & Triết Lý",
                    content = province.cultureSignificance,
                    icon = Icons.Default.Psychology,
                    tint = Color(0xFF6EE7B7)
                )

                Spacer(modifier = Modifier.height(8.dp))

                HeritageDetailCard(
                    title = "Bối Cảnh Lịch Sử & Không Gian Di Sản",
                    content = province.historicalContext,
                    icon = Icons.Default.HistoryEdu,
                    tint = Color(0xFFF472B6)
                )

                // ==========================================
                // 4. TRƯỜNG HỢP ĐANG BỔ SUNG TƯ LIỆU
                // ==========================================
                if (!province.hasFullHeritageDetails) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nội dung văn hóa và trang phục đặc trưng đang được Trung tâm Khảo cứu tiếp tục cập nhật thêm tư liệu.",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Thông tin trích xuất vector SVG
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mã SVG: ${province.svgId} • Tọa độ nhãn: (${province.labelX.toInt()}, ${province.labelY.toInt()})",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Nguồn: vietnam-34-provinces.svg",
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // FOOTER ACTIONS: THỬ TRANG PHỤC & ĐÓNG
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nút chuyển sang Studio thử đồ (nếu có liên kết)
                if (province.costumeId != null && onNavigateToOutfit != null) {
                    Button(
                        onClick = { onNavigateToOutfit(province.costumeId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2D2C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("try_province_costume_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checkroom,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Thử đồ Studio",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Nút xem bài viết văn hóa liên quan
                if (onNavigateToArticle != null) {
                    OutlinedButton(
                        onClick = {
                            val articleTag = when (province.costumeId) {
                                "ao_tu_than" -> "ao_tu_than_kinh_bac"
                                "ao_ngu_than_tay_thung", "ao_ngu_than_tay_chen" -> "ao_ngu_than_hue"
                                else -> "ao_dai_tien_trinh"
                            }
                            onNavigateToArticle(articleTag)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B)),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("read_province_article_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đọc tư liệu",
                            fontSize = 12.sp,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                // Nút đóng / quay lại bản đồ
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("deselect_province_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Đóng",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = tint,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun HeritageDetailCard(
    title: String,
    content: String,
    icon: ImageVector,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 17.sp
            )
        }
    }
}
