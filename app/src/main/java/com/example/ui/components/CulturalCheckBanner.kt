package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CulturalCheckResult
import com.example.data.model.CulturalCheckSeverity
import com.example.ui.theme.*

@Composable
fun CulturalCheckBanner(
    checkResult: CulturalCheckResult,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // 4 Trạng thái theo tài liệu mới: ✓ Phù hợp → ! Lưu ý → ⚠ Cảnh báo mạnh → ? Cần kiểm chứng
    val (symbol, tintColor, bgColor, borderColor) = when (checkResult.severity) {
        CulturalCheckSeverity.FIT -> Quad(
            "✓",
            CulturalFitGreen,
            CulturalFitGreenBg,
            CulturalFitGreen.copy(alpha = 0.35f)
        )
        CulturalCheckSeverity.CAUTION -> Quad(
            "!",
            ContemporaryYellow,
            ContemporaryYellowBg,
            ContemporaryYellow.copy(alpha = 0.35f)
        )
        CulturalCheckSeverity.STRONG_WARNING -> Quad(
            "⚠",
            RecheckRed,
            RecheckRedBg,
            RecheckRed.copy(alpha = 0.45f)
        )
        CulturalCheckSeverity.NEEDS_VERIFICATION -> Quad(
            "?",
            Color(0xFF5E35B1), // Deep Purple
            Color(0xFFEDE7F6),
            Color(0xFF5E35B1).copy(alpha = 0.35f)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Distinctive status badge circle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tintColor,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = symbol,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cultural Check · ${checkResult.severity.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = tintColor,
                                modifier = Modifier.weight(1f, fill = false),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = tintColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = checkResult.labelBadge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tintColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        Text(
                            text = checkResult.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Xem chi tiết quy tắc",
                        tint = tintColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = checkResult.explanation,
                fontSize = 13.sp,
                color = CharcoalPrimary,
                lineHeight = 18.sp
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(
                        color = borderColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Audit & Governance metadata (theo đặc tả trang 3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mã Rule: ${checkResult.sourceId}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalMuted
                        )
                        Text(
                            text = "Độ tin cậy: ${checkResult.confidence}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tintColor
                        )
                    }
                    Text(
                        text = "Kiểm duyệt: ${checkResult.reviewStatus} • Bởi: ${checkResult.reviewer}",
                        fontSize = 10.sp,
                        color = CharcoalMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cơ sở văn hóa & Pháp lý di sản:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = checkResult.academicNote,
                        fontSize = 12.sp,
                        color = CharcoalSecondary,
                        lineHeight = 17.sp
                    )

                    if (checkResult.citations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nguồn: ${checkResult.citations.joinToString(" • ")}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = tintColor
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
