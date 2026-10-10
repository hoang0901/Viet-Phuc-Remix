package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.*
import com.example.data.model.ActiveOutfit
import com.example.data.model.AvatarAngle
import com.example.data.model.AvatarGender
import com.example.data.model.CulturalMode
import com.example.data.repository.CulturalRuleEngine
import com.example.ui.components.AvatarCanvas
import com.example.ui.components.CulturalCheckBanner
import com.example.ui.components.ScoreRadarCard
import com.example.ui.theme.*

@Composable
fun LookCardModal(
    outfit: ActiveOutfit,
    onDismiss: () -> Unit,
    onSaveLook: () -> Unit,
    onCompare: () -> Unit,
    onReadStory: () -> Unit,
    onShare: () -> Unit
) {
    var currentAngle by remember { mutableStateOf(outfit.viewAngle) }
    var currentGender by remember { mutableStateOf(outfit.gender) }
    var currentHeight by remember { mutableIntStateOf(outfit.heightCm) }
    var currentWeight by remember { mutableIntStateOf(outfit.weightKg) }
    val displayOutfit = remember(outfit, currentAngle, currentGender, currentHeight, currentWeight) {
        outfit.copy(
            viewAngle = currentAngle,
            gender = currentGender,
            heightCm = currentHeight,
            weightKg = currentWeight
        )
    }
    val (checkResult, score) = CulturalRuleEngine.evaluate(displayOutfit)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, BorderCream, RoundedCornerShape(24.dp)),
            color = IvoryBase
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top close & share bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CreamSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = CharcoalPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CreamSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Chia sẻ",
                                tint = CharcoalPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onCompare,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CreamSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = "So sánh",
                                tint = CharcoalPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Look Title & Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = outfit.name.uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${outfit.mainGarment.name} · ${outfit.bottom.name} · ${outfit.footwear.name}",
                        fontSize = 13.sp,
                        color = CharcoalSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Mode Tag and Remix dots
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (outfit.mode) {
                                CulturalMode.HERITAGE -> VermilionRed
                                CulturalMode.FUSION -> JadeTeal
                                CulturalMode.REMIX_GEN_Z -> ImperialGold
                            }
                        ) {
                            Text(
                                text = outfit.mode.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "Mức remix: ${
                                when (outfit.mode) {
                                    CulturalMode.HERITAGE -> "●○○"
                                    CulturalMode.FUSION -> "●●○"
                                    CulturalMode.REMIX_GEN_Z -> "●●●"
                                }
                            }",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Canvas in Card with rotation and gender support
                AvatarCanvas(
                    outfit = displayOutfit,
                    onRotateAngle = {
                        currentAngle = when (currentAngle) {
                            AvatarAngle.FRONT -> AvatarAngle.THREE_QUARTER
                            AvatarAngle.THREE_QUARTER -> AvatarAngle.BACK
                            AvatarAngle.BACK -> AvatarAngle.FRONT
                        }
                    },
                    onToggleGender = {
                        currentGender = if (currentGender == AvatarGender.FEMALE) AvatarGender.MALE else AvatarGender.FEMALE
                    },
                    onMetricsChanged = { newH, newW ->
                        currentHeight = newH
                        currentWeight = newW
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Vóc dáng & Quy chuẩn Thước Tấc Cổ Truyền
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📏 Vóc dáng 3D: ${displayOutfit.heightCm}cm • ${displayOutfit.weightKg}kg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VermilionRed
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ImperialGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = displayOutfit.bodyTypeDesc,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB57C1E),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📐 ${displayOutfit.traditionalMeasurements}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CharcoalPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Card with 4 metrics & Cultural Status
                ScoreRadarCard(score = score)

                Spacer(modifier = Modifier.height(14.dp))

                // Cultural Check Notice
                CulturalCheckBanner(checkResult = checkResult)

                Spacer(modifier = Modifier.height(14.dp))

                // Micro Story card (as seen on page 9 of PDF)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderCream)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = JadeTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tri thức văn hóa ngắn (60s):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = JadeTeal
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“${outfit.mainGarment.shortStory}”",
                            fontSize = 13.sp,
                            color = CharcoalPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = onReadStory,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "[Xem toàn bộ câu chuyện di sản]",
                                color = VermilionRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions row: Save look & Compare
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onCompare,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CharcoalPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = CharcoalPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("So sánh", color = CharcoalPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSaveLook,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = null,
                            tint = PureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu Look", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
