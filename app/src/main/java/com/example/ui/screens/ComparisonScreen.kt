package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveOutfit
import com.example.data.model.CulturalMode
import com.example.data.repository.CulturalRuleEngine
import com.example.ui.components.AvatarCanvas
import com.example.ui.theme.*

@Composable
fun ComparisonScreen(
    lookA: ActiveOutfit,
    lookB: ActiveOutfit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (_, scoreA) = CulturalRuleEngine.evaluate(lookA)
    val (_, scoreB) = CulturalRuleEngine.evaluate(lookB)

    // Calculate modernity percentage for each look
    val modernScoreA = when (lookA.mode) {
        CulturalMode.HERITAGE -> 45
        CulturalMode.FUSION -> 72
        CulturalMode.REMIX_GEN_Z -> 90
    }
    val modernScoreB = when (lookB.mode) {
        CulturalMode.HERITAGE -> 45
        CulturalMode.FUSION -> 72
        CulturalMode.REMIX_GEN_Z -> 90
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryBase)
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = CharcoalPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "So Sánh Hai Outfit",
                    style = MaterialTheme.typography.titleLarge,
                    color = CharcoalPrimary
                )
                Text(
                    text = "Đánh giá mức độ phù hợp & tinh thần văn hóa",
                    fontSize = 11.sp,
                    color = CharcoalMuted
                )
            }
        }

        HorizontalDivider(color = BorderCream)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Split-screen Row: Look A and Look B
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // LOOK A COLUMN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CreamSurface)
                        .border(1.dp, BorderCream, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = VermilionRed
                    ) {
                        Text(
                            text = "LOOK A",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lookA.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${lookA.mainGarment.name} · ${lookA.primaryColor.name}",
                        fontSize = 10.sp,
                        color = CharcoalMuted,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AvatarCanvas(
                        outfit = lookA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = scoreA.culturalStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VermilionRed
                    )
                }

                // LOOK B COLUMN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CreamSurface)
                        .border(1.dp, BorderCream, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = JadeTeal
                    ) {
                        Text(
                            text = "LOOK B",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lookB.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${lookB.mainGarment.name} · ${lookB.primaryColor.name}",
                        fontSize = 10.sp,
                        color = CharcoalMuted,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AvatarCanvas(
                        outfit = lookB,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = scoreB.culturalStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = JadeTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Comparison Metrics Table (From page 10 of PDF)
            Text(
                text = "Bảng đối chiếu thông số",
                style = MaterialTheme.typography.titleMedium,
                color = CharcoalPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderCream, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ComparisonMetricRow(
                        label = "Phù hợp sự kiện (Event)",
                        valA = scoreA.eventMatch,
                        valB = scoreB.eventMatch
                    )
                    HorizontalDivider(color = BorderCream, modifier = Modifier.padding(vertical = 8.dp))

                    ComparisonMetricRow(
                        label = "Hài hòa màu sắc (Color)",
                        valA = scoreA.colorHarmony,
                        valB = scoreB.colorHarmony
                    )
                    HorizontalDivider(color = BorderCream, modifier = Modifier.padding(vertical = 8.dp))

                    ComparisonMetricRow(
                        label = "Cá tính phong cách (Style)",
                        valA = scoreA.personalStyle,
                        valB = scoreB.personalStyle
                    )
                    HorizontalDivider(color = BorderCream, modifier = Modifier.padding(vertical = 8.dp))

                    ComparisonMetricRow(
                        label = "Mức độ hiện đại (Modernity)",
                        valA = modernScoreA,
                        valB = modernScoreB
                    )
                    HorizontalDivider(color = BorderCream, modifier = Modifier.padding(vertical = 8.dp))

                    ComparisonMetricRow(
                        label = "Điểm tổng thể thuật toán",
                        valA = scoreA.totalScore,
                        valB = scoreB.totalScore,
                        isBold = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Objective Context Advice (Strictly adherence to PDF page 10 principle)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImperialGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardHighlight),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ImperialGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gợi ý lựa chọn từ Cố vấn Văn hóa:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (lookA.mode == CulturalMode.HERITAGE) {
                            "Look A giữ tinh thần truyền thống và cấu trúc cổ phong rõ nét hơn, rất phù hợp cho nghi lễ trang trọng, cúng gia tiên hoặc lễ hội cố đô. Look B phù hợp hơn nếu bạn muốn phong cách thanh xuân hiện đại, nhẹ nhàng và thoải mái vận động."
                        } else {
                            "Cả hai look đều mang dấu ấn giao thoa đương đại đẹp mắt. Hãy chọn Look A nếu bạn muốn điểm nhấn thị giác ấm cúng, hoặc chọn Look B để có sự tươi mới, trẻ trung hơn."
                        },
                        fontSize = 12.sp,
                        color = CharcoalSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Lưu ý nguyên tắc: Ứng dụng đánh giá mức độ phù hợp với từng bối cảnh cụ thể, không so sánh hay phán xét vẻ đẹp chủ quan.",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = CharcoalMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonMetricRow(
    label: String,
    valA: Int,
    valB: Int,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$valA",
            fontSize = if (isBold) 16.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = VermilionRed,
            modifier = Modifier.width(36.dp)
        )

        Text(
            text = label,
            fontSize = if (isBold) 13.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = CharcoalPrimary,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = "$valB",
            fontSize = if (isBold) 16.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = JadeTeal,
            modifier = Modifier.width(36.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
