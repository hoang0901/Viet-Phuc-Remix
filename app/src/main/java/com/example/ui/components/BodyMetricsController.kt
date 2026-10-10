package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveOutfit
import com.example.ui.theme.*
import kotlin.math.roundToInt

data class BodyPreset(
    val title: String,
    val heightCm: Int,
    val weightKg: Int,
    val note: String
)

val defaultBodyPresets = listOf(
    BodyPreset("Nữ Chuẩn", 162, 50, "Cân đối"),
    BodyPreset("Nữ Nhỏ Nhắn", 155, 45, "Thanh mảnh"),
    BodyPreset("Nam Đĩnh Đạc", 172, 64, "Cân đối"),
    BodyPreset("Nam Cao Ráo", 178, 72, "Vạm vỡ"),
    BodyPreset("Đầy Đặn", 165, 66, "Phom suông")
)

@Composable
fun BodyMetricsController(
    outfit: ActiveOutfit,
    onMetricsChanged: (heightCm: Int, weightKg: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderCream, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CreamSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Title & Current metrics summary pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = VermilionRed.copy(alpha = 0.12f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccessibilityNew,
                                contentDescription = null,
                                tint = VermilionRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vóc Dáng & Số Đo 3D",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = "${outfit.heightCm} cm • ${outfit.weightKg} kg (BMI: ${(outfit.bmi * 10f).roundToInt() / 10f})",
                            fontSize = 11.sp,
                            color = VermilionRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            outfit.bmi < 18.5f -> RoyalNavy.copy(alpha = 0.12f)
                            outfit.bmi < 23.0f -> JadeTeal.copy(alpha = 0.15f)
                            outfit.bmi < 26.0f -> ImperialGold.copy(alpha = 0.2f)
                            else -> VermilionRed.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = outfit.bodyTypeDesc,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                outfit.bmi < 18.5f -> RoyalNavy
                                outfit.bmi < 23.0f -> JadeTeal
                                outfit.bmi < 26.0f -> Color(0xFFB57C1E)
                                else -> VermilionRed
                            },
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Mở rộng tùy chỉnh số đo",
                            tint = CharcoalSecondary
                        )
                    }
                }
            }

            // Quick Presets Row (Always accessible)
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(defaultBodyPresets) { preset ->
                    val isSelected = outfit.heightCm == preset.heightCm && outfit.weightKg == preset.weightKg
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) VermilionRed else PureWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VermilionRed else BorderCream
                        ),
                        modifier = Modifier.clickable {
                            onMetricsChanged(preset.heightCm, preset.weightKg)
                        }
                    ) {
                        Text(
                            text = "${preset.title} (${preset.heightCm}cm)",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PureWhite else CharcoalPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Detailed Sliders and Fit Advice (Expanded)
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                    HorizontalDivider(color = BorderCream, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Height Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chiều cao",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = "${outfit.heightCm} cm",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                    }
                    Slider(
                        value = outfit.heightCm.toFloat(),
                        onValueChange = { newH ->
                            onMetricsChanged(newH.roundToInt(), outfit.weightKg)
                        },
                        valueRange = 150f..190f,
                        steps = 39,
                        colors = SliderDefaults.colors(
                            thumbColor = VermilionRed,
                            activeTrackColor = VermilionRed,
                            inactiveTrackColor = BorderCream
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Weight Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cân nặng",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalPrimary
                        )
                        Text(
                            text = "${outfit.weightKg} kg",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                    }
                    Slider(
                        value = outfit.weightKg.toFloat(),
                        onValueChange = { newW ->
                            onMetricsChanged(outfit.heightCm, newW.roundToInt())
                        },
                        valueRange = 40f..90f,
                        steps = 49,
                        colors = SliderDefaults.colors(
                            thumbColor = VermilionRed,
                            activeTrackColor = VermilionRed,
                            inactiveTrackColor = BorderCream
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Intelligent Fit Advice Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PureWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = JadeTeal,
                                modifier = Modifier.size(16.dp).padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tư vấn phom dáng di sản:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JadeTeal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = outfit.fitAdvice,
                                    fontSize = 11.sp,
                                    color = CharcoalSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thước Tấc Cổ Truyền (Triều Nguyễn)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ImperialGold.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                tint = Color(0xFFB57C1E),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Quy chuẩn Thước Tấc Cổ Truyền (1 tấc = 4cm):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB57C1E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = outfit.traditionalMeasurements,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CharcoalPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
