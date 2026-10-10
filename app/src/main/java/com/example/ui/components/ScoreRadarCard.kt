package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LookScore
import com.example.ui.theme.*

@Composable
fun ScoreRadarCard(
    score: LookScore,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderCream, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Total Score & Cultural Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Điểm tổng thể phong cách",
                        fontSize = 12.sp,
                        color = CharcoalSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${score.totalScore}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                        Text(
                            text = "/100",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = CharcoalMuted,
                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        score.culturalStatus.contains("Nét Gốc") -> VermilionRed.copy(alpha = 0.15f)
                        score.culturalStatus.contains("Giao Thoa") -> JadeTeal.copy(alpha = 0.15f)
                        else -> ImperialGold.copy(alpha = 0.2f)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            score.culturalStatus.contains("Nét Gốc") -> VermilionRed
                            score.culturalStatus.contains("Giao Thoa") -> JadeTeal
                            else -> ImperialGold
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        score.culturalStatus.contains("Nét Gốc") -> VermilionRed
                                        score.culturalStatus.contains("Giao Thoa") -> JadeTeal
                                        else -> ImperialGold
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = score.culturalStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                score.culturalStatus.contains("Nét Gốc") -> VermilionRed
                                score.culturalStatus.contains("Giao Thoa") -> JadeTeal
                                else -> ImperialGold
                            },
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderCream)
            Spacer(modifier = Modifier.height(12.dp))

            // 4 Algorithm Metrics
            ScoreMetricBar(
                title = "Phù hợp sự kiện (Event Match)",
                weightLabel = "40%",
                score = score.eventMatch,
                barColor = JadeTeal
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreMetricBar(
                title = "Hài hòa màu sắc (Color Harmony)",
                weightLabel = "30%",
                score = score.colorHarmony,
                barColor = ImperialGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreMetricBar(
                title = "Độ cá tính phong cách (Style Match)",
                weightLabel = "20%",
                score = score.personalStyle,
                barColor = VermilionRed
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreMetricBar(
                title = "Tính tiện dụng & thoải mái (Practicality)",
                weightLabel = "10%",
                score = score.practicality,
                barColor = RoyalNavy
            )
        }
    }
}

@Composable
private fun ScoreMetricBar(
    title: String,
    weightLabel: String,
    score: Int,
    barColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CharcoalPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($weightLabel)",
                    fontSize = 10.sp,
                    color = CharcoalMuted
                )
            }
            Text(
                text = "$score",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = PureWhite
        )
    }
}
